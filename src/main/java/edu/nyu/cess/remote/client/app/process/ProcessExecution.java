package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.AppExecution;

/**
 * Implementors of this interface must provide an implementation which enables the execution of an application,
 * specified by an AppExecution, and provide a means to retrieve the current execution state.
 */
public interface ProcessExecution
{
	/**
	 * Notify the stateObserver that an execution request has been received.
	 * @param appExecution The application execution request
     */
	void executeRequest(AppExecution appExecution);

	/**
	 * Return the current execution state.
	 * @return the current execution state
     */
	AppExecution getExecution();
}
