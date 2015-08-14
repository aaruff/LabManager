/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.State;

/**
 * @author Anwar A. Ruff 
 */
public interface ClientProxyObservable
{
    boolean addObserver(ClientProxyObserver clientProxyObserver);

    boolean deleteObserver(ClientProxyObserver clientProxyObserver);

    void notifyApplicationStateReceived(State applicationState, String ipAddress);

    void notifyNewClientConnectionEstablished(String ipAddress);

    void notifyNetworkStatusChange(String ipAddress, boolean isConnected);

    void notifyClientHostNameUpdate(String hostName, String ipAddress);
}
