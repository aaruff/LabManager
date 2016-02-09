package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.client.app.process.AppExecutor;
import edu.nyu.cess.remote.client.app.process.AppExeObservable;
import edu.nyu.cess.remote.client.app.process.ProcessStateObserver;
import edu.nyu.cess.remote.client.net.message.DispatchControl;
import edu.nyu.cess.remote.client.net.message.MessageDispatcher;
import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppExecutionValidator;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.apache.log4j.Logger;

/**
 * This class handles the unpacking, routing, and sending of application messages.
 */
public class AppMessageDispatcher implements ProcessStateObserver, MessageDispatcher
{
	final static Logger log = Logger.getLogger(AppMessageDispatcher.class);

	private DispatchControl dispatchControl;
	private NetworkInfo networkInfo;
	private AppExecutor appHandler;

	/**
	 * Initializes the AppMessenger class with the required handler, sender, and network information.
	 * @param appHandler the application execution handler
	 * @param networkInfo network information required for sending messages
     */
	public AppMessageDispatcher(AppExecutor appHandler, AppExeObservable appExeObservable, NetworkInfo networkInfo)
	{
		this.appHandler = appHandler;
		this.networkInfo = networkInfo;
        appExeObservable.setStateObserver(this);
	}

	/**
	 * {@link MessageDispatcher}
     */
	public void dispatchMessage(Message message)
	{
		if ( ! AppExecutionValidator.validate(message.getAppExe())) {
			log.error("Invalid application execution received and ignored.");
			return;
		}

		if (MessageType.APPLICATION_EXECUTION == message.getMessageType()) {
			appHandler.executeRequest(message.getAppExe());
		}
		else if(MessageType.APPLICATION_STATE_UPDATE == message.getMessageType()) {
			notifyStateChange(appHandler.getExecution());
		}
		else {
			log.error("Unsupported request received.");
		}
	}

	/**
	 * {@link ProcessStateObserver}
     */
	@Override public void notifyStateChange(AppExe appExe)
	{
		Message message = new Message(MessageType.STATE_UPDATE, appExe, networkInfo);
		dispatchControl.dispatchOutboundMessage(message);
	}
}
