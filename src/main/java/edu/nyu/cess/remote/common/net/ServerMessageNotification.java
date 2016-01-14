package edu.nyu.cess.remote.common.net;


/**
 * Interface for handling socket packets and state changes.
 */
public interface ServerMessageNotification
{
    void notifyServerMessageReceived(Message message);
}
