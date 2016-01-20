package edu.nyu.cess.remote.server.yaml;

/**
 * Created by aruff on 1/20/16.
 */
public class YamlExceptionMessage
{
	public static String getMessage()
	{
		return "YAML Error: There was a an error in the configuration file. \nPlease make sure that:\n" +
				"1. There is a '---' before each set of configuration parameters.\n" +
				"2. There is one parameter per line consisting of name, path, options.\n" +
				"3. Below is an sample parameter file:\n\n" +
				"---\n" +
				"name: \'Program A\'\n" +
				"path: \'C:\\\\Path\\To\\ProgramA\'\n" +
				"options: \'-option\'\n" +
				"---\n" +
				"name: \'Program B\'\n" +
				"path: \'C:\\\\Path\\To\\ProgramB\'\n" +
				"options: \'-option\'\n";
	}
}
