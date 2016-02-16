package edu.nyu.cess.remote.server.client;

/**
 * Created by aruff on 2/16/16.
 */
public interface ClientStatusObservable
{
	void addObserver(ClientPoolObserver clientPoolObserver);
}
