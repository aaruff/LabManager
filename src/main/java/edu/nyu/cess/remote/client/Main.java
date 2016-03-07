package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.app.AppMessageDispatcher;
import edu.nyu.cess.remote.client.app.process.AppExeManager;
import edu.nyu.cess.remote.client.config.NetInfoFile;
import edu.nyu.cess.remote.client.config.NetInfoFileValidator;
import edu.nyu.cess.remote.client.message.MessageDispatchControl;
import edu.nyu.cess.remote.client.message.MessageSocketManager;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.common.net.PortInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client main class is used to load in the required network configuration information, and attempts to
 * contact the server, and maintain a connection to it until stopped.
 */
public class Main
{
	private final static Logger log = LoggerFactory.getLogger(Main.class);

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

		MessageSocketManager messageSocketManager = new MessageSocketManager(networkInfo, portInfo);

        AppExeManager appExeManager = new AppExeManager();

        DispatchControl dispatchControl = new MessageDispatchControl(messageSocketManager, messageSocketManager);

        MessageDispatcher appMessageDispatcher = new AppMessageDispatcher(appExeManager, appExeManager, networkInfo);

        dispatchControl.setMessageDispatcher(MessageType.APP_EXE_REQUEST, appMessageDispatcher);
        dispatchControl.setMessageDispatcher(MessageType.APP_EXE_UPDATE, appMessageDispatcher);

        messageSocketManager.startSocketListener();
	}
}
