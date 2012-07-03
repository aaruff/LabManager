package edu.nyu.cess.remote.network;

import java.io.Serializable;

public class DataPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object content;

	public DataPacket(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
