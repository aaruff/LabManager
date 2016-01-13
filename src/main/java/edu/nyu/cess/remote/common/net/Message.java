package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private MessageType messageType;

	private Object payload;

	public Message(MessageType messageType, Object content) {
		this.payload = content;
		this.messageType = messageType;
	}

	public Object getPayload() {
		return payload;
	}

	public MessageType getMessageType() {
		return this.messageType;
	}
}
