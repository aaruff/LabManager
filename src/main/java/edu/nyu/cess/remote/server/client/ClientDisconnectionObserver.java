package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/11/16.
 */
public interface ClientDisconnectionObserver
{
	void notifyClientDisconnected(NetworkInfo networkInfo);
}
