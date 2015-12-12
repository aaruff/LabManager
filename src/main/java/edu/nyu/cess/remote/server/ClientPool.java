/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Anwar A. Ruff
 */
public class ClientPool implements LiteClientsObservable
{
	final static Logger logger = Logger.getLogger(Server.class);

	List<LiteClient> clients = new ArrayList<>();
	ArrayList<LiteClientsObserver> observers = new ArrayList<>();

	/**
	 * Returns the client specified by its IP address.
	 *
	 * @param ipAddress
	 * @return
	 * @throws LiteClientNotFoundException
     */
	public LiteClient getByIp(String ipAddress) throws LiteClientNotFoundException
	{
		for (LiteClient client : clients) {
			if (client.getIPAddress().equals(ipAddress)) {
				return client;
			}
		}

		throw new LiteClientNotFoundException("Client: " + ipAddress + " not found.");
	}

	/**
	 * Returns the client specified by its hostname.
	 *
	 * @param hostname
	 * @return
	 * @throws LiteClientNotFoundException
     */
	public LiteClient getByHostname(String hostname) throws LiteClientNotFoundException
	{
		for (LiteClient client : clients) {
			if (client.getHostName().equals(hostname)) {
				return client;
			}
		}

		throw new LiteClientNotFoundException("Client not found with hostname: " + hostname + ".");
	}

    /**
     * Notify observers that a client has been added.
     *
     * @param liteClient LiteClient
     */
	public void addClient(LiteClient liteClient) {
		clients.add(liteClient);

        // Notify observers that a lite client has been added
		notifyClientAdded(liteClient.getIPAddress());
	}

	/**
	 * Notify observers of a state change.
	 *
	 * @param state
	 * @param ipAddress
     */
	public boolean updateClientState(State state, String ipAddress)
	{
		try {
			LiteClient client = getByIp(ipAddress);
			client.setApplicationState(state);
			notifyClientStateChanged(ipAddress);
			return true;
		}
		catch (LiteClientNotFoundException e) {
			logger.error("Failed to find client.", e);
			return false;
		}
	}

	/**
	 * Updates the client host name, specified by the IP address.
	 *
	 * @param hostName
	 * @param ipAddress
     */
	public boolean updateHostName(String hostName, String ipAddress) throws LiteClientNotFoundException
	{
		try {
			LiteClient client = getByIp(ipAddress);
			client.setHostName(hostName);
			notifyClientHostNameChanged(ipAddress);
			return true;
		}
		catch (LiteClientNotFoundException e) {
			logger.error("Failed to find client.", e);
			return false;
		}
	}

	/**
	 * Removes, and returns the specified client.
	 *
	 * @param ipAddress
	 * @return
     */
	public LiteClient popByIpAddress(String ipAddress) throws LiteClientNotFoundException
	{
		LiteClient liteClient = getByIp(ipAddress);
		clients.remove(ipAddress);

		notifyClientRemoved(ipAddress);
		return liteClient;
	}

	/**
	 * Reg
	 * @return
     */
	public List<LiteClient> sort(Comparator<LiteClient> comparator) {
		List<LiteClient> clientsList = new ArrayList<>(clients);

		Collections.sort(clientsList, comparator);
		return clientsList;
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
			try {
				observer.updateLiteClientStateChanged(getByIp(ipAddress));
			}
			catch (LiteClientNotFoundException e) {
				logger.error("Client not found", e);
			}
		}
	}

	public void notifyClientHostNameChanged(String ipAddress) {
		for (LiteClientsObserver observer : observers) {
			try {
				observer.updateLiteClientHostNameChanged(getByIp(ipAddress));
			}
			catch (LiteClientNotFoundException e) {
				logger.error("Client not found", e);
			}
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
