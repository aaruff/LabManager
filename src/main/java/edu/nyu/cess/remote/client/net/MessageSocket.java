package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.Message;

import java.io.IOException;

/**
 * Created by aruff on 1/25/16.
 */
public interface MessageSocket
{
	boolean isConnected();
	void sendMessage(Message packet) throws IOException;
	Message readMessage() throws ClassNotFoundException, IOException;
}
