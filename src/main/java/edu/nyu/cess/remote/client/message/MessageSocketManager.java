package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.*;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.common.net.PortInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The SocketManager class handles the initialization of a persistent connection to the server, and passed inbound
 * messages to the MessageRouter to be handled.
 */
public class MessageSocketManager implements MessageSender, MessageObservable
{
	final static Logger log = LoggerFactory.getLogger(MessageSocketManager.class);

	private final Object messageSocketLock = new Object();
	private volatile MessageSocket messageSocket;
	private NetworkInfo networkInfo;
	private PortInfo portInfo;
    private MessageSocketObserver messageSocketObserver;

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
				synchronized (messageSocketLock) {
					log.debug("Attempting to establishing a new socket connection.");
					messageSocket = getNewMessageSocket();
				}
				messageSocketObserver.notifyMessageSenderState(ConnectionState.CONNECTED);
				while (messageSocket.isConnected()) {
					Message newClientMessage = messageSocket.readMessage();
					// TODO: Validate messages before passing them on to the handler
                    messageSocketObserver.notifyMessageReceived(networkInfo, newClientMessage);
				}
			} catch (IOException e) {
				log.error("IO Exception: {}", e.getMessage());
			}

			messageSocketObserver.notifyMessageSenderState(ConnectionState.DISCONNECTED);

			// Wait 1 minutes before trying to create another socket
			try {
				int oneMinuteMilliseconds = 60000;
				Thread.sleep(oneMinuteMilliseconds);
			} catch (InterruptedException e) {
				log.error("Interruption Exception: {}.", e.getMessage());
			}
			log.debug("Socket disconnected.");
		}
	}

	/**
	 * {@link MessageObservable}
	 */
	@Override public void addMessageSourceObserver(MessageSocketObserver messageSocketObserver)
	{
		this.messageSocketObserver = messageSocketObserver;
	}

	/**
	 * {@link MessageSender}
     */
	@Override public void sendMessage(Message message)
	{
		try {
			synchronized (messageSocketLock) {
				messageSocket.sendMessage(message);
			}
		} catch (IOException e) {
			log.error("IO Exception: Failed to send message. Error = {}", e.getMessage());
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
