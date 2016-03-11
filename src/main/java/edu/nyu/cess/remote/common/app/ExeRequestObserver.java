package edu.nyu.cess.remote.common.app;

import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/8/16.
 */
public interface ExeRequestObserver
{
    void notifyAppExecution(AppExe appExe, NetworkInfo networkInfo);
}
