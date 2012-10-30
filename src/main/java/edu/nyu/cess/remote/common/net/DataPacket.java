package edu.nyu.cess.remote.common.net;

import java.io.Serializable;

public class DataPacket implements Serializable {
	private static final long serialVersionUID = 1L;

	private PacketType packetType;
	
	private Object payload;
	
	public DataPacket(PacketType packetType, Object content) {
		this.payload = content;
		this.packetType = packetType;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object content) {
		this.payload = content;
	}
	

	public void setPacketType(PacketType packetType) {
		this.packetType = packetType;
	}
	
	public PacketType getPacketType() {
		return this.packetType;
	}
}
