package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.*;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The ProcessExecutionManager handles the execution and monitoring of processes.
 */
public class ProcessExecutionManager implements ProcessExecution, ProcessObserver, ProcessStateObservable
{
	final static Logger log = Logger.getLogger(ProcessExecutionManager.class);

	ProcessStateObserver stateObserver;

	private AppExecution appExecution;

	private Process applicationProcess;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	private Thread processMonitor;

	/**
	 * Initializes the process execution manager, with a default stopped state.
	 */
    public ProcessExecutionManager()
    {
		setAppExecutionState(AppState.STOPPED);
    }

	/**
	 * {@link ProcessStateObservable}
     */
	@Override public void setStateObserver(ProcessStateObserver stateObserver)
	{
		this.stateObserver = stateObserver;
	}

	/**
	 * {@link ProcessObserver}
	 */
	@Override public synchronized void notifyProcessStopped()
	{
		stopCurrentProcess();
	}

    /**
     * {@link ProcessExecution}
     */
	@Override public synchronized void executeRequest(AppExecution appExecution)
	{
		switch(appExecution.getState()) {
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
						stopCurrentProcess();
						break;
					case STOPPED:
                        // TODO: Handle case where application start request received, but an application is already started
						break;
				}
				break;
			default:
		}
	}

    /**
     * {@link ProcessExecution}
     */
    public AppExecution getExecution()
    {
        return appExecution;
    }

	/* ---------------------------------------------------------------------
	 *                          PRIVATE
	 * ---------------------------------------------------------------------*/

	private static ProcessObserver getProcessObserverFrom(ProcessExecutionManager processExecutionManager)
	{
		return processExecutionManager;
	}

    private synchronized void setAppExecution(AppExecution appExecution)
    {
        this.appExecution = appExecution;
    }

    private synchronized AppState getCurrentState()
    {
        return appExecution.getState();
    }

    private synchronized void setAppExecutionState(AppState appState)
    {
		appExecution = new AppExecution(appExecution.getName(), appExecution.getPath(), appExecution.getArgs(), appState);
    }

	private boolean start()
	{
		boolean execResult = false;
		switch (appExecution.getState()) {
			case STOPPED:
				if (applicationProcess == null) {
					try {
						String path = appExecution.getPath();
						String name = appExecution.getName();
						String args = appExecution.getArgs();

						log.info("Attempting to start " + path + name + " " + args);
						applicationProcess = Runtime.getRuntime().exec(path + name + " " + args);

						if (applicationProcess != null) {
							setAppExecutionState(AppState.STARTED);
                            stateObserver.notifyStateChange(appExecution);

							errorGobbler = new ProcessIOStreamGobbler(applicationProcess.getErrorStream(), "ERROR");
							outputGobbler = new ProcessIOStreamGobbler(applicationProcess.getInputStream(), "OUTPUT");

							errorGobbler.start();
							outputGobbler.start();

							startProcessMonitor();

							execResult = true;
							log.info(name + " has been executed.");
						}
						else {
							setAppExecutionState(AppState.STOPPED);
						}
					} catch (SecurityException e) {
						log.error("Security Exception Occurred.", e);
					} catch (IOException e) {
						log.error("Process execution failed.", e);
						setAppExecutionState(AppState.STOPPED);
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

	private void startProcessMonitor()
	{
		processMonitor = new Thread(new ProcessCloseMonitor(getProcessObserverFrom(this), applicationProcess));
		processMonitor.start();
	}

	private void stopCurrentProcess()
	{
		if (applicationProcess != null) {
			processMonitor.interrupt();
			applicationProcess.destroy();
		}
		outputGobbler = null;
		errorGobbler = null;
		applicationProcess = null;

		log.info("Application stopped.");
		setAppExecutionState(AppState.STOPPED);
		stateObserver.notifyStateChange(appExecution);
	}
}
