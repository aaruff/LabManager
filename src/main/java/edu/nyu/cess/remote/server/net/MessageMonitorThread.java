package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageObserver;
import edu.nyu.cess.remote.common.message.MessageSocket;
import org.apache.log4j.Logger;

import java.io.IOException;

class MessageMonitorThread implements Runnable
{
    final Logger logger = Logger.getLogger(MessageMonitorThread.class);

    private MessageObserver messageObserver;
    private MessageSocket messageSocket;

    public MessageMonitorThread(MessageSocket messageSocket, MessageObserver messageObserver)
    {
        this.messageSocket = messageSocket;
        this.messageObserver = messageObserver;
    }

    public void run()
	{
        while (true) {
            try {
                logger.info("Waiting for message from Client " + messageSocket.getClientIp());
                Message message = messageSocket.readMessage();
				messageObserver.notifyMessageReceived(message);
            } catch (IOException e) {
                logger.error("Failed to read from the socket", e);
            }
        }

    }
}
