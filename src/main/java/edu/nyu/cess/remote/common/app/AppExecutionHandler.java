package edu.nyu.cess.remote.common.app;

/**
 * Created by aruff on 1/14/16.
 */
public interface AppExecutionHandler
{
	/**
	 * Notify the observer that an execution request has been received.
	 * @param appExecution
     */
	void executeRequest(AppExecution appExecution);
}
