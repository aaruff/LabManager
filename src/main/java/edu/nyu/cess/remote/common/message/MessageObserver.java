package edu.nyu.cess.remote.common.message;

/**
 * Created by aruff on 2/8/16.
 */
public interface MessageObserver
{
    void notifyMessageReceived(Message message);
}
