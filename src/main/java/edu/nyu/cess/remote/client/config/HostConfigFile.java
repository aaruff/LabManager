package edu.nyu.cess.remote.client.config;


import edu.nyu.cess.remote.common.config.HostConfigInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aruff on 11/3/15.
 */
public class HostConfigFile implements HostConfigInterface
{
	public static final String NAME = "config.properties";

	private String ip;
	private String port;
	private String hostname;

	public HostConfigFile() throws IOException {
		readPropertiesFile();
	}

    public void readPropertiesFile() throws IOException
    {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(NAME);
        Properties properties = new Properties();
        properties.load(in);

        ip = properties.getProperty("ip");
        port = properties.getProperty("port");
        hostname = properties.getProperty("hostname");
	}

	public String getHostName()
	{
		return hostname;
	}

	public String getIpAddress()
	{
		return ip;
	}

	public String getPort()
	{
		return port;
	}
}
