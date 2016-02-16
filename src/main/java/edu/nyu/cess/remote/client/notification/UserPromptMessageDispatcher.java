package edu.nyu.cess.remote.client.notification;

import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.message.Message;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 1/26/16.
 */
public class UserPromptMessageDispatcher implements MessageDispatcher
{
	final static Logger log = Logger.getLogger(UserPromptMessageDispatcher.class);

	private UserPromptHandler userPromptHandler;
    private DispatchControl dispatchControl;

	public UserPromptMessageDispatcher(UserPromptHandler userPromptHandler)
	{
		this.userPromptHandler = userPromptHandler;
	}

	/**
	 * {@link MessageDispatcher}
	 */
	@Override public void dispatchMessage(Message message)
	{
		String userMessage = message.getClientMessage();
		if ( userMessage == null || userMessage.isEmpty()) {
			log.error("Invalid application execution received and ignored.");
			return;
		}

		userPromptHandler.notifyUser(userMessage);
	}

    /**
     * {@link MessageDispatcher}
     */
	@Override public void setDispatchControl(DispatchControl dispatchControl)
	{
        this.dispatchControl = dispatchControl;
	}
}
