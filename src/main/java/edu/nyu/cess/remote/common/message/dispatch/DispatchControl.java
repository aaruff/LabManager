package edu.nyu.cess.remote.common.message.dispatch;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageType;

/**
 * Created by aruff on 1/26/16.
 */
public interface DispatchControl
{
	/**
	 * Sets the handler for the specified message type.
	 *
	 * @param messageType the message type
	 * @param messageDispatcher the dispatcher to handle the message
     */
	void setMessageDispatcher(MessageType messageType, MessageDispatcher messageDispatcher);

    /**
     * Dispatches the message to the client.
     * @param message the message to dispatch.
     */
	void dispatchOutboundMessage(Message message);

    /**
     * Dispatches the message to the client.
     * @param message the message to dispatch.
     */
    void dispatchInboundMessage(Message message);
}
