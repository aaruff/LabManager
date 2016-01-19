package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.config.NetworkInformationFile;
import edu.nyu.cess.remote.client.config.NetworkInformationFileValidator;
import edu.nyu.cess.remote.client.net.NetworkMessageHandler;
import edu.nyu.cess.remote.client.notification.UserNotifier;
import edu.nyu.cess.remote.common.net.NetworkInformation;
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
	public static void main(String[] args)
    {
		try {
			NetworkInformation networkInfo = NetworkInformationFile.readPropertyFile("config.properties");

            NetworkInformationFileValidator validator = new NetworkInformationFileValidator(networkInfo);
            if ( ! validator.validate()) {
                log.error(StringUtils.join(validator.getErrors(), ", "));
                System.exit(1);
                return;
            }

			NetworkMessageHandler networkMessageHandler = new NetworkMessageHandler(networkInfo, new UserNotifier());
            networkMessageHandler.startMessageHandler();
		}
		catch (Exception e) {
			log.error("Failed to open the configuration file. Make sure that config.properties is in the classpath.");
			System.exit(1);
			return;
		}
	}
}
