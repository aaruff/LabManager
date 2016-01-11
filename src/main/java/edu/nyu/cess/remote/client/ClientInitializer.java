package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;
import edu.nyu.cess.remote.client.config.ConfigFileValidator;
import edu.nyu.cess.remote.client.config.HostConfigFile;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ClientInitializer
{
	private final static Logger log = Logger.getLogger(ClientInitializer.class);

	/**
	 * Reads in the client config file, and starts the client.
	 *
	 * @param args command line arguments
     */
	public static void main(String[] args) {

		ClientServerNetworkInfo configFile;
		try {
			configFile = HostConfigFile.readPropertyFile("config.properties");
		}
		catch (Exception e) {
			log.error("Failed to open the configuration file. Make sure that config.properties is in the classpath.");
			System.exit(1);
			return;
		}

		ConfigFileValidator validator = new ConfigFileValidator(configFile);
		if ( ! validator.validate()) {
			log.error(StringUtils.join(validator.getErrors(), ", "));
			System.exit(1);
			return;
		}

		Client client = new Client();
		client.initServerConnection(configFile);
	}

}
