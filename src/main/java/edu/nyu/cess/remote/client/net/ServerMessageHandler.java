package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.client.notification.UserNotificationHandler;
import edu.nyu.cess.remote.common.app.*;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class ServerMessageHandler implements MessageHandlerController, ApplicationStateObserver
{
	private java.net.Socket socket;

	private NetworkInformation networkInformation;

	private AppExecutionHandler appExecutionHandler;
	private UserNotificationHandler userNotificationHandler;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	final static Logger log = Logger.getLogger(ServerMessageHandler.class);

	public ServerMessageHandler(NetworkInformation networkInformation, UserNotificationHandler userNotificationHandler)
	{
		this.appExecutionHandler = new AppExecutor(this);
		this.networkInformation = networkInformation;
		this.userNotificationHandler = userNotificationHandler;
	}

	/**
	 * {@link MessageHandlerController}
	 */
	@Override public void initServerMessageListener() {

		while (true) {
			openSocket();
            confirmNetworkInfo();
            startNetworkInterfaceMonitor();
			startSocketListenerThread();
		}
	}

	/**
	 * {@link MessageHandlerController}
	 */
	@Override public synchronized void stopMessageHandler() {
		close(socket);
		close(objectOutputStream);
		close(objectInputStream);
	}

	/**
	 * {@link ApplicationStateObserver}
	 */
	@Override public void applicationStateUpdate(AppExecution appExecution)
	{
		Message message = new Message(MessageType.STATE_CHANGE, appExecution, networkInformation);
		sendMessage(message);
	}

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */

	/**
	 * Sends the provided message to the server.
	 *
	 * @param message The message
     */
	private void routeIncomingMessage(Message message)
	{
		log.info("Server message received.");

		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
				AppExecution appExecution = message.getAppExecution();
				if (appExecution != null) {
					log.info("Packet Content: ApplicationExecRequest");
					appExecutionHandler.executeRequest(appExecution);
				}
				break;
			case USER_NOTIFICATION:
				String text = message.getClientMessage();
				if (text != null && !text.isEmpty()) {
					userNotificationHandler.notifyUser(text);
				}
				break;
			case STATE_CHANGE:
			case NETWORK_INFO_UPDATE:
				// Not supported by the Client
				break;
			case KEEP_ALIVE_PING:
				// No processing occurs during a socket test
				break;
			default:
				// Do nothing
				break;
		}
	}

	/**
	 * Sends a {@link Message} to the Server
	 *
	 * @param packet
	 *            A data packet wrapper
	 * @return true if a connection was established, otherwise false
	 */
	private boolean sendMessage(Message packet) {
		boolean streamInitialized = false;

		if (socket != null) {
			if (socket.isConnected()) {

				if (objectOutputStream == null) {
					streamInitialized = setServerObjectOutputStream();
				}

				if (objectOutputStream != null) {
					try {
						objectOutputStream.writeObject(packet);
						objectOutputStream.flush();
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}

		return streamInitialized;
	}

	private synchronized Message readDataPacket() {
		Message message = null;
		boolean streamInitialized = true;

		if (socket != null) {
			if (socket.isConnected()) {

				if (objectInputStream == null) {
					streamInitialized = setServerObjectInputStream();
				}

				if (objectInputStream != null && streamInitialized) {
					try {
						Object object = objectInputStream.readObject();
						message = (Message) object;
					} catch (ClassNotFoundException e) {
						log.error("The Serialized Object Not Found", e);
						message = null;
					} catch (StreamCorruptedException e) {
						log.error(e);
						message = null;
					} catch (IOException e) {
						log.error(e);
						message = null;
					}
				}
			}
		}

		return message;

	}

	/**
	 * Initialize the {@link ObjectInputStream}.
	 *
	 * @return an {@link ObjectInputStream} or <code>null</code> if
	 *         initialization failed.
	 */
	private boolean setServerObjectInputStream() {
		boolean result = false;

		if (this.socket.isConnected()) {
			try {
				InputStream inputStream = this.socket.getInputStream();
				if (inputStream != null) {
					this.objectInputStream = new ObjectInputStream(inputStream);
					result = true;
				}
			} catch (IOException ex) {
				this.objectInputStream = null;
				result = false;
			}
		}

		return result;
	}

	/**
	 * Initialize the {@link ObjectOutputStream}
	 *
	 * @return an {@link ObjectOutputStream} or null if initialization failed.
	 */
	private boolean setServerObjectOutputStream() {
		boolean result = false;

		if (socket != null) {
			try {
				OutputStream outputStream = socket.getOutputStream();
				if (outputStream != null) {
					objectOutputStream = new ObjectOutputStream(outputStream);
					result = true;
				}
			} catch (IOException e) {
				log.error("Error occurred retrieving ObjectOutputStream.", e);
			}
		}
		return result;
	}

	/**
	 * Initializes a {@link java.net.Socket} connection via the IP Address and Port
	 * Number. If the initialization of the Socket fails the return value will
	 * be null.
	 *
	 * @return initialized {@link java.net.Socket}, or null if a socket connection is not
	 *         established.
	 */
	private java.net.Socket getServerSocketConnection() {
		socket = null;
		try {
			socket = new java.net.Socket(networkInformation.getServerIpAddress(), networkInformation.getServerPort());

			log.info("Network connection established...");

		} catch (UnknownHostException e) {
			log.error("Error: No Known Host.", e);

		} catch (ConnectException e) {
			log.error("Error: Failed to connection to server.", e);

		} catch (IOException e) {
			log.error("IO Exception occurred.", e);
		}

		return socket;
	}

	/**
	 * Polls the remote network node on a millisecond interval until a
	 * {@link java.net.Socket} connection is established.
	 */
	private void openSocket() {
		int pollInterval = 2000; // milliseconds
		socket = null;

		socket = getServerSocketConnection();
		while (socket == null) {
			try {
				Thread.sleep(pollInterval);
				log.info("Attempting to connect to server: " + networkInformation.getServerIpAddress());
				socket = getServerSocketConnection();
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted.", e);
			}
		}
	}

    private void confirmNetworkInfo()
    {
        log.info("Sending initial client-server network info to the server.");
        Message message = new Message(MessageType.NETWORK_INFO_UPDATE, networkInformation);
        sendMessage(message);

        boolean infoConfirmed = false;
        while (! infoConfirmed) {
            message = readDataPacket();
            if (message.getMessageType() == MessageType.NETWORK_INFO_CONFIRMED) {
                infoConfirmed = true;
            }
        }

        close(objectOutputStream);
        close(objectInputStream);
    }

    private void startNetworkInterfaceMonitor()
    {
        log.info("Starting the network interface monitor.");
        Thread networkInterfaceMonitorThread = new Thread(new NetworkInterfaceMonitor(this, networkInformation));
        networkInterfaceMonitorThread.setName("Network Interface Monitor");
        networkInterfaceMonitorThread.start();
    }

	private void startSocketListenerThread()
	{
        //TODO: Refactor out this function and place it into a thread that performs callback on the MessageHandler on incoming messages
		log.info("Waiting for message from the server.");
        Message message;
		while ((message = readDataPacket()) != null) {
            routeIncomingMessage(message);
		}

		close(objectOutputStream);
		close(objectInputStream);

	}

	private void close(Closeable closeable)
	{
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

}
