package edu.nyu.cess.remote.server.app;

import edu.nyu.cess.remote.common.app.AppInfo;

/**
 * Created by aruff on 2/24/16.
 */
public interface AppInfoCollection
{
	/**
	 * Returns the application execution information for the specified application.
	 *
	 * @param appName The application name
	 * @return The application information for the specified application
     */
	AppInfo getAppInfo(String appName);

	/**
	 * Returns all of the application names contained in this collection.
	 *
	 * @return An array of application names, or an empty array if no applications have been set.
     */
	String[] getAppNames();
}
