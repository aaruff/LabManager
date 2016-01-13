/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

public interface ServerProxyObservable {

	void notifyObserversMessageReceived(ExeRequestMessage executionRequestMessage);

	void notifyObserverServerConnectionStatusChanged(boolean isConnected);

	void notifyServerMessageReceived(String message);

}
