package edu.nyu.cess.remote.server.gui.runnables;

import edu.nyu.cess.remote.server.gui.LabView;

/**
 * Created by aruff on 2/9/16.
 */
public class RemoveClientRunnable implements Runnable {
	private final LabView labView;
	private final String clientIp;

	public RemoveClientRunnable(LabView labView, String clientIp)
	{
		this.labView = labView;
		this.clientIp = clientIp;
	}

	@Override public void run()
	{
		labView.removeClient(clientIp);
	}
}
