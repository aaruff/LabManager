package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

public class BotMaster
{
    final static Logger logger = Logger.getLogger(BotMaster.class);

	private final Port port;

	protected final RobotPool robotPool = new RobotPool();

	protected final DashboardView dashboardView;

	protected String applicationNames[];

	private HashMap<String, HashMap<String, String>> applicationsInfo;

	public BotMaster()
	{
		dashboardView = new DashboardView(this);
		port = new Port(this);
	}

	/**
	 * Initializes the Server by adding itself to the {@link RobotPool}
	 * observer list, invoking the UI, and initializing the clientProxy which
	 * handles network communication between the server and clients.
	 */
	public void init()
    {
		ApplicationInfo applicationInfo = new ApplicationInfo();
		applicationInfo.readFromFile(new File("application_info.txt"));

		applicationNames = applicationInfo.getApplicationNames();
		applicationsInfo = applicationInfo.getAllApplicationsInformation();

		robotPool.addObserver(dashboardView);

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
     * @param ipAddress
     */
	public void addBot(String ipAddress)
    {
		robotPool.put(new LiteClient(ipAddress));
	}

	/**
	 * Called by the {@link Port} when applicationState update has been
	 * received from a client.
	 */
	public void updateClientState(String ipAddress, State applicationState)
    {
		robotPool.updateState(applicationState, ipAddress);
	}

	/**
     * Removes the client with the corresponding IP address.
	 */
	public void removeClient(String ipAddress)
    {
        logger.debug(ipAddress + " has disconnected, and has been removed from the client list");
        robotPool.remove(ipAddress);
	}

	public synchronized void messageClient(String message, String ipAddress)
    {
		port.sendMessageToClient(message, ipAddress);
	}

	/**
	 * Prepares an {@link ExecutionRequest}, which contains the information
	 * needed to execute the chosen application on the client, and passes it to
	 * the clientProxy which will handle sending it over the network to the
	 * appropriate client.
	 *
	 * @param applicationName
	 *            The name of the application to be executed
	 * @param ipAddress
	 *            The IP Address of the client receiving the application
	 *            execution request
	 */
	public synchronized void startApplication(String applicationName, String ipAddress)
    {
		StartedState startState = new StartedState();

		if (applicationName == null || applicationName.isEmpty()) {
            logger.error("No application selected");
			return;
		}
		HashMap<String, String> appInfo = applicationsInfo.get(applicationName);
		ExecutionRequest executionRequest = new ExecutionRequest(appInfo.get("file_name"), appInfo.get("path"), appInfo
				.get("args"), startState);
		port.startApplicationOnClient(executionRequest, ipAddress);
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
		ExecutionRequest executionRequest = new ExecutionRequest("", "", "", new StopedState());

		port.stopApplicationOnClient(executionRequest, ipAddress);
	}

	/**
	 * Returns an array of strings containing the names of all of the supported
	 * applications.
	 *
	 * @return Strings containing all supported application names.
	 */
	public String[] getApplicationNames()
    {
		String empty[] = new String[0];
		return (applicationNames == null) ? empty : applicationNames;
	}

	/**
	 * Returns a {@link RobotPool} which contains a collection of
	 * {@link LiteClient} objects.
	 *
	 * @return
	 */
	public RobotPool getRobotPool()
    {
		return robotPool;
	}

	public synchronized void messageClientInRange(String message, String lowerBoundHostName, String upperBoundHostName)
    {
		if (lowerBoundHostName.isEmpty() || upperBoundHostName.isEmpty()) {
			return; // Error: Host range not set
		}

		LiteClient[] sortedLiteClients = this.robotPool.getSortedLiteClients();
		if (sortedLiteClients.length == 0) {
			return; // Error: No clients connected
		}

		for (int i = 0; i < sortedLiteClients.length; ++i) {
			if (sortedLiteClients[i].getHostName().compareTo(lowerBoundHostName) >= 0
					&& sortedLiteClients[i].getHostName().compareTo(upperBoundHostName) <= 0) {
				messageClient(message, sortedLiteClients[i].getIPAddress());
			}
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
		private final String clientLowerBound;
		private final String clientUpperBound;

		public StartAppInRangeRunnable(String applicationSelected, String clientLowerBound, String clientUpperBound) {
			this.applicationSelected = applicationSelected;
			this.clientLowerBound = clientLowerBound;
			this.clientUpperBound = clientUpperBound;
		}

		public void run() {
			if (clientLowerBound.isEmpty() || clientUpperBound.isEmpty()) {
				return; // Error: Host range not set
			}

			LiteClient[] sortedLiteClients = robotPool.getSortedLiteClients();
			if (sortedLiteClients.length == 0) {
				return; // Error: No clients connected
			}

			for (int i = 0; i < sortedLiteClients.length; ++i) {
				if (sortedLiteClients[i].getHostName().compareTo(clientLowerBound) >= 0
						&& sortedLiteClients[i].getHostName().compareTo(clientUpperBound) <= 0) {
						startApplication(applicationSelected, sortedLiteClients[i].getIPAddress());
						sortedLiteClients[i].setApplicationName(applicationSelected);
				}
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {}
			}
		}
	}

	private class StopAppInRangeRunnable implements Runnable
    {
		private final String clientLowerBound;
		private final String clientUpperBound;

		public StopAppInRangeRunnable(String clientLowerBound, String clientUpperBound) {
			this.clientLowerBound = clientLowerBound;
			this.clientUpperBound = clientUpperBound;
		}

		public void run() {
			if (clientLowerBound.isEmpty() || clientUpperBound.isEmpty()) {
				return; // Error: Host range not set
			}

			LiteClient[] sortedLiteClients = robotPool.getSortedLiteClients();
			if (sortedLiteClients.length == 0) {
				return; // Error: No clients connected
			}

			for (int i = 0; i < sortedLiteClients.length; ++i) {
				if (sortedLiteClients[i].getHostName().compareTo(clientLowerBound) >= 0
						&& sortedLiteClients[i].getHostName().compareTo(clientUpperBound) <= 0) {
						stopApplication(sortedLiteClients[i].getIPAddress());
						// Todo: clear old program name
						//sortedLiteClients[i].setApplicationName(applicationSelected);
				}
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {}
			}
		}
	}


	public void updateClientHostNameUpdate(String hostName, String ipAddress) {
		robotPool.updateHostName(hostName, ipAddress);
	}

}
