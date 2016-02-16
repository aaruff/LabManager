package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

/**
 * The interface for host config information.
 */
public class NetworkInfo implements Serializable
{
	private static final long serialVersionUID = -5362819996166894026L;

	private final String clientIpAddress;
	private final String serverIpAddress;

	private final String clientHostName;

    public NetworkInfo(String clientIpAddress, String serverIpAddress)
    {
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.clientHostName = "";
    }

	public NetworkInfo(String clientHostName, String clientIpAddress, String serverIpAddress)
	{
		this.clientHostName = clientHostName;
		this.clientIpAddress = clientIpAddress;
		this.serverIpAddress = serverIpAddress;
	}
	public String getClientIp()
	{
		return clientIpAddress;
	}

	public String getServerIp()
	{
		return serverIpAddress;
	}

	public String getClientHostName()
	{
		return clientHostName;
	}
}
