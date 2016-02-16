package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageObservable;
import edu.nyu.cess.remote.common.message.MessageObserver;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.message.MessageSender;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * This class implements the routing of inbound messages to their respective handlers.
 */
public class MessageDispatchControl implements DispatchControl, MessageObserver
{
	final static Logger log = Logger.getLogger(MessageDispatchControl.class);

    private MessageSender messageSender;

    public MessageDispatchControl(MessageObservable messageObservable, MessageSender messageSender)
    {
        messageObservable.addMessageSourceObserver(this);
        this.messageSender = messageSender;
    }

	private HashMap<MessageType, MessageDispatcher> messageHandlers = new HashMap<>();

	@Override public void setMessageDispatcher(MessageType messageType, MessageDispatcher messageDispatcher)
	{
		messageDispatcher.setDispatchControl(this);
		messageHandlers.put(messageType, messageDispatcher);
	}

    @Override public void dispatchOutboundMessage(Message message)
    {
        messageSender.sendMessage(message);
    }

    @Override public void notifyMessageReceived(Message message)
    {
        dispatchInboundMessage(message);
    }

	public void dispatchInboundMessage(Message message)
	{
		switch(message.getMessageType()) {
			case APP_EXE_REQUEST:
				messageHandlers.get(MessageType.APP_EXE_REQUEST).dispatchMessage(message);
				break;
			case APP_EXE_UPDATE:
				messageHandlers.get(MessageType.APP_EXE_UPDATE).dispatchMessage(message);
				break;
			case NETWORK_INFO_UPDATE:
                messageHandlers.get(MessageType.NETWORK_INFO_UPDATE).dispatchMessage(message);
			case KEEP_ALIVE_PING:
				log.info("keep alive ping received.");
			default:
				break;
		}
	}
}
