package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.client.app.process.ProcessExecution;
import edu.nyu.cess.remote.client.app.process.ProcessStateObserver;
import edu.nyu.cess.remote.client.net.message.MessageHandler;
import edu.nyu.cess.remote.client.net.message.MessageSender;
import edu.nyu.cess.remote.common.app.AppExecution;
import edu.nyu.cess.remote.common.app.AppExecutionValidator;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.log4j.Logger;

/**
 * This class handles the unpacking, routing, and sending of application messages.
 */
public class AppMessenger implements ProcessStateObserver, MessageHandler
{
	final static Logger log = Logger.getLogger(AppMessenger.class);

	private MessageSender messageSender;
	private NetworkInformation networkInformation;
	private ProcessExecution appHandler;

	public AppMessenger(ProcessExecution appHandler, MessageSender messageSender, NetworkInformation networkInformation)
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
	 * {@link ProcessStateObserver}
     */
	@Override public void notifyStateChange(AppExecution appExecution)
	{
		Message message = new Message(MessageType.STATE_UPDATE, appExecution, networkInformation);
		messageSender.sendMessage(message);
	}
}
