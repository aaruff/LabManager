package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.*;
import edu.nyu.cess.remote.common.config.HostConfigInterface;

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

	private Application application;

	private ServerProxy serverProxy;

	/**
	 * Attempts to connect to the server with the corresponding host config parameters.
	 */
	public void initServerConnection(HostConfigInterface hostConfig) {
		serverProxy = new ServerProxy(hostConfig);
		serverProxy.addServerProxyObserver(this);
		serverProxy.establishPersistentServerConnection();
	}

	public void applicationUpdate(State applicationState) {

		serverProxy.sendServerApplicationState(applicationState);

		if (applicationState instanceof StartedState) {
			System.out.println("Sending Started State");
		}
		else if (applicationState instanceof StopedState) {
			System.out.println("Sending Stopped State");
		}
	}

	public void updateServerExecutionRequestReceived(ExecutionRequest exeReq) {

		State requestedApplicationState = exeReq.getApplicationState();
		System.out.println("Application execution request received from the server.");

		// If a request to start an application has been made...
		if (requestedApplicationState instanceof StartedState) {

			if (application == null) {
				application = new Application(exeReq.getName(), exeReq.getPath(), exeReq.getArgs());
				application.addObserver(this);
				application.changeState(requestedApplicationState);
			}
			else if (application.isStopped()) {
				application = new Application(exeReq.getName(), exeReq.getPath(), exeReq.getArgs());
				application.addObserver(this);
				application.changeState(requestedApplicationState);
			}
			else if (application.isStarted()) {
				applicationUpdate(requestedApplicationState);
			}
		}
		else if (requestedApplicationState instanceof StopedState) {

			if (application == null) {
				applicationUpdate(requestedApplicationState);
			}
			else if (application.isStarted()) {
				application.changeState(requestedApplicationState);
			}
			else if (application.isStopped()) {
				applicationUpdate(requestedApplicationState);
			}

		}
	}

	public void updateNetworkStateChanged(boolean isConnected) {
		if (isConnected && application != null) {
			if (application.isStarted()) {
				System.out.println("notifying server of applications prior state (StartedState).");
				applicationUpdate(new StartedState());
			}
		}
	}

	public void updateServerMessageReceived(String message) {
		SwingUtilities.invokeLater(new MessageRunnable(message));
	}

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
