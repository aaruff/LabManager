package edu.nyu.cess.remote.common.net;

import edu.nyu.cess.remote.common.app.AppExecution;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private MessageType messageType;
	private NetworkInformation networkInfo;
	private AppExecution appExecution;
	private String clientMessage;

	public Message(MessageType messageType, AppExecution appExecution, NetworkInformation networkInfo)
	{
		this.messageType = messageType;
		this.appExecution = appExecution;
		this.networkInfo = networkInfo;
	}

	public Message(MessageType messageType, String clientMessage)
	{
		this.messageType = messageType;
		this.clientMessage = clientMessage;
	}

	public Message(MessageType messageType, NetworkInformation networkInfo)
	{
		this.messageType = messageType;
		this.networkInfo = networkInfo;
	}

	public MessageType getMessageType() {
		return this.messageType;
	}

	public NetworkInformation getNetworkInfo()
	{
		return networkInfo;
	}

	public AppExecution getAppExecution()
	{
		return appExecution;
	}

	public String getClientMessage()
	{
		return clientMessage;
	}
}
