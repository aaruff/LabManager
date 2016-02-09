package edu.nyu.cess.remote.client.net.message;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageSourceObservable;
import edu.nyu.cess.remote.common.net.MessageSourceObserver;
import edu.nyu.cess.remote.common.net.MessageType;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * This class implements the routing of inbound messages to their respective handlers.
 */
public class MessageDispatchControl implements DispatchControl, MessageSourceObserver
{
    private MessageSender messageSender;

    final static Logger log = Logger.getLogger(MessageDispatchControl.class);

    public MessageDispatchControl(MessageSourceObservable messageSourceObservable, MessageSender messageSender)
    {
        messageSourceObservable.addMessageSourceObserver(this);
        this.messageSender = messageSender;
    }

	private HashMap<MessageType, MessageDispatcher> messageHandlers = new HashMap<>();

	@Override public void setMessageDispatcher(MessageType messageType, MessageDispatcher messageDispatcher)
	{
		messageHandlers.put(messageType, messageDispatcher);
	}

    @Override public void dispatchOutboundMessage(Message message)
    {
        messageSender.sendMessage(message);
    }

    @Override public void notifyObserverMessageReceived(Message message)
    {
        dispatchInboundMessage(message);
    }

	public void dispatchInboundMessage(Message message)
	{
		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
				messageHandlers.get(MessageType.APPLICATION_EXECUTION).dispatchMessage(message);
				break;
			case APPLICATION_STATE_UPDATE:
				messageHandlers.get(MessageType.APPLICATION_STATE_UPDATE).dispatchMessage(message);
				break;
			case USER_NOTIFICATION:
                messageHandlers.get(MessageType.USER_NOTIFICATION).dispatchMessage(message);
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
