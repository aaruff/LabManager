package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;

import java.util.ArrayList;

/**
 * Created by aruff on 2/15/16.
 */
public interface ClientPoolExecutionManager
{
    void executeApp(AppExe appExe, ArrayList<String> ipAddresses);
}
