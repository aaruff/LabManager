package edu.nyu.cess.remote.client.notification;

import edu.nyu.cess.remote.client.net.MessageHandler;
import edu.nyu.cess.remote.common.net.Message;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/26/16.
 */
public class UserPromptMessenger implements MessageHandler
{
	final static Logger log = Logger.getLogger(UserPromptMessenger.class);

	private UserPromptHandler userPromptHandler;

	public UserPromptMessenger(UserPromptHandler userPromptHandler)
	{
		this.userPromptHandler = userPromptHandler;
	}

	/**
	 * {@link MessageHandler}
	 */
	public void handleMessage(Message message)
	{
		String userMessage = message.getClientMessage();
		if ( userMessage == null || userMessage.isEmpty()) {
			log.error("Invalid application execution received and ignored.");
			return;
		}

		userPromptHandler.notifyUser(userMessage);
	}
}
