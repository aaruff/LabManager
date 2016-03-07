/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.app.ClientAppInfoCollection;
import edu.nyu.cess.remote.server.gui.ViewController;
import edu.nyu.cess.remote.server.io.AppProfilesFile;
import edu.nyu.cess.remote.server.client.ClientPoolProxy;
import edu.nyu.cess.remote.server.io.LabLayoutFile;
import edu.nyu.cess.remote.server.lab.LabLayout;
import edu.nyu.cess.remote.server.net.ClientSocketConnectionMonitor;
import edu.nyu.cess.remote.server.yaml.YamlExceptionMessage;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.error.YAMLException;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Anwar A. Ruff
 */
public class Main
{
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        ClientAppInfoCollection clientAppInfoCollection = null;
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("app-config.yaml");
			if (inputStream == null) {
				JOptionPane.showMessageDialog(new JPanel(), "App config file not found.\n", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}

            clientAppInfoCollection = AppProfilesFile.readFile(inputStream);
        }
        catch (YAMLException e) {
            JOptionPane.showMessageDialog(new JPanel(), YamlExceptionMessage.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.error("YAML Exception: Unable to read config file because of an invalid entry(s).", e);
            System.exit(0);
        }

		LabLayout labLayout = null;
		try {
			InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("lab-layout.yaml");
			if (inputStream == null) {
				JOptionPane.showMessageDialog(new JPanel(), "Lab config file not found.\n", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			labLayout = LabLayoutFile.readFile(inputStream);
		}
		catch (YAMLException e) {
			JOptionPane.showMessageDialog(new JPanel(), YamlExceptionMessage.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			logger.error("YAML Exception: Unable to read the lab config file", e);
			System.exit(0);
		}

		ClientPoolProxy clientPoolProxy = new ClientPoolProxy();
        ViewController viewController = new ViewController(clientAppInfoCollection, clientPoolProxy, labLayout);

		clientPoolProxy.addObserver(viewController);

        viewController.display();


        ClientSocketConnectionMonitor messageObserver = new ClientSocketConnectionMonitor(clientPoolProxy);
		try {
			messageObserver.monitorNewClientSocketConnections(2600, labLayout);
		} catch (IOException e) {
			logger.error("Failed to initialize the server socket.", e);
			System.exit(0);
		}
	}
}
