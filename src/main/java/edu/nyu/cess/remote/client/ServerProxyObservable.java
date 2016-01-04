/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

public interface ServerProxyObservable {

	public void addDispatchObserver(ServerProxyObserver observer);

	public void removeDispatchObserver(ServerProxyObserver observer);

	public void notifyObserversMessageReceived(ExeRequestMessage executionRequestMessage);

	public void notifyObserverServerConnectionStatusChanged(boolean isConnected);

	public void notifyServerMessageReceived(String message);

}
