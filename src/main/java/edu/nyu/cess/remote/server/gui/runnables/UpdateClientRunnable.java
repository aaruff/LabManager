package edu.nyu.cess.remote.server.gui.runnables;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.server.gui.LabView;

/**
 * Created by aruff on 2/9/16.
 */
public class UpdateClientRunnable implements Runnable {
	private final LabView labView;
	private final String clientIp;
	private AppExe appExe;

	public UpdateClientRunnable(LabView labView, String clientIp, AppExe appExe)
	{
		this.labView = labView;
		this.clientIp = clientIp;
		this.appExe = appExe;
	}

	@Override public void run()
	{
		labView.updateClient(clientIp, appExe);
	}
}
