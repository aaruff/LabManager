package edu.nyu.cess.remote.common.app;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;

public class App implements ApplicationObservable, Serializable
{
	final static Logger log = Logger.getLogger(App.class);

	private static final long serialVersionUID = 1L;

	ApplicationObserver applicationObserver;

	private ExecutionRequest executionRequest;

	private AppState currentAppState;

	private Process applicationProcess;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	public Thread processMonitor;

	public App(ApplicationObserver applicationObserver, ExecutionRequest executionRequest) {
		currentAppState = AppState.STOPPED;

		this.applicationObserver = applicationObserver;
		this.executionRequest = executionRequest;
	}

	public void setExecutionRequest(ExecutionRequest executionRequest)
	{
		this.executionRequest = executionRequest;
	}

	public synchronized void setState(AppState appState)
	{
		this.currentAppState = appState;
	}

	public synchronized AppState getStateSnapshot()
	{
		AppState stateSnapshot;
		//TODO: ensure setState isn't being called (i.e. modifying the state) while reading the state.
		switch(currentAppState) {
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

	public boolean start() {
		boolean execResult = false;
		switch (currentAppState) {
			case STOPPED:
				if (applicationProcess == null) {
					try {
						String path = executionRequest.getPath();
						String name = executionRequest.getName();
						String args = executionRequest.getArgs();

						log.info("Attempting to start " + path + name + " " + args);
						applicationProcess = Runtime.getRuntime().exec(path + name + " " + args);

						if (applicationProcess != null) {
							currentAppState = AppState.STARTED;

							errorGobbler = new ProcessIOStreamGobbler(applicationProcess.getErrorStream(), "ERROR");
							outputGobbler = new ProcessIOStreamGobbler(applicationProcess.getInputStream(), "OUTPUT");

							errorGobbler.start();
							outputGobbler.start();

							startProcessMonitor();

							execResult = true;
							log.info(name + " has been executed.");
						}
						else {
							currentAppState = AppState.STOPPED;
						}
					} catch (SecurityException e) {
						log.error("Security Exception Occurred.", e);
					} catch (IOException e) {
						log.error("Process execution failed.", e);
						currentAppState = AppState.STOPPED;
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

		currentAppState = AppState.STOPPED;

		applicationProcess = null;
		executionRequest = null;
		outputGobbler = null;
		errorGobbler = null;

		log.info("application has terminated.");
		return true;
	}

	public void changeState(AppState appState) {
		switch(appState) {
			case STARTED:
				start();
				break;
			case STOPPED:
				stop();
				break;
			default:
		}

		notifyObserverAppStateChanged();
	}

	public void startProcessMonitor() {
		processMonitor = new Thread(new ProcessCloseMonitor(this, applicationProcess));
		processMonitor.start();

	}

	public synchronized void notifyObserverAppStateChanged() {
		AppState appState = this.currentAppState;
        applicationObserver.notifyStateChanged(appState);
	}
}
