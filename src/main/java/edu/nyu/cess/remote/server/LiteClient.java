package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.StartedState;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.app.StopedState;

import java.util.Comparator;

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

	public static Comparator<LiteClient> SortByHostname = new Comparator<LiteClient>()
	{
		@Override
		public int compare(LiteClient c1, LiteClient c2)
		{
			// Case 1: C1 is null
			if (c1 == null) {
				if (c2 == null) {
					return 0;
				}
				// C2 is not null, so C1 is less
				else {
					return -1;
				}
			}
			// Case 2: C1 is not null
			else {
				if (c2 == null) {
					return 1;
				}
				else {
					return c1.getHostName().compareTo(c2.getHostName());
				}
			}
		}
	};
}
