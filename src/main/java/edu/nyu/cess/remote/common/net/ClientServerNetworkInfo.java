package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

/**
 * The interface for host config information.
 */
public class ClientServerNetworkInfo implements Serializable
{
	private static final long serialVersionUID = -5362819996166894026L;

	private String clientIpAddress;
	private String serverIpAddress;
	private String clientHostName;
	private Integer serverPort;

	public String getClientIpAddress()
	{
		return clientIpAddress;
	}

	public void setClientIpAddress(String clientIpAddress)
	{
		this.clientIpAddress = clientIpAddress;
	}

	public String getServerIpAddress()
	{
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress)
	{
		this.serverIpAddress = serverIpAddress;
	}

	public String getClientName()
	{
		return clientHostName;
	}

	public void setClientName(String clientHostName)
	{
		this.clientHostName = clientHostName;
	}

	public Integer getServerPort()
	{
		return serverPort;
	}

	public void setServerPort(Integer serverPort)
	{
		this.serverPort = serverPort;
	}
}
