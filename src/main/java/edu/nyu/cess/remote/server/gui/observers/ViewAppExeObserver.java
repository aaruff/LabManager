package edu.nyu.cess.remote.server.gui.observers;

import edu.nyu.cess.remote.common.app.AppState;

import java.util.ArrayList;

/**
 * Created by aruff on 2/16/16.
 */
public interface ViewAppExeObserver
{
	void notifyAppExeRequest(String appName, AppState appState, ArrayList<String> ipAddresses);
}
