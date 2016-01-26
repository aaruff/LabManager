package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.client.net.MessageHandler;
import edu.nyu.cess.remote.client.net.MessageSender;
import edu.nyu.cess.remote.common.app.AppExecution;
import edu.nyu.cess.remote.common.app.AppExecutionHandler;
import edu.nyu.cess.remote.common.app.AppExecutionValidator;
import edu.nyu.cess.remote.common.app.ApplicationStateObserver;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/26/16.
 */
public class AppMessenger implements ApplicationStateObserver, MessageHandler
{
	final static Logger log = Logger.getLogger(AppMessenger.class);

	private MessageSender messageSender;
	private NetworkInformation networkInformation;
	private AppExecutionHandler appHandler;

	public AppMessenger(AppExecutionHandler appHandler, MessageSender messageSender, NetworkInformation networkInformation)
	{
		this.appHandler = appHandler;
		this.messageSender = messageSender;
		this.networkInformation = networkInformation;
	}

	/**
	 * {@link MessageHandler}
     */
	public void handleMessage(Message message)
	{
		if ( ! AppExecutionValidator.validate(message.getAppExecution())) {
			log.error("Invalid application execution received and ignored.");
			return;
		}

		if (MessageType.APPLICATION_EXECUTION == message.getMessageType()) {
			appHandler.executeRequest(message.getAppExecution());
		}
		else if(MessageType.APPLICATION_STATE_UPDATE == message.getMessageType()) {
			notifyStateChange(appHandler.getExecution());
		}
		else {
			log.error("Unsupported request received.");
		}
	}

	/**
	 * {@link ApplicationStateObserver}
     */
	@Override public void notifyStateChange(AppExecution appExecution)
	{
		Message message = new Message(MessageType.STATE_UPDATE, appExecution, networkInformation);
		messageSender.sendMessage(message);
	}
}
