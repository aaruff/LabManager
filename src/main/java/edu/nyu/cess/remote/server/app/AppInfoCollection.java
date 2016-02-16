package edu.nyu.cess.remote.server.app;

import edu.nyu.cess.remote.common.app.AppInfo;

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

	public String[] getAppNames()
	{
		return apps.keySet().toArray(new String[apps.size()]);
	}
}
