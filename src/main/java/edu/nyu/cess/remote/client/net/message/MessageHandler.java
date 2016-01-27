package edu.nyu.cess.remote.client.net.message;

import edu.nyu.cess.remote.common.net.Message;

/**
 * Interface for message handlers.
 */
public interface MessageHandler
{
	/**
	 * Reads and process the message provided.
	 * @param message
     */
	void handleMessage(Message message);
}
