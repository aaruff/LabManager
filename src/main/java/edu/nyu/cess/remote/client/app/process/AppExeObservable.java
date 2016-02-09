package edu.nyu.cess.remote.client.app.process;

/**
 * The ProcessStateObservable defines the method for setting the observer, and declares that only ProcessStateObservers
 * are allowed to to observe implementors of this interface..
 */
public interface AppExeObservable
{
	/**
	 * Sets the process state observer.
	 * @param processStateObserver the observer
     */
	void setStateObserver(ProcessStateObserver processStateObserver);
}
