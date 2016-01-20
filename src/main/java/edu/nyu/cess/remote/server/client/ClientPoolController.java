package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExecution;

public interface ClientPoolController
{
	void updateClientState(String ipAddress, AppExecution appExecution);
	void updateClientHostNameUpdate(String hostName, String ipAddress);
	void removeClient(String ipAddress);
}
