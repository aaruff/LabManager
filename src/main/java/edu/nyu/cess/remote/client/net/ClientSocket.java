package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.ServerMessageNotification;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class ClientSocket implements Socket
{
	ServerMessageNotification serverMessageNotification;

	private java.net.Socket socket;

	private ClientServerNetworkInfo clientServerNetworkInfo;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	final static Logger log = Logger.getLogger(ClientSocket.class);

	/**
	 * Establishes a persistent connection between the client and the server.
	 */
	public void createPersistentServerConnection() {

		while (true) {
			int pollIntervalMilliseconds = 2000; // milliseconds
			setServerSocketConnection(pollIntervalMilliseconds);
			startClientServerSocketCommunication();
			try {
				log.info("Connected to the server...");
				Thread.sleep(pollIntervalMilliseconds);
			} catch (InterruptedException e) {
				log.error("Polling tread sleep interrupted", e);
			}
			log.info("Attempting to reconnect to the server...");
		}
	}

	public ClientSocket(ClientServerNetworkInfo clientServerNetworkInfo)
	{
		this.clientServerNetworkInfo = clientServerNetworkInfo;
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
	public java.net.Socket getServerSocketConnection() {
		socket = null;
		try {
			socket = new java.net.Socket(clientServerNetworkInfo.getServerIpAddress(), clientServerNetworkInfo.getServerPort());

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
	 *
	 * @param pollInterval poll interval in milliseconds
	 */
	public void setServerSocketConnection(int pollInterval) {
		socket = null;

		socket = getServerSocketConnection();
		while (socket == null) {
			try {
				Thread.sleep(pollInterval);
				log.info("Attempting to connect to server: " + clientServerNetworkInfo.getServerIpAddress());
				socket = getServerSocketConnection();
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted.", e);
			}
		}
	}

	/**
	 * This function reads data packets and passes read {@link Message}s to
	 * {@link ServerMessageNotification}s.
	 */
	public void startClientServerSocketCommunication()
	{
		log.info("Sending initial client-server network info to the server.");
		Message message = new Message(MessageType.NETWORK_INFO, clientServerNetworkInfo);
		writeDataPacket(message);

		log.info("Starting the network interface monitor.");
		Thread networkInterfaceMonitorThread = new Thread(new NetworkInterfaceMonitor(this));
		networkInterfaceMonitorThread.setName("Network Interface Monitor");
		networkInterfaceMonitorThread.start();

		log.info("Waiting for message from the server.");
		while ((message = readDataPacket()) != null) {
            serverMessageNotification.notifyServerMessageReceived(message);
		}

		close(objectOutputStream);
		close(objectInputStream);

	}

	/**
	 * Read a {@link Message} sent from a remote node via the initialized
	 * {@link java.net.Socket} connection.
	 *
	 * @return a {@link Message} or null if the packet reading process
	 *         failed.
	 */
	public synchronized Message readDataPacket() {
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
	 * Sends a {@link Message} to the Server
	 *
	 * @param packet
	 *            A data packet wrapper
	 * @return true if a connection was established, otherwise false
	 */
	public boolean writeDataPacket(Message packet) {
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

	@Override public synchronized void closeSocketConnection() {
		close(socket);
		close(objectOutputStream);
		close(objectInputStream);
	}

	@Override
	public synchronized InetAddress getInetAddress() throws UnknownHostException
	{
		return InetAddress.getByName(clientServerNetworkInfo.getClientIpAddress());
	}

	public void setServerMessageNotification(ServerMessageNotification serverMessageNotification) {
		this.serverMessageNotification = serverMessageNotification;
	}
}
