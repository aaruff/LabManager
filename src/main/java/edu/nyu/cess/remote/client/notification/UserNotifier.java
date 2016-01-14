package edu.nyu.cess.remote.client.notification;

import edu.nyu.cess.remote.client.ui.MessageRunnable;

import javax.swing.*;

/**
 * Created by aruff on 1/14/16.
 */
public class UserNotifier implements UserNotificationHandler
{
	@Override
	public void notifyUser(String text)
	{
		SwingUtilities.invokeLater(new MessageRunnable(text));
	}
}
