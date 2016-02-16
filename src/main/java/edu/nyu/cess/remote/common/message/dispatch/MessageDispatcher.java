package edu.nyu.cess.remote.common.message.dispatch;

import edu.nyu.cess.remote.common.message.Message;

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

	void setDispatchControl(DispatchControl dispatchControl);
}
