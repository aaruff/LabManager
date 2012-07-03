/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.app.ExecutionRequest;

/**
 * @author Anwar A. Ruff
 */
public interface ServerProxyObserver {

	public void execRequestUpdate(ExecutionRequest executionRequest);

	public void networkStatusUpdate(boolean isConnected);

	public void serverMessageUpdate(String message);

}
