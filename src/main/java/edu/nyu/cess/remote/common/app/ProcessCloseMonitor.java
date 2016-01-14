package edu.nyu.cess.remote.common.app;

import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/14/16.
 */
class ProcessCloseMonitor implements Runnable
{
    final static Logger log = Logger.getLogger(ProcessCloseMonitor.class);

    private App app;
	private Process process;

	public ProcessCloseMonitor(App app, Process process)
    {
        this.app = app;
        this.process = process;
    }

    public void run()
	{
        try {
            if (process != null) {
                log.info("Monitoring process...");
                process.waitFor();

                //getErrorGobbler().join(); // handle condition where the
                //getOutputGobbler().join(); // process ends before the threads finish

                if (app.getStateSnapshot() == AppState.STARTED) {
                    app.stop();
                    app.notifyObserverAppStateChanged();
                }
            }
            else {
                log.info("process null, what the hell!");
            }
        } catch (InterruptedException e) {
            log.error("Process execution interrupted.", e);
        }

        log.info("Process destroyed on client.");
        // send message here to server
    }
}
