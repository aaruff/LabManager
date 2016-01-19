package edu.nyu.cess.remote.common.app;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;

public class AppExecutor implements AppExecutionHandler, Serializable
{
	final static Logger log = Logger.getLogger(AppExecutor.class);

	private static final long serialVersionUID = 1L;

	ApplicationStateObserver applicationStateObserver;

	private AppExecution appExecution;

	private AppState currentState;

	private Process applicationProcess;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	public Thread processMonitor;

	public AppExecutor(ApplicationStateObserver applicationStateObserver)
	{
		this.applicationStateObserver = applicationStateObserver;
		currentState = AppState.STOPPED;
	}

	public void setAppExecution(AppExecution appExecution)
	{
		this.appExecution = appExecution;
	}

	public synchronized void setState(AppState appState)
	{
		this.currentState = appState;
	}

	public synchronized AppState getCurrentState()
	{
		AppState stateSnapshot;
		switch(currentState) {
			case STARTED:
				stateSnapshot = AppState.STARTED;
				break;
			case STOPPED:
				stateSnapshot = AppState.STOPPED;
				break;
			default:
				stateSnapshot = AppState.STOPPED;
		}

		return stateSnapshot;
	}

	@Override public void executeRequest(AppExecution appExecution)
	{
		log.info("Application execution request received from the server.");
		AppState appState = appExecution.getState();

		switch(appState) {
			case STARTED:
				switch (getCurrentState()) {
					case STARTED:
                        // TODO: Handle case where application start request received, but an application is already started
						break;
					case STOPPED:
						setAppExecution(appExecution);
                        start();
						break;
				}
				break;
			case STOPPED:
				switch (getCurrentState()) {
					case STARTED:
						stop();
						break;
					case STOPPED:
                        // TODO: Handle case where application start request received, but an application is already started
						break;
				}
				break;
			default:
		}
	}

	public boolean start() {
		boolean execResult = false;
		switch (currentState) {
			case STOPPED:
				if (applicationProcess == null) {
					try {
						String path = appExecution.getPath();
						String name = appExecution.getName();
						String args = appExecution.getArgs();

						log.info("Attempting to start " + path + name + " " + args);
						applicationProcess = Runtime.getRuntime().exec(path + name + " " + args);

						if (applicationProcess != null) {
							setState(AppState.STARTED);
                            applicationStateObserver.applicationStateUpdate(appExecution.clone(currentState));

							errorGobbler = new ProcessIOStreamGobbler(applicationProcess.getErrorStream(), "ERROR");
							outputGobbler = new ProcessIOStreamGobbler(applicationProcess.getInputStream(), "OUTPUT");

							errorGobbler.start();
							outputGobbler.start();

							startProcessMonitor();

							execResult = true;
							log.info(name + " has been executed.");
						}
						else {
							currentState = AppState.STOPPED;
						}
					} catch (SecurityException e) {
						log.error("Security Exception Occurred.", e);
					} catch (IOException e) {
						log.error("Process execution failed.", e);
						currentState = AppState.STOPPED;
					}
				}
				break;
			case STARTED:
				execResult = true;
				break;
			default:
		}

		return execResult;
	}

	public synchronized boolean stop() {

		if (applicationProcess != null) {
			processMonitor.interrupt();
			applicationProcess.destroy();
		}

		applicationProcess = null;
		appExecution = null;
		outputGobbler = null;
		errorGobbler = null;

		log.info("Application stopped.");
        setState(AppState.STOPPED);
        applicationStateObserver.applicationStateUpdate(appExecution.clone(currentState));

		return true;
	}

	public void startProcessMonitor() {
		processMonitor = new Thread(new ProcessCloseMonitor(this, applicationProcess));
		processMonitor.start();

	}
}
