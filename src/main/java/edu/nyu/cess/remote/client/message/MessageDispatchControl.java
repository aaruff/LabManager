package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.*;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * This class implements the routing of inbound messages to their respective handlers.
 */
public class MessageDispatchControl implements DispatchControl, MessageSocketObserver
{
	final static Logger log = LoggerFactory.getLogger(MessageDispatchControl.class);

    private MessageSender messageSender;

    public MessageDispatchControl(MessageObservable messageObservable, MessageSender messageSender)
    {
        messageObservable.addMessageSourceObserver(this);
        this.messageSender = messageSender;
    }

	private HashMap<MessageType, MessageDispatcher> messageHandlers = new HashMap<>();

	/**
	 * {@link DispatchControl}
	 */
	@Override public void setMessageDispatcher(MessageType messageType, MessageDispatcher messageDispatcher)
	{
		messageDispatcher.setDispatchControl(this);
		messageHandlers.put(messageType, messageDispatcher);
	}

	/**
	 * {@link DispatchControl}
	 */
    @Override public void dispatchOutboundMessage(Message message)
    {
        messageSender.sendMessage(message);
    }

	/**
	 * {@link MessageSocketObserver}
     */
    @Override public void notifyMessageReceived(NetworkInfo networkInfo, Message message)
    {
        dispatchInboundMessage(networkInfo, message);
    }

	/**
	 * {@link MessageSocketObserver}
	 */
	@Override public void notifyMessageSenderState(ConnectionState connectionState)
	{
		messageHandlers.get(MessageType.APP_EXE_REQUEST).notifyDispatcherControlState(connectionState);
		messageHandlers.get(MessageType.APP_EXE_UPDATE).notifyDispatcherControlState(connectionState);
	}

	public void dispatchInboundMessage(NetworkInfo networkInfo, Message message)
	{
		switch(message.getMessageType()) {
			case APP_EXE_REQUEST:
				log.debug("App exe request received from {}.", networkInfo.getServerIp());
				messageHandlers.get(MessageType.APP_EXE_REQUEST).dispatchMessage(message);
				break;
			case APP_EXE_UPDATE:
				log.debug("App exe update received from {}.", networkInfo.getServerIp());
				messageHandlers.get(MessageType.APP_EXE_UPDATE).dispatchMessage(message);
				break;
			case KEEP_ALIVE_PING:
			default:
				break;
		}
	}
}
