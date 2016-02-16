package edu.nyu.cess.remote.client.notification;

import edu.nyu.cess.remote.client.gui.MessageRunnable;

import javax.swing.*;

public class UserPrompt implements UserPromptHandler
{
	@Override
	public void notifyUser(String text)
	{
		SwingUtilities.invokeLater(new MessageRunnable(text));
	}
}
