package edu.nyu.cess.remote.server.client;

/**
 * Created by aruff on 2/11/16.
 */
public interface ClientDisconnectionObserver
{
	void notifyClientDisconnected(String clientIp);
}
