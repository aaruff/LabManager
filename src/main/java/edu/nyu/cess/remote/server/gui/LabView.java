package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;

/**
 * Created by aruff on 2/10/16.
 */
public interface LabView
{
	void addClient(String clientName, String clientIp);
	void updateClient(String clientIp, AppExe appExe);
	void removeClient(String clientIp);
}
