package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.StartedState;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.app.StopedState;

public class LiteClient implements Comparable<LiteClient>
{
	private String ipAddress;
	private String hostName;

	private State applicationState;
	private String applicationName;

    /**
     * Sets the IP Address and the hostname to the IP Address.
     *
     * @param ipAddress String
     */
	public LiteClient(String ipAddress) {
		this.ipAddress = ipAddress;
        hostName = ipAddress;

		applicationState = new StopedState();
	}

	public void setApplicationState(State applicationState) {
		this.applicationState = applicationState;
	}

	public State getApplicationState() {
		return this.applicationState;
	}

	public String getIPAddress() {
		return this.ipAddress;
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

    @Override
	public int compareTo(LiteClient liteClient)
	{
        if (liteClient == null) return 1;

        if (liteClient.getHostName() == null) {
            if (hostName == null) {
                return 0;
            }
            else {
                return 1;
            }
        }

        return hostName.compareTo(liteClient.getHostName());
	}
}
