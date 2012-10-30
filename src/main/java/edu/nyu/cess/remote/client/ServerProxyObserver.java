/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExecutionRequest;

/**
 * @author Anwar A. Ruff
 */
public interface ServerProxyObserver {

	public void updateServerExecutionRequestReceived(ExecutionRequest executionRequest);

	public void updateNetworkStateChanged(boolean isConnected);

	public void updateServerMessageReceived(String message);

}
