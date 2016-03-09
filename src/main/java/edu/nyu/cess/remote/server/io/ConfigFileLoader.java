package edu.nyu.cess.remote.server.io;

import edu.nyu.cess.remote.server.app.AppInfoCollection;
import edu.nyu.cess.remote.server.lab.LabLayout;
import edu.nyu.cess.remote.server.yaml.YamlExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.error.YAMLException;

import javax.swing.*;
import java.io.InputStream;

/**
 * The configuration file loader.
 */
public class ConfigFileLoader
{
	private final static Logger logger = LoggerFactory.getLogger(ConfigFileLoader.class);

	/**
	 * Produces and returns an AppInfoCollection object, which is generated using the file specified by the
	 * appConfigFileName. Any errors that are generated because of a invalidly formatted file, will result in a
	 * message dialog box being displayed, and the corresponding error logged.
	 *
	 * @return An AppInfoCollection
     */
	public static AppInfoCollection getAppInfoCollection(String appConfigFileName)
	{
		try {
			InputStream inputStream = ConfigFileLoader.class.getClassLoader().getResourceAsStream(appConfigFileName);
			if (inputStream == null) {
				String error = "App config file not found.\n";
				logger.error(error);
				JOptionPane.showMessageDialog(new JPanel(), error, "Error", JOptionPane.ERROR_MESSAGE);
			}

			return AppProfilesFile.readFile(inputStream);
		}
		catch (YAMLException e) {
			logger.error("YAML Exception: Unable to read config file because of an invalid entry(s).", e);
			JOptionPane.showMessageDialog(new JPanel(), YamlExceptionMessage.getUserErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		return new AppInfoCollection();
	}

	/**
	 * Produces and returns the LabLayout object, which is generated using the file specified by the labLayoutFileName
	 * argument. Any errors that are generated because of a invalidly formatted file, will result in a
	 * message dialog box being displayed, and the corresponding error logged.
	 *
	 * @param labLayoutFileName the lab layout file name
	 * @return The LabLayout class
     */
	public static LabLayout getLabLayout(String labLayoutFileName)
	{
		try {
			InputStream inputStream = ConfigFileLoader.class.getClassLoader().getResourceAsStream(labLayoutFileName);
			if (inputStream == null) {
				JOptionPane.showMessageDialog(new JPanel(), "Lab config file not found.\n", "Error", JOptionPane.ERROR_MESSAGE);
			}
			return LabLayoutFile.readFile(inputStream);
		}
		catch (YAMLException e) {
			JOptionPane.showMessageDialog(new JPanel(), YamlExceptionMessage.getUserErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			logger.error("YAML Exception: Unable to read the lab config file", e);
		}

		return new LabLayout();
	}
}
