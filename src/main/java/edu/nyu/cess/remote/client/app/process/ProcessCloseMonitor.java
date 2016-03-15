package edu.nyu.cess.remote.client.app.process;

import org.apache.log4j.Logger;

/**
 * Monitors the process provided, and calls the ProcessObserver when its execution stops.
 */
class ProcessCloseMonitor implements Runnable
{
    final static Logger log = Logger.getLogger(ProcessCloseMonitor.class);

    private ProcessObserver processObserver;
	private Process process;

	public ProcessCloseMonitor(ProcessObserver processObserver, Process process)
    {
        this.processObserver = processObserver;
        this.process = process;
    }

    @Override public void run()
	{
        try {
            if (process != null) {
                log.info("Monitoring process...");
                process.waitFor();

				processObserver.notifyProcessStopped();
            }
            else {
                log.info("process null, what the hell!");
            }
        } catch (InterruptedException e) {
            log.error("Process execution interrupted.", e);
        }

        log.info("Process destroyed on client.");
    }
}
