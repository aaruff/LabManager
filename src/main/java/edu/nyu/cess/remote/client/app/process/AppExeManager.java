package edu.nyu.cess.remote.client.app.process;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppInfo;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.app.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The ProcessExecutionManager handles the execution and monitoring of processes.
 */
public class AppExeManager implements AppExecutor, ProcessObserver, AppExeObservable
{
	final static Logger log = LoggerFactory.getLogger(AppExeManager.class);

	private ProcessStateObserver stateObserver;

	private Object appExeLock = new Object();

	private volatile AppExe currentAppExe;

	private Process appExeProcess;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	private Thread processMonitor;

	/**
	 * Initializes the process execution manager, with a default stopped state.
	 */
    public AppExeManager()
    {
		currentAppExe = new AppExe(new AppInfo(), AppState.STOPPED);
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
	@Override public synchronized void executeRequest(AppExe requestedAppExe)
	{
		synchronized (appExeLock) {
			AppState requestedState = requestedAppExe.getState();
			AppState currentState = currentAppExe.getState();
			AppInfo currentAppInfo = currentAppExe.getAppInfo();
			AppInfo requestedAppInfo = currentAppExe.getAppInfo();

			switch(currentState) {
				case STARTED:
					switch(requestedState) {
						case STARTED:
							if (currentAppInfo.equals(requestedAppInfo)) {
								String errorMessage = String.format("Start Request Ignored: Application (%s) is already running.", requestedAppInfo.getName());
								log.debug(errorMessage);
								stateObserver.notifyStateChange(new AppExe(requestedAppInfo, AppState.STOPPED, ErrorType.SAME_APP_ALREADY_RUNNING, errorMessage));
							}
							else {
								String errorMessage = String.format("Start Request Ignored: Another app (%s) is already running.", currentAppInfo.getName());
								log.debug(errorMessage);
								stateObserver.notifyStateChange(new AppExe(requestedAppInfo, AppState.STOPPED, ErrorType.OTHER_APP_ALREADY_RUNNING, errorMessage));
							}
							break;
						case STOPPED:
							if (currentAppInfo.equals(requestedAppInfo)) {
								stopCurrentProcess();
							}
							else {
								String errorMessage = String.format("Stop Request Ignored: The app to stop (%s) is different the the one currently running (%s).", requestedAppInfo.getName(), currentAppInfo.getName());
								log.debug(errorMessage);
								stateObserver.notifyStateChange(new AppExe(requestedAppInfo, AppState.STOPPED, ErrorType.OTHER_APP_ALREADY_RUNNING, errorMessage));
							}
							break;
					}
					break;
				case STOPPED:
					switch (requestedState) {
						case STARTED:
								performAppExe(requestedAppExe);
							break;
						case STOPPED:
							String errorMessage = String.format("Stop Request Ignored: The app (%s) is not currently running.", requestedAppInfo.getName());
							log.debug(errorMessage);
							stateObserver.notifyStateChange(new AppExe(requestedAppInfo, AppState.STOPPED, ErrorType.APP_ALREADY_STOPPED, errorMessage));
							break;
					}
					break;
			}
		}
	}

    /**
     * {@link AppExecutor}
     */
    public AppExe getExecution()
    {
		synchronized (appExeLock) {
            return new AppExe(currentAppExe.getAppInfo().clone(), currentAppExe.getState());
		}
    }

	/* ---------------------------------------------------------------------
	 *                          PRIVATE
	 * ---------------------------------------------------------------------*/

	private void performAppExe(AppExe appExeRequest)
	{
		AppState exeState = AppState.STOPPED;
		ErrorType errorType = ErrorType.NO_ERROR;
		String errorMessage = "";

		synchronized (appExeLock) {
            log.debug("Attempting to start {}", appExeRequest);
            try {
                AppInfo appInfo = appExeRequest.getAppInfo();
                appExeProcess = Runtime.getRuntime().exec(appInfo.getPath() + " " + appInfo.getArgs());

                if (appExeProcess != null) {
					currentAppExe = new AppExe(appExeRequest.getAppInfo().clone(), AppState.STARTED);

                    errorGobbler = new ProcessIOStreamGobbler(appExeProcess.getErrorStream(), "ERROR");
                    outputGobbler = new ProcessIOStreamGobbler(appExeProcess.getInputStream(), "OUTPUT");

                    errorGobbler.start();
                    outputGobbler.start();

                    processMonitor = new Thread(new ProcessCloseMonitor(this, appExeProcess));
                    processMonitor.start();

                    log.debug("{} has been executed.", appInfo.getName());

					exeState = AppState.STARTED;
                }
				else {
					errorType = ErrorType.FAILED_TO_START;
					errorMessage = String.format("Failed to execute %s", appExeRequest);
					log.error(errorMessage);
                }
            } catch (SecurityException e) {
				errorType = ErrorType.SECURITY_ERROR;
				errorMessage = String.format("Security error: %s", e.getMessage());
            } catch (IOException e) {
				errorType = ErrorType.IO_ERROR;
				errorMessage = String.format("Input/Output error: %s", e.getMessage());
            }
			finally {
				log.error(errorMessage);
			}

			stateObserver.notifyStateChange(new AppExe(appExeRequest.getAppInfo(), exeState, errorType, errorMessage));
		}
	}

	private void stopCurrentProcess()
	{
		if (appExeProcess != null) {
			processMonitor.interrupt();
			appExeProcess.destroy();
		}
		outputGobbler = null;
		errorGobbler = null;
		appExeProcess = null;

		log.debug("Application stopped {}", currentAppExe);
		synchronized (appExeLock) {
			currentAppExe = new AppExe(currentAppExe.getAppInfo().clone(), AppState.STOPPED);
			stateObserver.notifyStateChange(currentAppExe);
		}
	}
}
