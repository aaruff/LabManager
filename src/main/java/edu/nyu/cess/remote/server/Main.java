/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.app.AppInfoCollection;
import edu.nyu.cess.remote.server.client.ClientPoolProxy;
import edu.nyu.cess.remote.server.gui.ViewController;
import edu.nyu.cess.remote.server.io.ConfigFileLoader;
import edu.nyu.cess.remote.server.lab.LabLayout;
import edu.nyu.cess.remote.server.net.ClientSocketConnectionMonitor;

/**
 * The server main class loads the lab layout, and application config files, generates the lab view, and finally
 * the client connection monitor.
 */
public class Main
{
    public static void main(String[] args)
    {
        AppInfoCollection appInfoCollection = ConfigFileLoader.getAppInfoCollection("production/app-config.yaml");
		LabLayout labLayout = ConfigFileLoader.getLabLayout("lab-layout.yaml");

		ClientPoolProxy clientPoolProxy = new ClientPoolProxy();
        ViewController viewController = new ViewController(appInfoCollection, clientPoolProxy, labLayout);

		clientPoolProxy.addObserver(viewController);
        viewController.display();

        ClientSocketConnectionMonitor messageObserver = new ClientSocketConnectionMonitor(clientPoolProxy);
        messageObserver.monitorNewClientSocketConnections(2600, labLayout);
	}
}
