package edu.nyu.cess.remote.client.config;


import edu.nyu.cess.remote.common.net.PortInfo;
import edu.nyu.cess.remote.common.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Contains the client server and host config fileName information.
 */
public class NetInfoFile
{
    private NetworkInfo networkInfo;
    private PortInfo portInfo;

    /**
     * Loads the client property file's fields
     * @param fileName file path and name
     * @throws IOException exception that may occur when reading an invalidly specified file
     */
    public NetInfoFile(String fileName) throws IOException
    {
        InputStream in = NetInfoFile.class.getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        properties.load(in);


        String serverIp = properties.getProperty("ip");
        String clientName = properties.getProperty("name");
        String clientIp = InetAddress.getLocalHost().getHostAddress();

        portInfo = new PortInfo(Integer.parseInt(properties.getProperty("port")));
        networkInfo = new NetworkInfo(clientName, clientIp, serverIp);
    }

    public NetworkInfo getNetworkInfo()
    {
        return networkInfo;
    }

    public PortInfo getPortInfo()
    {
        return portInfo;
    }
}
