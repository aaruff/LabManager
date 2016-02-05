package edu.nyu.cess.remote.client.net.message;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * This class implements the routing of inbound messages to their respective handlers.
 */
public class InboundMessageRouter implements MessageRouter
{
	final static Logger log = Logger.getLogger(InboundMessageRouter.class);

	private HashMap<MessageType, MessageHandler> messageHandlers = new HashMap<>();

	/**
	 * {@link MessageRouter}
	 */
	public void setInboundHandlerForMessageType(MessageType messageType, MessageHandler messageHandler)
	{
		messageHandlers.put(messageType, messageHandler);
	}

	/**
	 * {@link MessageRouter}
     */
	public void routeMessage(Message message)
	{
		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
				messageHandlers.get(MessageType.APPLICATION_EXECUTION).handleMessage(message);
				break;
			case APPLICATION_STATE_UPDATE:
				messageHandlers.get(MessageType.APPLICATION_STATE_UPDATE).handleMessage(message);
				break;
			case USER_NOTIFICATION:
                messageHandlers.get(MessageType.USER_NOTIFICATION).handleMessage(message);
				break;
            // Unsupported messages
			case STATE_UPDATE:
				log.error("State change received from server.");
			case KEEP_ALIVE_PING:
				log.info("keep alive ping received.");
			default:
				break;
		}
	}
}
