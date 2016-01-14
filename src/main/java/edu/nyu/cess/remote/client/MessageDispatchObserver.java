/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExecutionRequest;

/**
 * @author Anwar A. Ruff
 */
public interface MessageDispatchObserver
{

	void updateServerExecutionRequestReceived(ExecutionRequest executionRequest);

	void updateNetworkStateChanged(boolean isConnected);

	void updateServerMessageReceived(String message);

}
