package edu.nyu.cess.remote.common.message;

import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/8/16.
 */
public interface MessageSocketObserver
{
    void notifyMessageReceived(NetworkInfo networkInfo, Message message);

	void notifyMessageSenderState(ConnectionState connectionState);
}
