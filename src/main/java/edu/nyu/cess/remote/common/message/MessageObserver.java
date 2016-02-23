package edu.nyu.cess.remote.common.message;

import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/8/16.
 */
public interface MessageObserver
{
    void notifyMessageReceived(NetworkInfo networkInfo, Message message);
}
