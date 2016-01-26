package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.Message;

/**
 * Created by aruff on 1/26/16.
 */
public interface MessageRouter
{
	void routeMessage(Message message);
}
