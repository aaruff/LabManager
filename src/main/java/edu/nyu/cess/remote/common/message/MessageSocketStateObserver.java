package edu.nyu.cess.remote.common.message;

import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/12/16.
 */
public interface MessageSocketStateObserver
{
    void notifySocketDisconnected(NetworkInfo networkInfo);
}
