package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.PortWatcher;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CommunicationNetworkInterface
{

	ArrayList<PortWatcher> observers = new ArrayList<PortWatcher>();

	private Socket socket;

	private Thread networkInterfaceMonitorThread;

	private NetworkInterface networkInterface;

	private ClientServerNetworkInfo clientServerNetworkInfo;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	final static Logger log = Logger.getLogger(CommunicationNetworkInterface.class);

	public CommunicationNetworkInterface(ClientServerNetworkInfo clientServerNetworkInfo)
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
	 * Initializes a {@link Socket} connection via the IP Address and Port
	 * Number. If the initialization of the Socket fails the return value will
	 * be null.
	 *
	 * @return initialized {@link Socket}, or null if a socket connection is not
	 *         established.
	 */
	public Socket getServerSocketConnection() {
		socket = null;
		try {
			socket = new Socket(clientServerNetworkInfo.getServerIpAddress(), clientServerNetworkInfo.getServerPort());

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
	 * {@link Socket} connection is established.
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
	 * Starts the thread that listens for incoming messages from the server.
	 */
	public void startServerMessageListenerThread() {
		networkInterfaceMonitorThread = new Thread(new MonitorNetworkInterface());
		networkInterfaceMonitorThread.setName("Network Interface Monitor");
		networkInterfaceMonitorThread.start();

	}

	/**
	 * This function reads data packets and passes read {@link Message}s to
	 * {@link PortWatcher}s.
	 */
	public void handleClientServerMessaging() {
		Message message = new Message(MessageType.HOST_INFO, clientServerNetworkInfo);
		writeDataPacket(message);

		startServerMessageListenerThread();

		log.info("Waiting for message from " + clientServerNetworkInfo.getServerIpAddress());

		notifyPortWatcher(clientServerNetworkInfo.getServerIpAddress(), true);

		while ((message = readDataPacket()) != null) {
			tellPortWatcherPacketReceived(message);
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
				objectOutputStream = null;
			} catch (IOException e) {
				log.error("failed closing output stream", e);
			}
		}
		if (objectInputStream != null) {
			try {
				objectInputStream.close();
				objectInputStream = null;
			} catch (IOException e) {
				log.error("IO Exception: failed to close ObjectOutputStream.", e);
			}
		}

		notifyPortWatcher(clientServerNetworkInfo.getServerIpAddress(), false);
	}

	/**
	 * Read a {@link Message} sent from a remote node via the initialized
	 * {@link Socket} connection.
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

	public void closeServerNetworkConnection() {
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				log.error(e);
			}
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
				objectOutputStream = null;
			} catch (IOException e) {
				log.error(e);
			}
		}

		if (objectInputStream != null) {
			try {
				objectInputStream.close();
				objectInputStream = null;
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	public boolean addObserver(PortWatcher networkObserver) {
		return observers.add(networkObserver);
	}

	public synchronized void tellPortWatcherPacketReceived(Message packet) {
		for (PortWatcher observer : observers) {
			observer.readServerMessage(packet, clientServerNetworkInfo.getServerIpAddress());
		}
	}

	public synchronized void notifyPortWatcher(String ipAddress, boolean isConnected) {
		for (PortWatcher observer : observers) {
			observer.processStateChange(ipAddress, isConnected);
		}

	}

	/**
	 * Monitors the state of the network interface. If the network interface is
	 * down the socket is set to null to trigger an interrupt.
	 */
	private class MonitorNetworkInterface implements Runnable {

		public void run() {
			boolean networkInterfaceUp = true;
			int monitorInterval = 40000;

			while (networkInterfaceUp) {

				try {
					InetAddress addr = InetAddress.getByName(clientServerNetworkInfo.getClientIpAddress());
					networkInterface = NetworkInterface.getByInetAddress(addr);
				} catch (SocketException e) {
					networkInterface = null;
				} catch (UnknownHostException e) {
					networkInterface = null;
				}

				if (networkInterface != null) {
					try {
						Thread.sleep(monitorInterval);
						networkInterfaceUp = networkInterface.isUp();
						log.info("NIC Status: " + ((networkInterfaceUp) ? "UP" : "DOWN"));
					} catch (InterruptedException e) {
						networkInterfaceUp = false;
					} catch (SocketException e) {
						networkInterfaceUp = false;
					}
				}
				else {
					networkInterfaceUp = false;
				}
			}

			closeServerNetworkConnection();
			log.info("Network Interface Is Down!!!");
			log.info("Attempting to interrupt the network communication thread...");
		}
	}

}
