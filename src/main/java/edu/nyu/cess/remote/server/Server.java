package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.app.AppInfo;
import edu.nyu.cess.remote.server.app.AppInfoCollection;
import edu.nyu.cess.remote.server.client.ClientPool;
import edu.nyu.cess.remote.server.client.ClientPoolController;
import edu.nyu.cess.remote.server.client.LiteClient;
import edu.nyu.cess.remote.server.client.LiteClientNotFoundException;
import edu.nyu.cess.remote.server.net.ClientConnectionObserver;
import edu.nyu.cess.remote.server.gui.LabViewFrame;
import edu.nyu.cess.remote.server.gui.ExecutionRequestObserver;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Server implements ClientConnectionObserver, ExecutionRequestObserver, ClientPoolController
{
    final static Logger logger = Logger.getLogger(Server.class);

	private final MessageObserver messageObserver;

	protected final LabViewFrame labViewFrame;

	private AppInfoCollection appInfoCollection;

	private ClientPool clientPool;

	public Server(ClientPool clientPool, AppInfoCollection appInfoCollection)
	{
		this.clientPool = clientPool;
		this.appInfoCollection = appInfoCollection;

	}

	/**
	 * Initializes the Server by adding itself to the {@link ClientPool}
	 * observer list, invoking the UI, and initializing the clientProxy which
	 * handles network communication between the server and clients.
     *
     * @param port the port the server will listen on
     */
	public void start(int port) throws IOException
    {
		clientPool.addLiteClientObserver(labViewFrame);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				labViewFrame.initialize();
				labViewFrame.setVisible(true);
			}
		});

		messageObserver.initializeMessageHandler(port);
	}

	/**
	 * {@link ExecutionRequestObserver}
     */
	@Override public void notifyViewObserverStartAppInRangeRequested(String app, String start, String end)
    {
		Thread startInRange = new Thread(new StartAppInRangeRunnable(app, start, end));
		startInRange.start();
	}

	/**
	 * {@link ExecutionRequestObserver}
	 */
	@Override public void notifyStopAppInRangeRequested(String start, String end)
    {
		Thread stopInRange = new Thread(new StopAppInRangeRunnable(start, end));
		stopInRange.start();
	}

	/**
	 * {@link ExecutionRequestObserver}
     */
	@Override public void notifyNewClientConnected(NetworkInfo networkInfo)
    {
		clientPool.addClient(new LiteClient(networkInfo));
	}

	/**
	 * {@link ExecutionRequestObserver}
	 */
	@Override public synchronized void messageClient(String message, String ipAddress)
	{
		messageObserver.handleOutboundMessage(message, ipAddress);
	}

	/**
	 * {@link ExecutionRequestObserver}
	 */
	@Override public synchronized void startApplication(String appName, String ipAddress)
	{
		AppState startState = AppState.STARTED;

		AppInfo appInfo = appInfoCollection.get(appName);
		AppExe appExe = new AppExe(
				appInfo.getName(), appInfo.getPath(), appInfo.getOptions(), startState);
		messageObserver.notifyAppExecution(appExe, ipAddress);
	}

	/**
	 * {@link ExecutionRequestObserver}
	 */
	@Override public synchronized void stopApplication(String ipAddress)
	{
		//TODO: Store AppExecution in the client, and return it when stopping and staring an application
		AppExe appExe = new AppExe("", "", "", AppState.STOPPED);

		messageObserver.notifyAppExecution(appExe, ipAddress);
	}

	/**
	 * {@link ExecutionRequestObserver}
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
	public void updateClientState(String ipAddress, AppExe appExe)
    {
		clientPool.updateClientState(appExe, ipAddress);
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
		Set<String> names = appInfoCollection.keySet();
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

	private ClientConnectionObserver getClientConnectionObserverFrom(Server server)
	{
		return server;
	}

	private ClientPoolController getClientPoolControllerFrom(Server server)
	{
		return server;
	}

	private ExecutionRequestObserver getViewActionObserverFrom(Server server)
	{
		return server;
	}
}
