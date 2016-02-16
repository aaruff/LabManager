/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.app.AppInfoCollection;
import edu.nyu.cess.remote.server.io.AppProfilesFile;
import edu.nyu.cess.remote.server.client.ClientSocketPoolManager;
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
        AppInfoCollection appInfoCollection = null;
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("app-config.yaml");
            appInfoCollection = AppProfilesFile.readFile(inputStream);
        }
        catch (YAMLException e) {
            JOptionPane.showMessageDialog(new JPanel(), YamlExceptionMessage.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.error("YAML Exception: Unable to read config file because of an invalid entry(s).", e);
            System.exit(0);
        }
        catch (NullPointerException e) {
            JOptionPane.showMessageDialog(new JPanel(),
                    "Could not open, or load, the configuration file.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.error("File Not Found Exception: Unable to find the config file.", e);
            System.exit(0);
        }

		ClientSocketPoolManager clientPoolManager = new ClientSocketPoolManager();
        ViewController viewController = new ViewController(appInfoCollection, clientPoolManager, clientPoolManager);
        viewController.display();


        ClientSocketConnectionMonitor messageObserver = new ClientSocketConnectionMonitor(clientPoolManager);
		try {
			messageObserver.monitorNewClientSocketConnections(2600);
		} catch (IOException e) {
			logger.error("Failed to initialize the server socket.", e);
			System.exit(0);
		}
	}
}
