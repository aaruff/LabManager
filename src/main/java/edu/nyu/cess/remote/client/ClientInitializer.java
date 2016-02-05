package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.app.AppMessenger;
import edu.nyu.cess.remote.client.app.process.AppExecutionManager;
import edu.nyu.cess.remote.client.config.NetInfoFile;
import edu.nyu.cess.remote.client.config.NetInfoFileValidator;
import edu.nyu.cess.remote.common.net.PortInfo;
import edu.nyu.cess.remote.client.net.message.InboundMessageRouter;
import edu.nyu.cess.remote.client.net.socket.SocketManager;
import edu.nyu.cess.remote.client.notification.UserPrompt;
import edu.nyu.cess.remote.client.notification.UserPromptMessenger;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
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
        NetInfoFile netInfoFile;
		try {
            netInfoFile = new NetInfoFile("config.properties");
		}
		catch (Exception e) {
			log.error("Failed to open the configuration file. Make sure that config.properties is in the classpath.", e);
			System.exit(1);
			return;
		}

		NetInfoFileValidator netInfoValidator = new NetInfoFileValidator(netInfoFile);
		if ( ! netInfoValidator.validate()) {
			log.error(netInfoValidator.getAllErrors());
			System.exit(1);
			return;
		}

        PortInfo portInfo = netInfoFile.getPortInfo();
        NetworkInfo networkInfo = netInfoFile.getNetworkInfo();

        InboundMessageRouter inboundMessageRouter = new InboundMessageRouter();
        SocketManager socketManager = new SocketManager(inboundMessageRouter, networkInfo, portInfo);

        AppExecutionManager appExecutionManager = new AppExecutionManager();
        AppMessenger appMessenger = new AppMessenger(new AppExecutionManager(), socketManager, networkInfo);

        appExecutionManager.setStateObserver(appMessenger);

        inboundMessageRouter.setInboundHandlerForMessageType(MessageType.APPLICATION_EXECUTION, appMessenger);
        inboundMessageRouter.setInboundHandlerForMessageType(MessageType.APPLICATION_STATE_UPDATE, appMessenger);

        UserPromptMessenger userPromptMessenger = new UserPromptMessenger(new UserPrompt());
        inboundMessageRouter.setInboundHandlerForMessageType(MessageType.USER_NOTIFICATION, userPromptMessenger);

        socketManager.startListeningForMessagesAndNotifyRouter();
	}
}
