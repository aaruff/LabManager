package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Implementors of this interface must provide an implementation which enables the execution of an application,
 * specified by an AppExecution, and provide a means to retrieve the current execution state.
 */
public interface AppExecutor
{
	/**
	 * Notify the stateObserver that an execution request has been received.
	 * @param appExe The application execution request
     */
	void executeRequest(AppExe appExe);

	/**
	 * Return the current execution state.
	 * @return the current execution state
     */
	AppExe getExecution();
}
