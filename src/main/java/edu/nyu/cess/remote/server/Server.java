package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;
import edu.nyu.cess.remote.common.app.StartedState;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.app.StopedState;
import edu.nyu.cess.remote.server.app.profile.AppProfile;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server
{
    final static Logger logger = Logger.getLogger(Server.class);

	private final Port port;

	protected final DashboardView dashboardView;

	private Map<String, AppProfile> appProfileMap;

	private ClientPool clientPool;

	public Server(ClientPool clientPool, Map<String, AppProfile> appProfileMap)
	{
		this.clientPool = clientPool;
		this.appProfileMap = appProfileMap;

		dashboardView = new DashboardView(this);
		port = new Port(this);
	}

	/**
	 * Initializes the Server by adding itself to the {@link ClientPool}
	 * observer list, invoking the UI, and initializing the clientProxy which
	 * handles network communication between the server and clients.
     */
	public void init()
    {
		clientPool.addObserver(dashboardView);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dashboardView.buildGUI();
				dashboardView.setVisible(true);
			}
		});

		port.listen(2600);
	}

	public void startAppInRange(String app, String start, String end)
    {
		Thread startInRange = new Thread(new StartAppInRangeRunnable(app, start, end));
		startInRange.start();
	}

	public void stopAppInRange(String start, String end)
    {
		Thread stopInRange = new Thread(new StopAppInRangeRunnable(start, end));
		stopInRange.start();
	}

    /**
     * Adds a client proxy with the provided ip address to the servers collection of active clients.
     * @param ipAddress The clients IP address
     */
	public void addClient(String ipAddress)
    {
		clientPool.addClient(new LiteClient(ipAddress));
	}

	/**
	 * Called by the {@link Port} when applicationState update has been
	 * received from a client.
	 */
	public void updateClientState(String ipAddress, State applicationState)
    {
		clientPool.updateClientState(applicationState, ipAddress);
	}

	/**
     * Removes the client with the corresponding IP address.
	 */
	public void removeClient(String ipAddress)
    {
        logger.debug(ipAddress + " has disconnected, and has been removed from the client list");
		try {
			clientPool.popByIpAddress(ipAddress);
		}
		catch(LiteClientNotFoundException e) {
			logger.error("Client not found.", e);
		}
	}

	public synchronized void messageClient(String message, String ipAddress)
    {
		port.sendMessageToClient(message, ipAddress);
	}

	/**
	 * Prepares an {@link ExeRequestMessage}, which contains the information
	 * needed to execute the chosen application on the client, and passes it to
	 * the clientProxy which will handle sending it over the network to the
	 * appropriate client.
	 *
	 * @param appName
	 *            The name of the application to be executed
	 * @param ipAddress
	 *            The IP Address of the client receiving the application
	 *            execution request
	 */
	public synchronized void startApplication(String appName, String ipAddress)
    {
		StartedState startState = new StartedState();

		AppProfile appProfile = appProfileMap.get(appName);
		ExeRequestMessage exeRequestMessage = new ExeRequestMessage(
				appProfile.getName(), appProfile.getPath(), appProfile.getOptions(), startState);
		port.startApplicationOnClient(exeRequestMessage, ipAddress);
	}


	/**
	 * Request a client at ipAddress to stop the previously executed
	 * application.
	 *
	 * @param ipAddress
	 *            IP Address of the client running the application
	 */
	public synchronized void stopApplication(String ipAddress)
    {
		ExeRequestMessage exeRequestMessage = new ExeRequestMessage("", "", "", new StopedState());

		port.stopApplicationOnClient(exeRequestMessage, ipAddress);
	}


	/**
	 * Returns an array of strings containing the names of all of the supported
	 * applications.
	 *
	 * @return Strings containing all supported application names.
	 */
	public String[] getApplicationNames()
    {
		Set<String> names = appProfileMap.keySet();
		return names.toArray(new String[names.size()]);
	}


	/**
	 * Returns a {@link ClientPool} which contains a collection of
	 * {@link LiteClient} objects.
	 *
	 * @return returns the client collection
	 */
	public ClientPool getClientPool()
    {
		return clientPool;
	}

	public synchronized void messageClientInRange(String message, String lowerBoundHostName, String upperBoundHostName)
    {
		if (lowerBoundHostName.isEmpty() || upperBoundHostName.isEmpty()) {
			logger.error("Either lower or upper bound is empty.");
			return; // Error: Host range not set
		}

		List<LiteClient> sortedLiteClients = clientPool.sort(LiteClient.SORT_BY_HOSTNAME);
		for (LiteClient client : sortedLiteClients) {
			messageClient(message, client.getIPAddress());
		}
	}


	/**
	 * Thread request application execution on a remote client
	 * @author Anwar A. Ruff
	 *
	 */
	private class StartAppInRangeRunnable implements Runnable
    {
		private final String applicationSelected;
		private final String lowerHostname;
		private final String upperHostname;

		public StartAppInRangeRunnable(String applicationSelected, String lowerHostname, String upperHostname) {
			this.applicationSelected = applicationSelected;
			this.lowerHostname = lowerHostname;
			this.upperHostname = upperHostname;
		}

		public void run() {
			if (lowerHostname.isEmpty() || upperHostname.isEmpty()) {
				logger.error("Either lower or upper bound is empty.");
				return; // Error: Host range not set
			}

			List<LiteClient> sortedLiteClients = clientPool.sort(LiteClient.SORT_BY_HOSTNAME);

			for (LiteClient client : sortedLiteClients) {
				String hostName = client.getHostName();
				if (hostName.compareTo(lowerHostname) >= 0 && hostName.compareTo(upperHostname) <= 0) {
					startApplication(applicationSelected, client.getIPAddress());
					client.setApplicationName(applicationSelected);
				}
				/*
				 * Sleep the thread to allow for the network communication to finish, before attempting to contact
				 * the next client.
				 */
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {}
			}
		}
	}


	private class StopAppInRangeRunnable implements Runnable
    {
		private final String lowerHostname;
		private final String upperHostname;

		public StopAppInRangeRunnable(String lowerHostname, String upperHostname) {
			this.lowerHostname = lowerHostname;
			this.upperHostname = upperHostname;
		}

		public void run() {
			if (lowerHostname.isEmpty() || upperHostname.isEmpty()) {
				return; // Error: Host range not set
			}

			List<LiteClient> sortedLiteClients = clientPool.sort(LiteClient.SORT_BY_HOSTNAME);
			for (LiteClient client : sortedLiteClients) {
				String hostName = client.getHostName();
				if (hostName.compareTo(lowerHostname) >= 0 && hostName.compareTo(upperHostname) <= 0) {
					stopApplication(client.getIPAddress());
					// Todo: clear old program name
					//sortedLiteClients[i].setApplicationName(applicationSelected);
				}
				/*
				 * Sleep the thread to allow for the network communication to finish, before attempting to contact
				 * the next client.
				 */
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {}
			}
		}
	}


	public void updateClientHostNameUpdate(String hostName, String ipAddress) {
		try {
			LiteClient client = clientPool.getByIp(ipAddress);
			client.setHostName(hostName);
		}
		catch (LiteClientNotFoundException e) {
			logger.error("Client not found", e);
		}
	}
}
