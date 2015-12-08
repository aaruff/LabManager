/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

import java.util.*;

/**
 * @author Anwar A. Ruff
 */
public class ClientPool implements LiteClientsObservable
{
	List<LiteClient> clients = new ArrayList<>();
	ArrayList<LiteClientsObserver> observers = new ArrayList<>();

    /**
     * Adds a lite client to this collection.
     *
     * @param liteClient LiteClient
     */
	public void put(LiteClient liteClient) {
		clients.add(liteClient);

        // Notify observers that a lite client has been added
		notifyClientAdded(liteClient.getIPAddress());
	}

	public void updateState(State applicationState, String ipAddress) {
		LiteClient liteClient = clients.get(ipAddress);
		liteClient.setApplicationState(applicationState);

		notifyClientStateChanged(ipAddress);
	}

	public void updateHostName(String hostName, String ipAddress) {
		clients.get(ipAddress).setHostName(hostName);

		notifyClientHostNameChanged(ipAddress);
	}

	public LiteClient remove(String ipAddress) {
		LiteClient liteClient = clients.get(ipAddress);
		clients.remove(ipAddress);

		notifyClientRemoved(ipAddress);
		return liteClient;
	}

	public LiteClient getClientByIpAddress(String ipAddress) throws LiteClientNotFoundException {
        for (LiteClient client: clients) {
            if (client.getIPAddress().equals(ipAddress)) {
                return client;
            }
        }

		throw new LiteClientNotFoundException("Client: " + ipAddress + " not found.");
	}

	public LiteClient getLiteClientByHostName(String hostName) {
		for (LiteClient c : this.clients.values()) {
			if (hostName.equals(c.getHostName())) {
				return c;
			}
		}

		return null;
	}

	public LiteClient[] getSortedLiteClients() {
		Map<String, LiteClient> clients = this.clients;
		String clientKeys[] = new String[this.clients.size()];
		int i = 0;
		for (String key : clients.keySet()) {
			clientKeys[i++] = key;
		}

		Arrays.sort(clientKeys);

		i = 0;
		LiteClient[] sortedClients = new LiteClient[this.clients.size()];
		for (String key : clientKeys) {
			sortedClients[i++] = this.clients.get(key);
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
			observer.updateLiteClientStateChanged(this.clients.get(ipAddress));
		}
	}

	public void notifyClientHostNameChanged(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			observer.updateLiteClientHostNameChanged(this.clients.get(ipAddress));
		}
	}

	public int size() {
		return this.clients.size();
	}

	public void addObserver(LiteClientsObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(LiteClientsObserver observer) {
		observers.remove(observer);
	}

}
