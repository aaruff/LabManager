package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import edu.nyu.cess.remote.server.ui.NullComparator;

import java.util.Comparator;

public class LiteClient implements Comparable<LiteClient>
{
	private NetworkInformation networkInformation;

	private AppState appState;
	private String applicationName;

	public static final SortByHostname SORT_BY_HOSTNAME = new SortByHostname();
	public static final SortByIp SORT_BY_IP = new SortByIp();

	public LiteClient(NetworkInformation networkInformation) {
		this.networkInformation = networkInformation;

		appState = AppState.STOPPED;
	}

	/**
	 * Sets the client's application state.
	 *
	 * @param appState the application state
     */
	public void setAppState(AppState appState) {
		this.appState = appState;
	}

	public String getIPAddress() {
		return networkInformation.getClientIpAddress();
	}

	/**
	 * Returns true if the application is running, otherwise false is returned.
	 *
	 * @return the applications state
     */
	public boolean isApplicationRunning() {
		return appState == AppState.STARTED;
	}

	/**
	 * Returns the client's host name.
	 *
	 * @return the host name
     */
	public String getHostName()
	{
		return networkInformation.getClientName();
	}

	/**
	 * Sets the client's host name.
	 *
	 * @param hostName the clients host name
     */
	public void setHostName(String hostName)
	{
        this.networkInformation.setClientName(hostName);
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

        return networkInformation.getClientName().compareTo(client.getHostName());
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
