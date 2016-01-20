package edu.nyu.cess.remote.server.app;

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
	 * @param inputStream
	 * @return List<RemoteExecProfile>
	 * @throws YAMLException
     */
	public static Map<String, AppProfile> readFile(InputStream inputStream) throws YAMLException
	{
		Yaml yaml = new Yaml(new Constructor(AppProfile.class));

		Map<String, AppProfile> appProfileList = new HashMap<>();
		for (Object remoteExecProfile : yaml.loadAll(inputStream)) {
			AppProfile appProfile = (AppProfile) remoteExecProfile;
			appProfileList.put(appProfile.getName(), appProfile);
		}
		return appProfileList;
	}
}
