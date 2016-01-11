package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

public class HostInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String hostName;
	private String IPAddress;


	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public String getIPAddress()
	{
		return IPAddress;
	}

	public void setIPAddress(String iPAddress)
	{
		IPAddress = iPAddress;
	}
}
