package edu.nyu.cess.remote.common.net;

import edu.nyu.cess.remote.common.app.AppExe;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private MessageType messageType;
	private NetworkInfo networkInfo;
	private AppExe appExe;
	private String clientMessage;

	public Message(MessageType messageType, AppExe appExe, NetworkInfo networkInfo)
	{
		this.messageType = messageType;
		this.appExe = appExe;
		this.networkInfo = networkInfo;
	}

	public Message(MessageType messageType, String clientMessage)
	{
		this.messageType = messageType;
		this.clientMessage = clientMessage;
	}

	public Message(MessageType messageType, NetworkInfo networkInfo)
	{
		this.messageType = messageType;
		this.networkInfo = networkInfo;
	}

	public MessageType getMessageType() {
		return this.messageType;
	}

	public NetworkInfo getNetworkInfo()
	{
		return networkInfo;
	}

	public AppExe getAppExe()
	{
		return appExe;
	}

	public String getClientMessage()
	{
		return clientMessage;
	}
}
