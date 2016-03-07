package edu.nyu.cess.remote.server.io;

import edu.nyu.cess.remote.common.app.AppInfo;
import edu.nyu.cess.remote.server.app.ClientAppInfoCollection;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AppProfilesFile
{
	/**
	 * Reads the remotely executable application list.
	 *
	 * @param inputStream the input stream to read from
	 * @return AppInfoCollection the collection of applications that are available for execution in the lab
	 * @throws YAMLException when an error occurs reading the app info file
     */
	public static ClientAppInfoCollection readFile(InputStream inputStream) throws YAMLException
	{
		Yaml yaml = new Yaml(new Constructor(AppInfo.class));

		Map<String, AppInfo> appProfileList = new HashMap<>();
		for (Object remoteExecProfile : yaml.loadAll(inputStream)) {
			AppInfo appInfo = (AppInfo) remoteExecProfile;
			appProfileList.put(appInfo.getName(), appInfo);
		}
		return new ClientAppInfoCollection(appProfileList);
	}
}
