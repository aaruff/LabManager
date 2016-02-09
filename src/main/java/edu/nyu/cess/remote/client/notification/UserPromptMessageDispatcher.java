package edu.nyu.cess.remote.client.notification;

import edu.nyu.cess.remote.client.net.message.MessageDispatcher;
import edu.nyu.cess.remote.common.net.Message;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/26/16.
 */
public class UserPromptMessageDispatcher implements MessageDispatcher
{
	final static Logger log = Logger.getLogger(UserPromptMessageDispatcher.class);

	private UserPromptHandler userPromptHandler;

	public UserPromptMessageDispatcher(UserPromptHandler userPromptHandler)
	{
		this.userPromptHandler = userPromptHandler;
	}

	/**
	 * {@link MessageDispatcher}
	 */
	public void dispatchMessage(Message message)
	{
		String userMessage = message.getClientMessage();
		if ( userMessage == null || userMessage.isEmpty()) {
			log.error("Invalid application execution received and ignored.");
			return;
		}

		userPromptHandler.notifyUser(userMessage);
	}
}
