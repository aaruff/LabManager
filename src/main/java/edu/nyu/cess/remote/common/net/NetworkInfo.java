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

	private final String clientName;

    public NetworkInfo(String clientIpAddress, String serverIpAddress)
    {
        this.clientIpAddress = clientIpAddress;
        this.serverIpAddress = serverIpAddress;
        this.clientName = "";
    }

	public NetworkInfo(String clientName, String clientIpAddress, String serverIpAddress)
	{
		this.clientName = clientName;
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

	public String getClientName()
	{
		return clientName;
	}

	public String toString()
	{
		return "{client IP: " + clientIpAddress + ", server IP: " + serverIpAddress + ", client name: " + clientName + "}";
	}
}
