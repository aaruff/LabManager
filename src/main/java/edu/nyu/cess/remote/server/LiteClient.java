package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.ui.NullComparator;

import java.util.Comparator;

public class LiteClient implements Comparable<LiteClient>
{
	private String ipAddress;
	private String hostName;

	private AppState applicationAppState;
	private String applicationName;

	public static final SortByHostname SORT_BY_HOSTNAME = new SortByHostname();
	public static final SortByIp SORT_BY_IP = new SortByIp();

    /**
     * Sets the IP Address and the hostname to the IP Address.
     *
     * @param ipAddress String
     */
	public LiteClient(String ipAddress) {
		this.ipAddress = ipAddress;
        hostName = ipAddress;

		applicationAppState = new StopedState();
	}

	/**
	 * Sets the client's application state.
	 *
	 * @param applicationAppState the application state
     */
	public void setApplicationAppState(AppState applicationAppState) {
		this.applicationAppState = applicationAppState;
	}

	public String getIPAddress() {
		return this.ipAddress;
	}

	/**
	 * Returns true if the application is running, otherwise false is returned.
	 *
	 * @return the applications state
     */
	public boolean isApplicationRunning() {
		boolean applicationRunning = false;
		if (applicationAppState instanceof StartedState) {
			applicationRunning = true;
		}
		return applicationRunning;
	}

	/**
	 * Returns the client's host name.
	 *
	 * @return the host name
     */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the client's host name.
	 *
	 * @param hostName the clients host name
     */
	public void setHostName(String hostName) {
        this.hostName = hostName;
	}

	/**
	 * Sets the client's application state.
	 *
	 * @param applicationName the client's application name
     */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * Returns the current application name if set, otherwise null is returned.
	 *
	 * @return the current application name
     */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Compares this client's hostname to the client parameter, based upon the lexical ordering of the hostname.
	 * Note: A client having a null name, is considered less than one that doesn't. Two null clients are considered
	 * equal.
	 *
	 * @param client the client being compared to.
	 * @return the comparison result: 0 if equal, -1 if less, or 1 if greater
     */
	@Override public int compareTo(LiteClient client)
	{
        if (client.getHostName() == null) {
			return NullComparator.compareNullString(getHostName(), client.getHostName());
        }

        return hostName.compareTo(client.getHostName());
	}

	/**
	 * Provides the sort by host name method.
	 */
	public static class SortByHostname implements Comparator<LiteClient>
	{
		/**
		 * Compares one client to another by their host name.
		 * @param c1 client one
		 * @param c2 client two
         * @return returns 1 if greater, -1 if less, 0 if equal
         */
		@Override public int compare(LiteClient c1, LiteClient c2)
		{
			if (c1.getHostName() == null || c2.getHostName() == null) {
				NullComparator.compareNullString(c1.getHostName(), c2.getHostName());
			}

            return c1.getHostName().compareTo(c2.getHostName());
		}
	}

	/**
	 * Provides the sort by IP address method.
	 */
	public static class SortByIp implements Comparator<LiteClient>
	{
		/**
		 * Compares one client to another by their IP address.
		 * @param c1 client one
		 * @param c2 client two
		 * @return returns 1 if greater, -1 if less, 0 if equal
         */
		@Override public int compare(LiteClient c1, LiteClient c2)
		{
			if (c1.getIPAddress() == null || c2.getIPAddress() == null) {
				NullComparator.compareNullString(c1.getIPAddress(), c2.getIPAddress());
			}

            return c1.getIPAddress().compareTo(c2.getIPAddress());
		}
	}
}
