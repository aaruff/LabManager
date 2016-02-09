package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.app.AppMessageDispatcher;
import edu.nyu.cess.remote.client.app.process.AppExeManager;
import edu.nyu.cess.remote.client.app.process.AppExeObservable;
import edu.nyu.cess.remote.client.app.process.AppExecutor;
import edu.nyu.cess.remote.client.config.NetInfoFile;
import edu.nyu.cess.remote.client.config.NetInfoFileValidator;
import edu.nyu.cess.remote.client.net.message.DispatchControl;
import edu.nyu.cess.remote.client.net.message.MessageDispatchControl;
import edu.nyu.cess.remote.client.net.message.MessageDispatcher;
import edu.nyu.cess.remote.client.net.message.MessageSender;
import edu.nyu.cess.remote.client.net.socket.SocketManager;
import edu.nyu.cess.remote.client.notification.UserPrompt;
import edu.nyu.cess.remote.client.notification.UserPromptMessageDispatcher;
import edu.nyu.cess.remote.common.net.MessageSourceObservable;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.common.net.PortInfo;
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

		SocketManager socketManager = new SocketManager(networkInfo, portInfo);

        MessageSender messageSender = socketManager;
        MessageSourceObservable messageSourceObservable = socketManager;
        DispatchControl dispatchControl = new MessageDispatchControl(messageSourceObservable, messageSender);

        AppExeManager appExeManager = new AppExeManager();
        AppExecutor appExecutor = appExeManager;
        AppExeObservable appExeObservable = appExeManager;
        MessageDispatcher appMessageDispatcher = new AppMessageDispatcher(appExecutor, appExeObservable, networkInfo);

        MessageDispatcher userPromptMessageDispatcher = new UserPromptMessageDispatcher(new UserPrompt());

        dispatchControl.setMessageDispatcher(MessageType.APPLICATION_EXECUTION, appMessageDispatcher);
        dispatchControl.setMessageDispatcher(MessageType.APPLICATION_STATE_UPDATE, appMessageDispatcher);
        dispatchControl.setMessageDispatcher(MessageType.USER_NOTIFICATION, userPromptMessageDispatcher);

        socketManager.startListening();
	}
}
