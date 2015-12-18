/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.app.profile.AppProfile;
import edu.nyu.cess.remote.server.app.profile.AppProfilesFile;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.error.YAMLException;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Anwar A. Ruff
 */
public class ServerInitializer {

	final static Logger logger = Logger.getLogger(Server.class);

	public static void main(String[] args) {

		try {
			InputStream inputStream = ServerInitializer.class.getClassLoader().getResourceAsStream("app-config.yaml");
			Map<String, AppProfile> appList = AppProfilesFile.readFile(inputStream);
			Server server = new Server(new ClientPool(), appList);
			server.init();
		}
		catch (YAMLException e) {
			JOptionPane.showMessageDialog(new JPanel(),
					"YAML Error: There was a an error in the configuration file. \nPlease make sure that:\n" +
					"1. There is a '---' before each set of configuration parameters.\n" +
					"2. There is one parameter per line consisting of name, path, options.\n" +
					"3. Below is an sample parameter file:\n\n" +
					"---\n" +
					"name: \'Program A\'\n" +
					"path: \'C:\\\\Path\\To\\ProgramA\'\n" +
					"options: \'-option\'\n" +
					"---\n" +
					"name: \'Program B\'\n" +
					"path: \'C:\\\\Path\\To\\ProgramB\'\n" +
					"options: \'-option\'\n",
					"Error", JOptionPane.ERROR_MESSAGE);
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
		catch (IOException e) {
			JOptionPane.showMessageDialog(new JPanel(),
					"A connection error occurred.\n" + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			logger.error("Connection Error", e);

		}
	}
}
