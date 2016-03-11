package edu.nyu.cess.remote.server.gui.runnables;

import edu.nyu.cess.remote.server.gui.LabView;

/**
 * Created by aruff on 2/10/16.
 */
public class AddClientRunnable implements Runnable
{
	private final LabView labView;
	private final String clientName;
	private final String clientIp;

	public AddClientRunnable(LabView labView, String clientName, String clientIp)
	{
		this.labView = labView;
		this.clientName = clientName;
		this.clientIp = clientIp;
	}

	@Override public void run()
	{
		labView.addClient(clientName, clientIp);
	}
}
