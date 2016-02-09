package edu.nyu.cess.remote.common.net;

import java.io.IOException;

/**
 * The message socket interface.
 */
public interface MessageSocket
{
	/**
	 * Returns true if the socket is connected, otherwise false.
	 * @return boolean
     */
	boolean isConnected();

	/**
	 * Attempts to send the message to the server.
	 * Note: The message will not be sent if an exception occurs.
	 *
	 * @param message The message to be sent
	 * @throws IOException Exception which may occur during the sending of the message.
     */
	void sendMessage(Message message) throws IOException;

	/**
	 * Attempts to reads a message from the socket, and returns it.
	 * Note: The message will not be read if an exception occurs.
	 * @return Message
	 * @throws IOException Thrown if an IO error occurs while reading
     */
	Message readMessage() throws IOException;
}
