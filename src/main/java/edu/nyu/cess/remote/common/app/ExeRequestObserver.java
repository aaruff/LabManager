package edu.nyu.cess.remote.common.app;

/**
 * Created by aruff on 2/8/16.
 */
public interface ExeRequestObserver
{
    void notifyAppExecution(AppExe appExe, String ipAddress);
}
