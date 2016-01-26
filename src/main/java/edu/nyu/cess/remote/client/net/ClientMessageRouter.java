package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.Message;
import org.apache.log4j.Logger;

public class ClientMessageRouter implements MessageRouter
{
	final static Logger log = Logger.getLogger(ClientMessageRouter.class);

	private MessageHandler appMessageHandler;
	private MessageHandler userNotificationMessageHandler;

	public void setAppMessageHandler(MessageHandler appMessageHandler)
	{
		this.appMessageHandler = appMessageHandler;
	}

	public void setUserPromptMessageHandler(MessageHandler userNotificationMessageHandler)
	{
		this.userNotificationMessageHandler = userNotificationMessageHandler;
	}

	public void routeMessage(Message message)
	{
		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
			case APPLICATION_STATE_UPDATE:
                appMessageHandler.handleMessage(message);
				break;
			case USER_NOTIFICATION:
                userNotificationMessageHandler.handleMessage(message);
				break;
            // Unsupported messages on client ignored
			case STATE_UPDATE:
				log.error("State change received from server.");
			case KEEP_ALIVE_PING:
			default:
				break;
		}
	}
}
