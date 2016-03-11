package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Created by aruff on 2/13/16.
 */
public interface ClientPoolObserver
{
    void notifyNewClientConnected(String hostName, String ipAddress);
    void notifyClientDisconnected(String ipAddress);
    void notifyClientAppUpdate(AppExe appExe, String ipAddress);
}
