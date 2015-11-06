/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Anwar A. Ruff
 */
public class RobotPool implements LiteClientsObservable {

	ArrayList<LiteClient> liteClients = new ArrayList<>();
	ArrayList<LiteClientsObserver> observers = new ArrayList<>();

    /**
     * Adds a lite client to this collection.
     *
     * @param liteClient LiteClient
     */
	public void put(LiteClient liteClient) {
		liteClients.add(liteClient);

        // Notify observers that a lite client has been added
		notifyClientAdded(liteClient.getIPAddress());
	}

	public void updateState(State applicationState, String ipAddress) {
		LiteClient liteClient = liteClients.get(ipAddress);
		liteClient.setApplicationState(applicationState);

		notifyClientStateChanged(ipAddress);
	}

	public void updateHostName(String hostName, String ipAddress) {
		liteClients.get(ipAddress).setHostName(hostName);

		notifyClientHostNameChanged(ipAddress);
	}

	public LiteClient remove(String ipAddress) {
		LiteClient liteClient = liteClients.get(ipAddress);
		liteClients.remove(ipAddress);

		notifyClientRemoved(ipAddress);
		return liteClient;
	}

	public LiteClient getLiteClientByIPAddress(String ipAddress) {
        for (LiteClient client: liteClients) {
            if (client.getIPAddress().equals(ipAddress)) {
                return client;
            }
        }

        throw
		return liteClients.get(ipAddress);
	}

	public LiteClient getLiteClientByHostName(String hostName) {
		for (LiteClient c : this.liteClients.values()) {
			if (hostName.equals(c.getHostName())) {
				return c;
			}
		}

		return null;
	}

	public LiteClient[] getSortedLiteClients() {
		Map<String, LiteClient> clients = this.liteClients;
		String clientKeys[] = new String[this.liteClients.size()];
		int i = 0;
		for (String key : clients.keySet()) {
			clientKeys[i++] = key;
		}

		Arrays.sort(clientKeys);

		i = 0;
		LiteClient[] sortedClients = new LiteClient[this.liteClients.size()];
		for (String key : clientKeys) {
			sortedClients[i++] = this.liteClients.get(key);
		}

		return sortedClients;
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
			observer.updateLiteClientStateChanged(this.liteClients.get(ipAddress));
		}
	}

	public void notifyClientHostNameChanged(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			observer.updateLiteClientHostNameChanged(this.liteClients.get(ipAddress));
		}
	}

	public int size() {
		return this.liteClients.size();
	}

	public void addObserver(LiteClientsObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(LiteClientsObserver observer) {
		observers.remove(observer);
	}

}
