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

    public boolean isSame(AppExe appExe)
    {
        return appInfo.equals(appExe.getAppInfo()) && appState == appExe.getState() && errorType == appExe.getErrorType();
    }

	@Override public String toString()
    {
		return String.format("{appInfo=%s, state=%s, errorType=%s, errorMessage=%s}", appInfo, appState, errorType, errorMessage);
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
