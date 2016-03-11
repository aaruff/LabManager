package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.server.net.ClientSocket;

/**
 * Created by aruff on 2/16/16.
 */
public interface ClientPool
{
	void addClient(ClientSocket socket);
}
