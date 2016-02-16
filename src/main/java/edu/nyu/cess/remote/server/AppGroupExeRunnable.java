package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.server.client.ClientAppExeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Thread request application execution on a remote client
 */
public class AppGroupExeRunnable implements Runnable
{
	private final ArrayList<String> ipAddresses;
	private final AppExe appExe;
	private ClientAppExeManager clientAppExeManager;

	public AppGroupExeRunnable(ClientAppExeManager clientAppExeManager, ArrayList<String> ipAddresses, AppExe appExe)
    {
		this.clientAppExeManager = clientAppExeManager;
		this.ipAddresses = new ArrayList<>(ipAddresses);
		this.appExe = appExe;
	}

	/**
	 * {@link Runnable}
	 */
    public void run()
    {
		long seed = System.nanoTime();
        Collections.shuffle(ipAddresses, new Random(seed));

		for (String ipAddress : ipAddresses) {
			clientAppExeManager.executeApp(ipAddress, appExe);
		}
    }
}
