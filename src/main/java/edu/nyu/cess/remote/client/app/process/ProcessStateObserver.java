package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.AppExecution;

/**
 * Application State Observer Interface
 */
public interface ProcessStateObserver
{
	/**
	 * Notifies the observer of the current application state.
	 * @param appExecution the applications current execution information
     */
	void notifyStateChange(AppExecution appExecution);
}
