package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.common.app.*;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/14/16.
 */
public class AppHandler implements ExecutionRequestHandler, ApplicationObserver
{
	final static Logger log = Logger.getLogger(AppHandler.class);

	private App app;
	private ApplicationStateChangeNotifier applicationStateChangeNotifier;

	public AppHandler(App app, ApplicationStateChangeNotifier applicationStateChangeNotifier)
	{
		this.app = app;
		this.applicationStateChangeNotifier = applicationStateChangeNotifier;
	}

	@Override
	public void execute(ExecutionRequest executionRequest)
	{
		log.info("Application execution request received from the server.");
		AppState requestedApplicationAppState = executionRequest.getApplicationAppState();

		switch(requestedApplicationAppState) {
			case STARTED:
                switch (app.getStateSnapshot()) {
                    case STARTED:
						// application start request received, but an application is already started
						// TODO: refactor
						applicationStateChangeNotifier.notifyApplicationStateChangeOccurred(requestedApplicationAppState);
                        break;
                    case STOPPED:
                        app = new App(this, executionRequest);
                        app.changeState(requestedApplicationAppState);
                        break;
                }
				break;
			case STOPPED:
				switch (app.getStateSnapshot()) {
					case STARTED:
						app.changeState(requestedApplicationAppState);
						break;
					case STOPPED:
						// application stop request received, but an application is already stopped
						// TODO: refactor
						applicationStateChangeNotifier.notifyApplicationStateChangeOccurred(requestedApplicationAppState);
						break;
				}
				break;
			default:
		}
	}

	@Override
	public void notifyStateChanged(AppState applicationAppState)
	{
		applicationStateChangeNotifier.notifyApplicationStateChangeOccurred(applicationAppState);
	}
}
