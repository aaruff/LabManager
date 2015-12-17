/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;

/**
 * @author Anwar A. Ruff
 */
public interface ServerProxyObserver {

	public void updateServerExecutionRequestReceived(ExeRequestMessage exeRequestMessage);

	public void updateNetworkStateChanged(boolean isConnected);

	public void updateServerMessageReceived(String message);

}
