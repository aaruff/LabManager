package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppInfo;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.app.AppInfoCollection;
import edu.nyu.cess.remote.server.client.ClientAppExeManager;
import edu.nyu.cess.remote.server.client.ClientPoolObserver;
import edu.nyu.cess.remote.server.client.ClientStatusObservable;
import edu.nyu.cess.remote.server.gui.LabViewFrame;
import edu.nyu.cess.remote.server.gui.observers.ViewAppExeObserver;
import edu.nyu.cess.remote.server.gui.runnables.AddClientRunnable;
import edu.nyu.cess.remote.server.gui.runnables.RemoveClientRunnable;
import edu.nyu.cess.remote.server.gui.runnables.UpdateClientRunnable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * The view controller receives updated from both the ClientPool, and the View. Updates from the View are passed on
 * to the AppExeDispatcher to be sent to the client. Request from the ClientPool are passed to the view for processing.
 */
public class ViewController implements ClientPoolObserver, ViewAppExeObserver
{
	private final AppInfoCollection appInfoCollection;
	private LabViewFrame labViewFrame;
    private ClientAppExeManager clientAppExeManager;

    public ViewController(AppInfoCollection appInfoCollection, ClientAppExeManager clientAppExeManager, ClientStatusObservable clientStatusObservable)
	{
        this.clientAppExeManager = clientAppExeManager;
        this.appInfoCollection = appInfoCollection;

		clientStatusObservable.addObserver(this);
		this.labViewFrame = new LabViewFrame(appInfoCollection.getAppNames(), this);
	}

	public void display()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
                labViewFrame.render();
				labViewFrame.setVisible(true);
			}
		});
	}

	/**
	 * {@link ViewAppExeObserver}
     */
	@Override public void notifyAppExeRequest(String appName, AppState appState, ArrayList<String> ipAddresses) {
        AppInfo appInfo = appInfoCollection.getAppInfo(appName);
		AppExe appExe = new AppExe(appInfo, appState);
		Thread startInRange = new Thread(new AppGroupExeRunnable(clientAppExeManager, ipAddresses, appExe));
		startInRange.start();
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyNewClientConnected(String hostName, String ipAddress)
	{
        SwingUtilities.invokeLater(new AddClientRunnable(labViewFrame, hostName, ipAddress));
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyClientDisconnected(String ipAddress)
	{
        SwingUtilities.invokeLater(new RemoveClientRunnable(labViewFrame, ipAddress));
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyClientAppUpdate(AppExe appExe, String ipAddress)
	{
        SwingUtilities.invokeLater(new UpdateClientRunnable(labViewFrame, ipAddress, appExe));
	}
}
