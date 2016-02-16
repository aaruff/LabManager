package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.*;
import edu.nyu.cess.remote.common.net.*;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The SocketManager class handles the initialization of a persistent connection to the server, and passed inbound
 * messages to the MessageRouter to be handled.
 */
public class MessageSocketManager implements MessageSender, MessageObservable
{
	final static Logger log = Logger.getLogger(MessageSocketManager.class);

	private MessageSocket messageSocket;
	private NetworkInfo networkInfo;
	private PortInfo portInfo;
    private MessageObserver messageObserver;

	/**
	 * Provides this class with the NetworkInformation required to establish a persistent connection to the server, and
	 * the MessageRouter used to route messages from the server to the corresponding handlers.
	 * @param networkInfo the network information
	 * @param portInfo the port info
     */
	public MessageSocketManager(NetworkInfo networkInfo, PortInfo portInfo)
	{
		this.networkInfo = networkInfo;
		this.portInfo = portInfo;
	}

	/**
	 * Initializes a persistent connection to the server, and passes all valid inbound messages to the router.
	 */
	public void startSocketListener()
	{
		while (true) {
			try {
				log.info("Attempting socket connection.");
				// TODO: Add locking mechanism to prevent sendmessage() from being called when a new socket is being created.
				messageSocket = getNewMessageSocket();
				while (messageSocket.isConnected()) {
                    messageObserver.notifyMessageReceived(messageSocket.readMessage());
				}
			} catch (IOException e) {
				log.error("Failed to create a message socket.", e);

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

	/**
	 * {@link MessageObservable}
	 */
	@Override public void addMessageSourceObserver(MessageObserver messageObserver)
	{
		this.messageObserver = messageObserver;
	}

	/**
	 * {@link MessageSender}
     */
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
		return new ClientMessageSocket(networkInfo, portInfo.getNumber());
	}
}
