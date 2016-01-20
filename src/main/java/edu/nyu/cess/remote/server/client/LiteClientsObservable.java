/**
 *
 */
package edu.nyu.cess.remote.server.client;

/**
 * @author akira
 */
public interface LiteClientsObservable {

	public void addLiteClientObserver(LiteClientsObserver observer);

	public void removeObserver(LiteClientsObserver observer);

	/**
	 * This method is called when a {@link LiteClient} is added
	 * to the {@link ClientPool} collection.
	 * @param ipAddress the key used to add the LiteClient
	 */
	public void notifyClientAdded(String ipAddress);

	public void notifyClientRemoved(String ipAddress);

	public void notifyClientStateChanged(String ipAddress);

	public void notifyClientHostNameChanged(String ipAddress);

}
