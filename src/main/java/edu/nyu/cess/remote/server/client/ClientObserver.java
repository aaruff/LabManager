package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Created by aruff on 2/13/16.
 */
public interface ClientObserver
{
    /**
     * Notifies the client observer that the client's host name has been confirmed.
     * @param clientIp the IP address of the client
     */
    void notifyClientConfirmed(String clientIp);

    /**
     * Notifies the client observer that the client has disconnected.
     * @param clientIp IP address of the disconnected client
     */
    void notifyClientDisconnected(String clientIp);

    /**
     * Notifies the client observer if the client application execution has changed.
     *
     * @param appExe the current app execution that has changed
     * @param clientIp the client's IP address
     */
    void notifyClientAppExeUpdate(AppExe appExe, String clientIp);
}
