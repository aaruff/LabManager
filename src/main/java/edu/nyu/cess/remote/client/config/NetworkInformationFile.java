package edu.nyu.cess.remote.client.config;


import edu.nyu.cess.remote.common.net.NetworkInformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Contains the client server and host config file information.
 */
public class NetworkInformationFile
{
	/**
	 * Reads the client-server network property file for the server IP, Port, and Client hostname alias.
	 *
	 * @param propertyFileName
	 * @return
	 * @throws IOException
     */
    public static NetworkInformation readPropertyFile(String propertyFileName) throws IOException
    {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(propertyFileName);
        Properties properties = new Properties();
        properties.load(in);

		String serverIp = properties.getProperty("ip");
		int serverPort = Integer.parseInt(properties.getProperty("port"));
		String clientName = properties.getProperty("name");
		String clientIp = InetAddress.getLocalHost().getHostAddress();

		return new NetworkInformation(clientName, clientIp, serverIp, serverPort);
	}
}
