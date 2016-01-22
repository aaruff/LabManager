package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.AppExecution;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import edu.nyu.cess.remote.server.app.AppProfile;
import edu.nyu.cess.remote.server.client.ClientPool;
import edu.nyu.cess.remote.server.client.ClientPoolController;
import edu.nyu.cess.remote.server.client.LiteClient;
import edu.nyu.cess.remote.server.client.LiteClientNotFoundException;
import edu.nyu.cess.remote.server.message.ClientMessageHandler;
import edu.nyu.cess.remote.server.message.MessageHandler;
import edu.nyu.cess.remote.server.net.ClientConnectionObserver;
import edu.nyu.cess.remote.server.ui.DashboardView;
import edu.nyu.cess.remote.server.ui.ViewActionObserver;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server implements ClientConnectionObserver, ViewActionObserver, ClientPoolController
{
    final static Logger logger = Logger.getLogger(Server.class);

	private final MessageHandler messageHandler;

	protected final DashboardView dashboardView;

	private Map<String, AppProfile> appProfileMap;

	private ClientPool clientPool;

	public Server(ClientPool clientPool, Map<String, AppProfile> appProfileMap)
	{
		this.clientPool = clientPool;
		this.appProfileMap = appProfileMap;

		dashboardView = new DashboardView(thisToViewActionObserver(), clientPool, getApplicationNames());
		messageHandler = new ClientMessageHandler(thisToClientConnectionObserver(), thisToClientPoolController());
	}

	/**
	 * Initializes the Server by adding itself to the {@link ClientPool}
	 * observer list, invoking the UI, and initializing the clientProxy which
	 * handles network communication between the server and clients.
     */
	public void start() throws IOException
    {
		clientPool.addLiteClientObserver(dashboardView);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dashboardView.buildGUI();
				dashboardView.setVisible(true);
			}
		});

		messageHandler.initializeMessageHandler();
	}

	/**
	 * {@link ViewActionObserver}
     */
	@Override public void notifyViewObserverStartAppInRangeRequested(String app, String start, String end)
    {
		Thread startInRange = new Thread(new StartAppInRangeRunnable(app, start, end));
		startInRange.start();
	}

	/**
	 * {@link ViewActionObserver}
	 */
	@Override public void notifyStopAppInRangeRequested(String start, String end)
    {
		Thread stopInRange = new Thread(new StopAppInRangeRunnable(start, end));
		stopInRange.start();
	}

	/**
	 * {@link ViewActionObserver}
     */
	@Override public void notifyNewClientConnected(NetworkInformation networkInformation)
    {
		clientPool.addClient(new LiteClient(networkInformation));
	}

	/**
	 * {@link ViewActionObserver}
	 */
	@Override public synchronized void messageClient(String message, String ipAddress)
	{
		messageHandler.handleOutboundMessage(message, ipAddress);
	}

	/**
	 * {@link ViewActionObserver}
	 */
	@Override public synchronized void startApplication(String appName, String ipAddress)
	{
		AppState startState = AppState.STARTED;

		AppProfile appProfile = appProfileMap.get(appName);
		AppExecution appExecution = new AppExecution(
				appProfile.getName(), appProfile.getPath(), appProfile.getOptions(), startState);
		messageHandler.handleOutboundAppExecution(appExecution, ipAddress);
	}

	/**
	 * {@link ViewActionObserver}
	 */
	@Override public synchronized void stopApplication(String ipAddress)
	{
		//TODO: Store AppExecution in the client, and return it when stopping and staring an application
		AppExecution appExecution = new AppExecution("", "", "", AppState.STOPPED);

		messageHandler.handleOutboundAppExecution(appExecution, ipAddress);
	}

	/**
	 * {@link ViewActionObserver}
	 */
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
	 * {@link ClientPoolController}
	 */
	public void updateClientState(String ipAddress, AppExecution appExecution)
    {
		clientPool.updateClientState(appExecution, ipAddress);
	}

	/**
	 * {@link ClientPoolController}
	 */
	public void updateClientHostNameUpdate(String hostName, String ipAddress) {
		try {
			LiteClient client = clientPool.getByIp(ipAddress);
			client.setHostName(hostName);
		}
		catch (LiteClientNotFoundException e) {
			logger.error("Client not found", e);
		}
	}

	/**
	 * {@link ClientPoolController}
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

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */

	/**
	 * Returns an array of strings containing the names of all of the supported
	 * applications.
	 *
	 * @return Strings containing all supported application names.
	 */
	private String[] getApplicationNames()
    {
		Set<String> names = appProfileMap.keySet();
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Thread request application execution on a remote client
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

	private ClientConnectionObserver thisToClientConnectionObserver()
	{
		return this;
	}

	private ClientPoolController thisToClientPoolController()
	{
		return this;
	}

	private ViewActionObserver thisToViewActionObserver()
	{
		return this;
	}
}
