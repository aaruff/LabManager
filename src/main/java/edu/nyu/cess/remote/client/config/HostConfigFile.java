package edu.nyu.cess.remote.client.config;


import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Contains the client server and host config file information.
 */
public class HostConfigFile
{
	/**
	 * Reads the client-server network property file for the server IP, Port, and Client hostname alias.
	 *
	 * @param propertyFileName
	 * @return
	 * @throws IOException
     */
    public static ClientServerNetworkInfo readPropertyFile(String propertyFileName) throws IOException
    {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(propertyFileName);
        Properties properties = new Properties();
        properties.load(in);

		ClientServerNetworkInfo networkInfo = new ClientServerNetworkInfo();
        networkInfo.setServerIpAddress(properties.getProperty("ip"));
        networkInfo.setServerPort(Integer.parseInt(properties.getProperty("port")));
        networkInfo.setClientName(properties.getProperty("name"));
		networkInfo.setClientIpAddress(InetAddress.getLocalHost().getHostAddress());

		return networkInfo;
	}
}
