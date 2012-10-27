package edu.nyu.cess.remote.server;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import edu.nyu.cess.remote.common.app.ApplicationInfo;
import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.StartedState;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.app.StopedState;

public class Server implements ClientProxyObserver {

	// TODO: after texting set protected fields to private

	private final ClientProxy clientProxy;

	protected final LiteClients liteClients;

	protected final ServerView view;

	private ApplicationInfo applicationInfo;

	protected String applicationNames[];

	private HashMap<String, HashMap<String, String>> applicationsInfo;

	/**
	 * The Server constructor initializes instance variables. Adds itself as a
	 * clientProxy observer -- for monitoring network activity, and adds
	 * relevant application information to the applicationInfo HashMap.
	 */
	public Server() {
		liteClients = new LiteClients();

		view = new ServerView(this);

		clientProxy = new ClientProxy();
		clientProxy.addObserver(this);
	}

	/**
	 * Initializes the Server by adding itself to the {@link LiteClients}
	 * observer list, invoking the UI, and initializing the clientProxy which
	 * handles network communication between the server and clients.
	 */
	public void init() {
		applicationInfo = new ApplicationInfo();
		applicationInfo.readFromFile(new File("application_info.txt"));

		applicationNames = applicationInfo.getApplicationNames();
		applicationsInfo = applicationInfo.getAllApplicationsInformation();

		liteClients.addObserver(view);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.buildGUI();
				view.setVisible(true);
			}
		});

		clientProxy.connectionRequestHandler();
	}

	public String[] getSortedHostNames() {
		String empty[] = new String[0];
		return (liteClients.getSortedHostNames() == null) ? empty : liteClients.getSortedHostNames();
	}

	public void startApplicationInRange(String applicationSelected, String clientLowerBound, String clientUpperBound) {

		Thread startApplicationInRange = new Thread(new StartApplicationInRangeRunnable(applicationSelected,
				clientLowerBound, clientUpperBound));
		startApplicationInRange.start();
	}

	/**
	 * Called by the {@link ClientProxy} when a new client connection is
	 * established.
	 */
	public void newClientUpdate(String ipAddress) {
		liteClients.put(ipAddress, new LiteClient(ipAddress));
		System.out.println("liteClient " + ipAddress + " was added to liteClients");
	}

	/**
	 * Called by the {@link ClientProxy} when applicationState update has been
	 * received from a client.
	 */
	public void applicationStateUpdate(String ipAddress, State applicationState) {
		liteClients.updateState(applicationState, ipAddress);
	}

	/**
	 * Called by the {@link ClientProxy} when a clients network status has
	 * changed.
	 */
	public void networkStatusUpdate(String ipAddress, boolean isConnected) {
		if (isConnected == false) {
			System.out.println(ipAddress + " has disconnected, and has been removed from the client list");
			liteClients.remove(ipAddress);
		}
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
	public synchronized void startApplication(String applicationName, String ipAddress) {
		StartedState startState = new StartedState();

		HashMap<String, String> appInfo = applicationsInfo.get(applicationName);
		ExecutionRequest executionRequest = new ExecutionRequest(appInfo.get("file_name"), appInfo.get("path"), appInfo
				.get("args"), startState);
		clientProxy.startApplicationOnClient(executionRequest, ipAddress);
	}

	public synchronized void messageClientInRange(String message, String lowerBoundHostName, String upperBoundHostName) {
		boolean inRange = false;

		String[] sortedHostNames = getSortedHostNames();

		if (sortedHostNames != null) {
			for (int i = 0; i < sortedHostNames.length; ++i) {

				if ((sortedHostNames[i]).equals(lowerBoundHostName) || (sortedHostNames[i]).equals(upperBoundHostName)) {
					inRange = (inRange == false) ? true : false;
				}

				if (inRange || (sortedHostNames[i]).equals(lowerBoundHostName)
						|| (sortedHostNames[i]).equals(upperBoundHostName)) {

					messageClient(message, liteClients.getIPAddressFromHostName(sortedHostNames[i]));
				}

				if (lowerBoundHostName.equals(upperBoundHostName) && (sortedHostNames[i]).equals(lowerBoundHostName)) {
					i = sortedHostNames.length;
				}
			}
		}

	}

	public synchronized void messageClient(String message, String ipAddress) {
		clientProxy.sendMessageToClient(message, ipAddress);
	}

	/**
	 * Request a client at ipAddress to stop the previously executed
	 * application.
	 *
	 * @param ipAddress
	 *            IP Address of the client running the application
	 */
	public synchronized void stopApplication(String ipAddress) {
		ExecutionRequest executionRequest = new ExecutionRequest("", "", "", new StopedState());

		clientProxy.stopApplicationOnClient(executionRequest, ipAddress);
	}

	/**
	 * Retrieves the servers IP Address
	 *
	 * @return The Servers IP Address
	 */
	public String getServerIPAddress() {
		String ipAddress = "";

		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e) {
			System.out.println("Unable to get server IP Address");
		}

		return ipAddress;
	}

	/**
	 * Returns an array of strings containing the names of all of the supported
	 * applications.
	 *
	 * @return Strings containing all supported application names.
	 */
	public String[] getApplicationNames() {
		String empty[] = new String[0];
		return (applicationNames == null) ? empty : applicationNames;
	}

	/**
	 * Returns a {@link LiteClients} which contains a collection of
	 * {@link LiteClient} objects.
	 *
	 * @return
	 */
	public LiteClients getLiteClients() {
		return liteClients;
	}

	private class StartApplicationInRangeRunnable implements Runnable {
		private final String applicationSelected;
		private final String clientLowerBound;
		private final String clientUpperBound;

		public StartApplicationInRangeRunnable(String applicationSelected, String clientLowerBound,
				String clientUpperBound) {
			this.applicationSelected = applicationSelected;
			this.clientLowerBound = clientLowerBound;
			this.clientUpperBound = clientUpperBound;
		}

		public void run() {
			boolean inRange = false;

			String[] sortedHostNames = getSortedHostNames();

			if (sortedHostNames != null) {
				for (int i = 0; i < sortedHostNames.length; ++i) {

					if ((sortedHostNames[i]).equals(clientLowerBound) || (sortedHostNames[i]).equals(clientUpperBound)) {
						inRange = (inRange == false) ? true : false;
					}

					if (inRange || (sortedHostNames[i]).equals(clientLowerBound)
							|| (sortedHostNames[i]).equals(clientUpperBound)) {

						startApplication(applicationSelected, liteClients.getIPAddressFromHostName(sortedHostNames[i]));

						(liteClients.get(liteClients.getIPAddressFromHostName(sortedHostNames[i])))
								.setApplicationName(applicationSelected);
					}

					if (clientLowerBound.equals(clientUpperBound) && (sortedHostNames[i]).equals(clientLowerBound)) {
						i = sortedHostNames.length;
					}

					try {
						Thread.sleep(200);
					}
					catch (InterruptedException e) {}
				}
			}

		}

	}

}
