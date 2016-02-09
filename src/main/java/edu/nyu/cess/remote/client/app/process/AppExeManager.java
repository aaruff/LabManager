package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.*;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The ProcessExecutionManager handles the execution and monitoring of processes.
 */
public class AppExeManager implements AppExecutor, ProcessObserver, AppExeObservable
{
	final static Logger log = Logger.getLogger(AppExeManager.class);

	ProcessStateObserver stateObserver;

	private AppExe appExe = new AppExe("", "", "", AppState.STOPPED);

	private Process applicationProcess;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	private Thread processMonitor;

	/**
	 * Initializes the process execution manager, with a default stopped state.
	 */
    public AppExeManager()
    {
		setAppExecutionState(AppState.STOPPED);
    }

	/**
	 * {@link AppExeObservable}
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
     * {@link AppExecutor}
     */
	@Override public synchronized void executeRequest(AppExe appExe)
	{
		switch(appExe.getState()) {
			case STARTED:
				switch (getCurrentState()) {
					case STARTED:
                        // TODO: Handle case where application start request received, but an application is already started
						break;
					case STOPPED:
						setAppExe(appExe);
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
     * {@link AppExecutor}
     */
    public AppExe getExecution()
    {
        return appExe;
    }

	/* ---------------------------------------------------------------------
	 *                          PRIVATE
	 * ---------------------------------------------------------------------*/

	private static ProcessObserver getProcessObserverFrom(AppExeManager appExeManager)
	{
		return appExeManager;
	}

    private synchronized void setAppExe(AppExe appExe)
    {
        this.appExe = appExe;
    }

    private synchronized AppState getCurrentState()
    {
        return appExe.getState();
    }

    private synchronized void setAppExecutionState(AppState appState)
    {
		appExe = new AppExe(appExe.getName(), appExe.getPath(), appExe.getArgs(), appState);
    }

	private boolean start()
	{
		boolean execResult = false;
		switch (appExe.getState()) {
			case STOPPED:
				if (applicationProcess == null) {
					try {
						String path = appExe.getPath();
						String name = appExe.getName();
						String args = appExe.getArgs();

						log.info("Attempting to start " + path + name + " " + args);
						applicationProcess = Runtime.getRuntime().exec(path + name + " " + args);

						if (applicationProcess != null) {
							setAppExecutionState(AppState.STARTED);
                            stateObserver.notifyStateChange(appExe);

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
		stateObserver.notifyStateChange(appExe);
	}
}
