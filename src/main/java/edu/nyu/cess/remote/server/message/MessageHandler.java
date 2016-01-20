package edu.nyu.cess.remote.server.message;

import edu.nyu.cess.remote.common.net.Message;

/**
 * Created by aruff on 1/20/16.
 */
public interface MessageHandler
{
	void handleInboundMessage(Message message);
}
