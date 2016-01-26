package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by aruff on 1/26/16.
 */
public class SocketManager implements MessageSender
{
	final static Logger log = Logger.getLogger(SocketManager.class);

	private MessageSocket messageSocket;
	private MessageRouter messageRouter;
	private NetworkInformation networkInfo;

	public SocketManager(MessageRouter messageRouter, NetworkInformation networkInfo)
	{
		this.messageRouter = messageRouter;
		this.networkInfo = networkInfo;
	}

	public void startPersistentConnection()
	{
		while (true) {
			try {
				log.info("Attempting socket connection.");
				// TODO: Add locking mechanism to prevent sendmessage() from being called when a new socket is being created.
				messageSocket = getNewMessageSocket();
				while (messageSocket.isConnected()) {
					messageRouter.routeMessage(messageSocket.readMessage());
				}
			} catch (IOException e) {
				log.error("Failed to create a message socket.", e);

			} catch (ClassNotFoundException e) {
				log.error("Failed reading inbound message.", e);
			}

			// Wait 2 minutes before trying to create another socket
			try {
				int milliseconds = 2000;
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted.", e);
			}
			log.info("Socket disconnected.");
		}
	}

	@Override public synchronized void sendMessage(Message message)
	{
		try {
			messageSocket.sendMessage(message);
		} catch (IOException e) {
			log.error("Failed to send message.", e);
		}
	}

	private ServerMessageSocket getNewMessageSocket() throws IOException
	{
		return new ServerMessageSocket(networkInfo.getServerIpAddress(), networkInfo.getServerPort());
	}

}
