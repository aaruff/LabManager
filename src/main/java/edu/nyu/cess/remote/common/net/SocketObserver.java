package edu.nyu.cess.remote.common.net;

/**
 * Created by aruff on 2/8/16.
 */
public interface SocketObserver
{
    boolean notifySocketDisconnected(NetworkInfo networkInfo);
}
