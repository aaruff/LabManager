/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

public interface ServerProxyObservable {

	void addDispatchObserver(MessageDispatchObserver observer);

	void removeDispatchObserver(MessageDispatchObserver observer);

	void notifyObserversMessageReceived(ExeRequestMessage executionRequestMessage);

	void notifyObserverServerConnectionStatusChanged(boolean isConnected);

	void notifyServerMessageReceived(String message);

}
