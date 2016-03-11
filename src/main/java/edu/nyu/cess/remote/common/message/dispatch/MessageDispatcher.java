package edu.nyu.cess.remote.common.message.dispatch;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.net.ConnectionState;

/**
 * Interface for message handlers.
 */
public interface MessageDispatcher
{
	/**
	 * Reads and process the message provided.
	 * @param message
     */
	void dispatchMessage(Message message);

	/**
	 * Sets the dispatcher that will be handling message routing.
	 * @param dispatchControl
     */
	void setDispatchControl(DispatchControl dispatchControl);

	/**
	 * Notifies the dispatcher that the it has been connected to a message sender.
	 */
	void notifyDispatcherControlState(ConnectionState state);
}
