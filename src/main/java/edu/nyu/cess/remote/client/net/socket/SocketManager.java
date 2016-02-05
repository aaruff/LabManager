package edu.nyu.cess.remote.client.net.socket;

import edu.nyu.cess.remote.client.net.message.MessageRouter;
import edu.nyu.cess.remote.client.net.message.MessageSender;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.common.net.PortInfo;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The SocketManager class handles the initialization of a persistent connection to the server, and passed inbound
 * messages to the MessageRouter to be handled.
 */
public class SocketManager implements MessageSender
{
	final static Logger log = Logger.getLogger(SocketManager.class);

	private MessageSocket messageSocket;
	private MessageRouter messageRouter;
	private NetworkInfo networkInfo;
	private PortInfo portInfo;

	/**
	 * Provides this class with the NetworkInformation required to establish a persistent connection to the server, and
	 * the MessageRouter used to route messages from the server to the corresponding handlers.
	 * @param messageRouter the message router
	 * @param networkInfo the network information
	 * @param portInfo the port info
     */
	public SocketManager(MessageRouter messageRouter, NetworkInfo networkInfo, PortInfo portInfo)
	{
		this.messageRouter = messageRouter;
		this.networkInfo = networkInfo;
		this.portInfo = portInfo;
	}

	/**
	 * Initializes a persistent connection to the server, and passes all valid inbound messages to the router.
	 */
	public void startListeningForMessagesAndNotifyRouter()
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

	/**
	 * Creates and returns a new client socket.
	 * @return a new socket
	 * @throws IOException
     */
	private ClientMessageSocket getNewMessageSocket() throws IOException
	{
		return new ClientMessageSocket(networkInfo.getServerIpAddress(), portInfo.getNumber());
	}

}
