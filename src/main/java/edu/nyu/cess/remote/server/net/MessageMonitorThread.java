package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.message.MessageSocketObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessageMonitorThread implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MessageMonitorThread.class);

    private MessageSocketObserver messageSocketObserver;
    private MessageSocket messageSocket;

    public MessageMonitorThread(MessageSocket messageSocket, MessageSocketObserver messageSocketObserver)
    {
        this.messageSocket = messageSocket;
        this.messageSocketObserver = messageSocketObserver;
    }

    public void run()
	{
		boolean socketEnabled = true;
        while (socketEnabled) {
            try {
                Message message = messageSocket.readMessage();

				// todo: validate message before passing it to the observer:w
				messageSocketObserver.notifyMessageReceived(messageSocket.getNetworkInfo(), message);
				Thread.sleep(1000);
            } catch (IOException e) {
                logger.info("IO Exception occurred, halting read message. Error = {}", e.getMessage());
				socketEnabled = false;
            } catch (InterruptedException e) {
				logger.error("Failed to pause the message monitor thread.", e);
			}
		}

    }
}
