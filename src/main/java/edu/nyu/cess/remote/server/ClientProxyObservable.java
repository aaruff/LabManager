/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

/**
 * @author Anwar A. Ruff 
 */
public interface ClientProxyObservable {

	public boolean addObserver(ClientProxyObserver clientProxyObserver);

	public boolean deleteObserver(ClientProxyObserver clientProxyObserver);

	public void notifyApplicationStateReceived(State applicationState, String ipAddress);

	public void notifyNewClientConnectionEstablished(String ipAddress);

	public void notifyNetworkStatusChange(String ipAddress, boolean isConnected);
	
	public void notifyClientHostNameUpdate(String hostName, String ipAddress);

}
