package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Application State Observer Interface
 */
public interface ProcessStateObserver
{
	/**
	 * Notifies the observer of the current application state.
	 * @param appExe the applications current execution information
     */
	void notifyStateChange(AppExe appExe);
}
