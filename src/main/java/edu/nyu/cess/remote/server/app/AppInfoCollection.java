package edu.nyu.cess.remote.server.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aruff on 2/8/16.
 */
public class AppInfoCollection
{
    private Map<String, AppInfo> apps = new HashMap<>();

    public AppInfoCollection(Map<String, AppInfo> apps)
    {
        this.apps = apps;
    }

    public AppInfo getAppInfo(String name)
    {
        return apps.get(name);
    }
}
