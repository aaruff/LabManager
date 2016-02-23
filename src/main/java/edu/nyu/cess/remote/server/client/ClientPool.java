package edu.nyu.cess.remote.server.client;

import java.net.Socket;

/**
 * Created by aruff on 2/16/16.
 */
public interface ClientPool
{
	void addClientSocket(Socket socket);
}
