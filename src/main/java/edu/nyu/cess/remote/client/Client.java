package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.config.HostConfigInterface;
import edu.nyu.cess.remote.common.app.*;

import javax.swing.*;

/**
 * The Client essentially manages local application execution requests
 * on behalf of the Server. The client receives status updates from both
 * the Application and the ServerProxy, which manages communication with the
 * Server.
 *
 * @author Anwar A. Ruff
 */
public class Client implements ApplicationObserver, ServerProxyObserver {

	private App app;

	private ServerProxy serverProxy;

	/**
	 * Initialize the client
	 * @param hostConfig host config file
     */
	public void initServerConnection(HostConfigInterface hostConfig) {
		serverProxy = new ServerProxy(hostConfig);
		serverProxy.addServerProxyObserver(this);
		serverProxy.establishPersistentServerConnection();
	}

	/**
	 * Updates the application state
	 * @param applicationState update the application state
     */
	public void applicationUpdate(State applicationState) {

		serverProxy.sendServerApplicationState(applicationState);

		if (applicationState instanceof StartedState) {
			System.out.println("Sending Started State");
		}
		else if (applicationState instanceof StopedState) {
			System.out.println("Sending Stopped State");
		}
	}

	/**
	 * Update the server with the execution request received.
	 * @param exeReq the application execution request
     */
	public void updateServerExecutionRequestReceived(ExeRequestMessage exeReq) {

		State requestedApplicationState = exeReq.getApplicationState();
		System.out.println("Application execution request received from the server.");

		// If a request to start an application has been made...
		if (requestedApplicationState instanceof StartedState) {

			if (app == null) {
				app = new App(exeReq.getName(), exeReq.getPath(), exeReq.getArgs());
				app.addObserver(this);
				app.changeState(requestedApplicationState);
			}
			else if (app.isStopped()) {
				app = new App(exeReq.getName(), exeReq.getPath(), exeReq.getArgs());
				app.addObserver(this);
				app.changeState(requestedApplicationState);
			}
			else if (app.isStarted()) {
				applicationUpdate(requestedApplicationState);
			}
		}
		else if (requestedApplicationState instanceof StopedState) {

			if (app == null) {
				applicationUpdate(requestedApplicationState);
			}
			else if (app.isStarted()) {
				app.changeState(requestedApplicationState);
			}
			else if (app.isStopped()) {
				applicationUpdate(requestedApplicationState);
			}

		}
	}

	/**
	 * Update the observers with the clients connection status.
	 * @param isConnected the connection status
     */
	public void updateNetworkStateChanged(boolean isConnected) {
		if (isConnected && app != null) {
			if (app.isStarted()) {
				System.out.println("notifying server of applications prior state (StartedState).");
				applicationUpdate(new StartedState());
			}
		}
	}

	/**
	 * Passes and invokes the MessageRunnable with the message string.
	 * @param message message status string
     */
	public void updateServerMessageReceived(String message) {
		SwingUtilities.invokeLater(new MessageRunnable(message));
	}

	/**
	 * The message runnable class used to display messages sent from the server.
	 */
	private class MessageRunnable implements Runnable {
		String message;

		public MessageRunnable(String message) {
			this.message = message;
		}

		public void run() {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, message, "Experimenter Notification", JOptionPane.WARNING_MESSAGE);
		}

	}
}
