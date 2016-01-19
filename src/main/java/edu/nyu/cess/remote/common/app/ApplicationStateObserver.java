package edu.nyu.cess.remote.common.app;

/**
 * Application State Observer Interface
 */
public interface ApplicationStateObserver
{
	void applicationStateUpdate(AppExecution appExecution);
}
