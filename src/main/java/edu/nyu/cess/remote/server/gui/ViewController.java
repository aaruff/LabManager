package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.Main;
import edu.nyu.cess.remote.server.app.ClientAppInfoCollection;
import edu.nyu.cess.remote.server.client.ClientPoolExecutionManager;
import edu.nyu.cess.remote.server.client.ClientPoolObserver;
import edu.nyu.cess.remote.server.gui.observers.ViewAppExeObserver;
import edu.nyu.cess.remote.server.gui.runnables.AddClientRunnable;
import edu.nyu.cess.remote.server.gui.runnables.RemoveClientRunnable;
import edu.nyu.cess.remote.server.gui.runnables.UpdateClientRunnable;
import edu.nyu.cess.remote.server.lab.LabLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;

public class ViewController implements ClientPoolObserver, ViewAppExeObserver
{
	final static Logger logger = Logger.getLogger(Main.class);

	private final ClientAppInfoCollection clientAppInfoCollection;
	private LabFrame labFrame;
    private ClientPoolExecutionManager clientPoolExecutionManager;

    public ViewController(ClientAppInfoCollection clientAppInfoCollection,
						  ClientPoolExecutionManager clientPoolExecutionManager,
						  LabLayout labLayout)
	{
        this.clientPoolExecutionManager = clientPoolExecutionManager;
        this.clientAppInfoCollection = clientAppInfoCollection;
		this.labFrame = new LabFrame(clientAppInfoCollection.getAppNames(), labLayout, this);
	}

	/**
	 * Display the lab manager panel
	 */
	public void display()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				labFrame.pack();
				labFrame.setVisible(true);
			}
		});
	}

	/**
	 * {@link ViewAppExeObserver}
     */
	@Override public void notifyAppExeRequest(String appName, AppState appState, ArrayList<String> ipAddresses)
	{
		AppExe appExe = new AppExe(clientAppInfoCollection.getAppInfo(appName), appState);
		clientPoolExecutionManager.executeApp(appExe, ipAddresses);
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyNewClientConnected(String hostName, String ipAddress)
	{
        SwingUtilities.invokeLater(new AddClientRunnable(labFrame, hostName, ipAddress));
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyClientDisconnected(String ipAddress)
	{
        SwingUtilities.invokeLater(new RemoveClientRunnable(labFrame, ipAddress));
	}

	/**
	 * {@link ClientPoolObserver}
	 */
	@Override public void notifyClientAppUpdate(AppExe appExe, String ipAddress)
	{
        SwingUtilities.invokeLater(new UpdateClientRunnable(labFrame, ipAddress, appExe));
	}
}
