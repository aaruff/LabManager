package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.*;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.NetworkInfo;
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

    @Override public void notifyMessageReceived(NetworkInfo networkInfo, Message message)
    {
        dispatchInboundMessage(networkInfo, message);
    }

	public void dispatchInboundMessage(NetworkInfo networkInfo, Message message)
	{
		switch(message.getMessageType()) {
			case APP_EXE_REQUEST:
				log.info("App exe request received.");
				messageHandlers.get(MessageType.APP_EXE_REQUEST).dispatchMessage(message);
				break;
			case APP_EXE_UPDATE:
				log.info("App exe update received.");
				messageHandlers.get(MessageType.APP_EXE_UPDATE).dispatchMessage(message);
				break;
			case NETWORK_INFO_UPDATE:
				log.info("Network info update received.");
                messageHandlers.get(MessageType.NETWORK_INFO_UPDATE).dispatchMessage(message);
			case KEEP_ALIVE_PING:
				log.info("keep alive ping received.");
			default:
				break;
		}
	}
}
