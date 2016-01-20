/**
 *
 */
package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExecution;
import edu.nyu.cess.remote.server.Server;
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
	LiteClientsObserver liteClientObserver;

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
	 * Returns the client names in the order specified by the comparator argument.
	 *
	 * @param comparator sort order
	 * @return A list of client names in the order specified.
     */
	public List<String> getHostNames(Comparator<LiteClient> comparator) {
		List<LiteClient> sortedClients = sort(comparator);
		List<String> names = new ArrayList<>();
		for (LiteClient client : sortedClients) {
			names.add(client.getHostName());
		}

		return names;
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
	 * @param appExecution
	 * @param ipAddress
     */
	public boolean updateClientState(AppExecution appExecution, String ipAddress)
	{
		// TODO: Update the application state on the client by passing it the AppExecution, instead of just the state.
		try {
			LiteClient client = getByIp(ipAddress);
			client.setAppState(appExecution.getState());
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
        liteClientObserver.updateLiteClientAdded(ipAddress);
	}

	public void notifyClientRemoved(String ipAddress) {
        liteClientObserver.updateLiteClientRemoved(ipAddress);
	}

	public void notifyClientStateChanged(String ipAddress) {
        try {
            liteClientObserver.updateLiteClientStateChanged(getByIp(ipAddress));
        }
        catch (LiteClientNotFoundException e) {
            logger.error("Client not found", e);
        }
	}

	public void notifyClientHostNameChanged(String ipAddress) {
        try {
            liteClientObserver.updateLiteClientHostNameChanged(getByIp(ipAddress));
        }
        catch (LiteClientNotFoundException e) {
            logger.error("Client not found", e);
        }
	}

	public int size() {
		return this.clients.size();
	}

	public void addLiteClientObserver(LiteClientsObserver liteClientsObserver) {
		this.liteClientObserver = liteClientsObserver;
	}

	public void removeObserver(LiteClientsObserver observer) {
		liteClientObserver = null;
	}

}
