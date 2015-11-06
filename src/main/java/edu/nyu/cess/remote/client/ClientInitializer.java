package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.config.ConfigFileValidator;
import edu.nyu.cess.remote.client.config.HostConfigFile;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ClientInitializer
{
	private final static Logger log = Logger.getLogger(ClientInitializer.class);


	public static void main(String[] args) {
		Client client = new Client();

		HostConfigFile configFile;
		try {
			configFile = new HostConfigFile();
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

		client.initServerConnection(configFile);
	}

}
