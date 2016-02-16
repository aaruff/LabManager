package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Created by aruff on 2/15/16.
 */
public interface ClientAppExeManager
{
    void executeApp(String clientIp, AppExe appExe);
}
