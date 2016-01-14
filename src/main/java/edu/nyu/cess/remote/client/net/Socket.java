package edu.nyu.cess.remote.client.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by aruff on 1/13/16.
 */
public interface Socket
{
	void closeSocketConnection();
	InetAddress getInetAddress() throws UnknownHostException;
}
