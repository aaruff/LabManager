package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

public class AppExe implements Serializable
{
	private static final long serialVersionUID = -3416367781462816040L;

	private final String name;
	private final String path;
	private final String args;

	private final AppState appState;

	public AppExe(String name, String path, String args, AppState appState) {
		this.name = name;
		this.path = path;
		this.args = args;

		this.appState = appState;
	}

	public AppExe(AppInfo appInfo, AppState appState)
	{
		this.name = appInfo.getName();
		this.path = appInfo.getPath();
		this.args = appInfo.getOptions();
		this.appState = appState;
	}

	public AppState getState() {
		return appState;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getArgs() {
		return args;
	}

    public boolean isSameApp(AppExe appExe)
    {
        return name.equals(appExe.getName()) && path.equals(appExe.getPath()) && args.equals(appExe.getArgs());
    }

    public boolean isSameState(AppExe appExe)
    {
        return appExe.getState() == appState;
    }

}
