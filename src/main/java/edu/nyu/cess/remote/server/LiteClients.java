/**
 *
 */
package edu.nyu.cess.remote.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import edu.nyu.cess.remote.common.app.State;

/**
 * @author Anwar A. Ruff 
 */
public class LiteClients implements LiteClientsObservable {

	HashMap<String, LiteClient> liteClients = new HashMap<String, LiteClient>();

	HashMap<String, String> hostNameToIPAddress = new HashMap<String, String>();
	HashMap<String, String> ipAddressToHostName = new HashMap<String, String>();

	ArrayList<LiteClientsObserver> observers = new ArrayList<LiteClientsObserver>();

	String sortedHostNames[];

	public LiteClients() {
		sortedHostNames = new String[0];
	}

	public LiteClient put(String ipAddress, LiteClient liteClient) {
		
		String hostName;
		try {
			InetAddress addr = InetAddress.getByName(ipAddress);
			hostName = addr.getHostName();
			if (hostName.isEmpty()) {
				hostName = ipAddress;
			}
		} 
		catch (UnknownHostException e) {
			hostName = ipAddress;
		}
			

		liteClient.setHostName(hostName);

		LiteClient tempLiteClient = liteClients.put(ipAddress, liteClient);

		hostNameToIPAddress.put(hostName, ipAddress);
		ipAddressToHostName.put(ipAddress, hostName);

		sortHostNames();

		notifyClientAdded(ipAddress);

		return tempLiteClient;
	}

	public void updateState(State applicationState, String ipAddress) {
		LiteClient liteClient = liteClients.get(ipAddress);
		liteClient.setApplicationState(applicationState);
		notifyClientStateChanged(ipAddress);
	}

	public LiteClient remove(String ipAddress) {
		LiteClient liteClient = liteClients.get(ipAddress);

		hostNameToIPAddress.put(liteClient.getHostName(), liteClient.getIPAddress());
		ipAddressToHostName.put(liteClient.getIPAddress(), liteClient.getHostName());

		liteClients.remove(ipAddress);

		sortHostNames();

		notifyClientRemoved(ipAddress);

		return liteClient;
	}

	public String getIPAddressFromHostName(String hostName) {
		return hostNameToIPAddress.get(hostName);
	}

	public String getHostNameFromIPAddress(String ipAddress) {
		return ipAddressToHostName.get(ipAddress);
	}

	public LiteClient get(String ipAddress) {
		return liteClients.get(ipAddress);
	}

	/**
	 * Generates an array of sorted host strings from the hostNameToIPAddress
	 * ArrayList.
	 *
	 * @return sorted array of host names
	 */
	private void sortHostNames() {
		List<String> hostNameKeys = new ArrayList<String>(hostNameToIPAddress.keySet());
		TreeSet<String> sortedHostNameKeys = new TreeSet<String>(hostNameKeys);

		Object[] hstNames = sortedHostNameKeys.toArray();

		int sortedSetSize = hstNames.length;
		sortedHostNames = new String[sortedSetSize];

		for (int i = 0; i < sortedSetSize; i++) {
			sortedHostNames[i] = (String) hstNames[i];
		}

		Arrays.sort(sortedHostNames);
	}

	public String[] getSortedHostNames() {
		return sortedHostNames;
	}

	public void notifyClientAdded(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			observer.updateLiteClientAdded(ipAddress);
		}
	}

	public void notifyClientRemoved(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			observer.updateLiteClientRemoved(ipAddress);
		}
	}

	public void notifyClientStateChanged(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			observer.updateLiteClientStateChanged(liteClients.get(ipAddress));
		}
	}

	public int size() {
		return liteClients.size();
	}

	public void addObserver(LiteClientsObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(LiteClientsObserver observer) {
		observers.remove(observer);
	}

}
