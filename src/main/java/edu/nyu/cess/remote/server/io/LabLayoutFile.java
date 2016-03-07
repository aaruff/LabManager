package edu.nyu.cess.remote.server.io;

import edu.nyu.cess.remote.server.lab.LabLayout;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;

/**
 * Created by aruff on 2/24/16.
 */
public class LabLayoutFile
{
	/**
	 * Reads the lab config file.
	 *
	 * @param inputStream the input stream to read from
	 * @return AppInfoCollection the collection of applications that are available for execution in the lab
	 * @throws YAMLException when an error occurs reading the app info file
	 */
	public static LabLayout readFile(InputStream inputStream) throws YAMLException
	{
		Yaml yaml = new Yaml(new Constructor(LabLayout.class));
		return (LabLayout) yaml.load(inputStream);
	}
}
