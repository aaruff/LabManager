package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 1/20/16.
 */
public interface ClientConnectionObserver
{
	void notifyNewClientConnected(NetworkInfo networkInfo);
}
