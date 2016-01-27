package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

/**
 * The interface for host config information.
 */
public class NetworkInformation implements Serializable
{
	private static final long serialVersionUID = -5362819996166894026L;

	private final String clientIpAddress;
	private final String serverIpAddress;

	private final String clientHostName;
	private final Integer serverPort;

	public NetworkInformation(String clientHostName, String clientIpAddress, String serverIpAddress, int serverPort)
	{
		this.clientHostName = clientHostName;
		this.clientIpAddress = clientIpAddress;
		this.serverIpAddress = serverIpAddress;
		this.serverPort = serverPort;
	}
	public String getClientIpAddress()
	{
		return clientIpAddress;
	}

	public String getServerIpAddress()
	{
		return serverIpAddress;
	}

	public String getClientName()
	{
		return clientHostName;
	}

	public Integer getServerPort()
	{
		return serverPort;
	}
}
