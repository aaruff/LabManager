/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

/**
 * @author Anwar A. Ruff
 */
public interface MessageDispatchObserver
{

	void updateServerExecutionRequestReceived(ExeRequestMessage exeRequestMessage);

	void updateNetworkStateChanged(boolean isConnected);

	void updateServerMessageReceived(String message);

}
