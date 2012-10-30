package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.StartedState;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.app.StopedState;

public class LiteClient {

	private String ipAddress;
	private String hostName;
	private State applicationState;
	private String applicationName;
	private boolean isConnected = true;

	public LiteClient(String ipAddress) {
		this.ipAddress = ipAddress;
		hostName = "";
		isConnected = true;

		applicationState = new StopedState();

	}

	public void setApplicationState(State applicationState) {
		this.applicationState = applicationState;
	}

	public void setIsConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public State getApplicationState() {
		return this.applicationState;
	}

	public String getIPAddress() {
		return this.ipAddress;
	}

	public boolean getIsConnected() {
		return this.isConnected;
	}

	public boolean isApplicationRunning() {
		boolean applicationRunning = false;
		if (applicationState instanceof StartedState) {
			applicationRunning = true;
		}
		return applicationRunning;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationName() {
		return applicationName;
	}
}
