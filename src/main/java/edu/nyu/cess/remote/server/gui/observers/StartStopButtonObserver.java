package edu.nyu.cess.remote.server.gui.observers;

import edu.nyu.cess.remote.common.app.AppState;

/**
 * Created by aruff on 2/10/16.
 */
public interface StartStopButtonObserver
{
	void notifyExeRequest(AppState appState, String clientName, String clientIp);
}
