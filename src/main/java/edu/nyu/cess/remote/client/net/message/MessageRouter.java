package edu.nyu.cess.remote.client.net.message;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;

/**
 * Created by aruff on 1/26/16.
 */
public interface MessageRouter
{
	/**
	 * Routes the received message to the corresponding message handler, specified by setInboundHandlerForMessageType().
	 * Note: Messages without a provided type handler are ignored.
	 * @param message message to route
     */
	void routeMessage(Message message);

	/**
	 * Sets the handler for the specified message type.
	 *
	 * @param messageType
	 * @param messageHandler
     */
	void setInboundHandlerForMessageType(MessageType messageType, MessageHandler messageHandler);
}
