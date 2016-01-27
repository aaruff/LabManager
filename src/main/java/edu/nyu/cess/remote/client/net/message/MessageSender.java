package edu.nyu.cess.remote.client.net.message;

import edu.nyu.cess.remote.common.net.Message;

/**
 * Created by aruff on 1/26/16.
 */
public interface MessageSender
{
	void sendMessage(Message message);
}
