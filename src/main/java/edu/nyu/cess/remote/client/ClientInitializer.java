package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.app.AppMessenger;
import edu.nyu.cess.remote.client.config.NetworkInformationFile;
import edu.nyu.cess.remote.client.config.NetworkInformationFileValidator;
import edu.nyu.cess.remote.client.net.message.InboundMessageRouter;
import edu.nyu.cess.remote.client.net.socket.SocketManager;
import edu.nyu.cess.remote.client.notification.UserPrompt;
import edu.nyu.cess.remote.client.notification.UserPromptMessenger;
import edu.nyu.cess.remote.client.app.process.ProcessExecutionManager;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
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
                log.error(validator.getAllErrors());
                System.exit(1);
                return;
            }

			InboundMessageRouter messageRouter = new InboundMessageRouter();
			SocketManager socketManager = new SocketManager(messageRouter, networkInfo);

			ProcessExecutionManager appExecutionManager = new ProcessExecutionManager();
			AppMessenger appMessenger = new AppMessenger(new ProcessExecutionManager(), socketManager, networkInfo);
			appExecutionManager.setStateObserver(appMessenger);
			messageRouter.setMessageHandler(MessageType.APPLICATION_EXECUTION, appMessenger);
			messageRouter.setMessageHandler(MessageType.APPLICATION_STATE_UPDATE, appMessenger);

			UserPromptMessenger userPromptMessenger = new UserPromptMessenger(new UserPrompt());
			messageRouter.setMessageHandler(MessageType.USER_NOTIFICATION, userPromptMessenger);

			socketManager.startPersistentConnection();
		}
		catch (Exception e) {
			log.error("Failed to open the configuration file. Make sure that config.properties is in the classpath.");
			System.exit(1);
			return;
		}
	}
}
