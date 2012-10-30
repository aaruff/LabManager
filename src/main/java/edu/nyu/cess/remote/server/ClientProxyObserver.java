/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

public interface ClientProxyObserver {

	public void updateNewClientConnected(String ipAddress);

	public void updateApplicationStateChanged(String ipAddress, State applicationState);

	public void updateClientConnectionStateChanged(String ipAddress, boolean isConnected);
	
	public void updateClientHostNameUpdate(String hostName, String ipAddress);

}
