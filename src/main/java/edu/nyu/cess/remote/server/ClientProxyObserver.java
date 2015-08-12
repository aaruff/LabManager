/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

public interface ClientProxyObserver {

	void updateNewClientConnected(String ipAddress);

	void updateApplicationStateChanged(String ipAddress, State applicationState);

	void updateClientConnectionStateChanged(String ipAddress, boolean isConnected);
	
	void updateClientHostNameUpdate(String hostName, String ipAddress);

}
