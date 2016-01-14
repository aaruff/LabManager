package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.common.app.ExecutionRequest;

/**
 * Created by aruff on 1/14/16.
 */
public interface ExecutionRequestHandler
{
	/**
	 * Notify the observer that an execution request has been received.
	 * @param executionRequest
     */
	void execute(ExecutionRequest executionRequest);
}
