package edu.nyu.cess.remote.server.app;

import edu.nyu.cess.remote.common.app.AppInfo;

/**
 * Created by aruff on 2/16/16.
 */
public class AppInfoValidator
{
	public static boolean validate(AppInfo appInfo)
	{
		// The application info must not be null
		if (appInfo == null) {
			return false;
		}

		// The application name is required.
		if (appInfo.getName() == null || appInfo.getName().isEmpty()) {
			return false;
		}

		// The application's binary path is required
		if (appInfo.getPath() == null || appInfo.getPath().isEmpty()) {
			return false;
		}

		// Options are not required, but should not be null
		if (appInfo.getArgs() == null) {
			return false;
		}

		return true;
	}

	public static boolean validateCollection(ClientAppInfoCollection clientAppInfoCollection)
	{
		if (clientAppInfoCollection == null) {
			return false;
		}

		if (clientAppInfoCollection.getAppNames() == null || clientAppInfoCollection.getAppNames().length == 0) {
			return false;
		}

		String[] names = clientAppInfoCollection.getAppNames();
		for (int i = 0; i < names.length; ++i) {
			if (names[i] == null || names[i].isEmpty() || ! validate(clientAppInfoCollection.getAppInfo(names[i]))) {
				return false;
			}
		}

		return true;
	}
}
