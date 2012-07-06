/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExecutionRequest;

public interface ServerProxyObservable {

	public void addObserver(ServerProxyObserver observer);

	public void deleteObserver(ServerProxyObserver observer);

	public void notifyApplicationExececutionRequestReceived(ExecutionRequest executionRequest);

	public void notifyNetworkStatusUpdate(boolean isConnected);

	public void notifyServerMessageReceived(String message);

}
