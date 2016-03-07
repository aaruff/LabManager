package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

public class AppExe implements Serializable
{
	private static final long serialVersionUID = -3416367781462816040L;

	private final AppInfo appInfo;
	private final AppState appState;
	private final ErrorType errorType;
	private final String errorMessage;

	public AppExe(AppInfo appInfo, AppState appState)
	{
		this.appInfo = appInfo;
		this.appState = appState;
		this.errorType = ErrorType.NO_ERROR;
		this.errorMessage = "";
	}

	public AppExe(AppInfo appInfo, AppState appState, ErrorType errorType, String errorMessage)
	{
		this.appInfo = appInfo;
		this.appState = appState;
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}

	public AppState getState() {
		return appState;
	}

	public AppInfo getAppInfo()
	{
		return appInfo;
	}

    public boolean isSameAppSameState(AppExe appExe)
    {
        return appInfo.equals(appExe.getAppInfo()) && appState == appExe.getState();
    }

	@Override public String toString()
    {
        return "{appInfo=\"" + appInfo.toString() + "\", state=\"" + appState.toString() +"\"}";
    }

	public ErrorType getErrorType()
	{
		return errorType;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
