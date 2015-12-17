/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

public interface ServerProxyObservable {

	public void addServerProxyObserver(ServerProxyObserver observer);

	public void deleteServerProxyObserver(ServerProxyObserver observer);

	public void notifyApplicationExececutionRequestReceived(ExeRequestMessage executionRequestMessage);

	public void notifyObserverNetworkStateChanged(boolean isConnected);

	public void notifyServerMessageReceived(String message);

}
