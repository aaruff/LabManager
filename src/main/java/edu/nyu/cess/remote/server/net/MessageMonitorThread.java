package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocketObserver;
import edu.nyu.cess.remote.common.message.MessageSocket;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageMonitorThread implements Runnable
{
    final Logger logger = Logger.getLogger(MessageMonitorThread.class);

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
                logger.info("Client connection lost, halting socket monitor thread..");
				socketEnabled = false;
            } catch (InterruptedException e) {
				logger.error("Failed to pause the message monitor thread.", e);
				socketEnabled = false;
			}
		}

    }
}
