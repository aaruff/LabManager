package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Created by aruff on 2/13/16.
 */
public interface ClientSocketObserver
{
    void notifyClientConnectionLost();
    void notifyClientConfirmed();
    void notifyAppExeUpdate(AppExe appExe);
}
