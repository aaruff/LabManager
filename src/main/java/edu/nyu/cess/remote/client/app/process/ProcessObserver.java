package edu.nyu.cess.remote.client.app.process;

/**
 * The process observer interface.
 */
public interface ProcessObserver
{
	/**
	 * This method when called notifies the observer when the process has stopped.
	 */
	void notifyProcessStopped();
}
