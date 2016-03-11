package edu.nyu.cess.remote.server.app;

import edu.nyu.cess.remote.common.app.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aruff on 2/8/16.
 */
public class AppInfoCollection
{
    private Map<String, AppInfo> apps = new HashMap<>();

	public AppInfoCollection() {}

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
		ArrayList<String> namesList = new ArrayList<>(apps.keySet());
        Collections.sort(namesList);

        String[] names = new String[namesList.size()];
        namesList.toArray(names);
        return names;
	}
}
