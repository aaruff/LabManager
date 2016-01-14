package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.common.app.AppState;

/**
 * Created by aruff on 1/14/16.
 */
public interface ApplicationStateChangeNotifier
{
	void notifyApplicationStateChangeOccurred(AppState appState);
}
