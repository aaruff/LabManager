package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;
import edu.nyu.cess.remote.common.net.DataPacket;
import edu.nyu.cess.remote.common.net.PacketType;
import edu.nyu.cess.remote.common.net.PortWatcher;

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
			} catch (IOException ex) {
				System.out.println("Error occured retrieving ObjectOutputStream.");
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

			System.out.println("Network connection established...");

		} catch (UnknownHostException ex) {
			System.out.println("Network Error: No Known Host.");

		} catch (ConnectException ex) {
			System.out.println("Network Error: connection to server " + clientServerNetworkInfo.getServerIpAddress() + " failed.");

		} catch (IOException ex) {
			System.out.println("IO Exception occurred.");
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
				System.out.println("Attempting to connect to server: " + clientServerNetworkInfo.getServerIpAddress());
				socket = getServerSocketConnection();
			} catch (InterruptedException e) {
				System.out.println("Thread sleep interrupted...");
			}
		}
	}

	/**
	 * Initializes the network interface monitor thread.
	 */
	public void startNetworkInterfaceMonitor() {
		networkInterfaceMonitorThread = new Thread(new MonitorNetworkInterface());
		networkInterfaceMonitorThread.setName("Network Interface Monitor");
		networkInterfaceMonitorThread.start();

	}

	/**
	 * This function reads data packets and passes read {@link DataPacket}s to
	 * {@link PortWatcher}s.
	 */
	public void handleClientServerMessaging() {
		DataPacket dataPacket = new DataPacket(PacketType.HOST_INFO, clientServerNetworkInfo);
		writeDataPacket(dataPacket);

		startNetworkInterfaceMonitor();

		System.out.println("Waiting for message from " + clientServerNetworkInfo.getServerIpAddress());

		tellPortWatcherConnectionChanged(clientServerNetworkInfo.getServerIpAddress(), true);

		while ((dataPacket = readDataPacket()) != null) {
			tellPortWatcherPacketReceived(dataPacket);
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
				objectOutputStream = null;
			} catch (IOException e) {
				System.out.println("failed closing output stream");
			}
		}
		if (objectInputStream != null) {
			try {
				objectInputStream.close();
				objectInputStream = null;
			} catch (IOException e) {
				System.out.println("IO Exception: failed to close ObjectOutputStream.");
			}
		}

		tellPortWatcherConnectionChanged(clientServerNetworkInfo.getServerIpAddress(), false);
	}

	/**
	 * Read a {@link DataPacket} sent from a remote node via the initialized
	 * {@link Socket} connection.
	 *
	 * @return a {@link DataPacket} or null if the packet reading process
	 *         failed.
	 */
	public synchronized DataPacket readDataPacket() {
		DataPacket dataPacket = null;
		boolean streamInitialized = true;

		if (socket != null) {
			if (socket.isConnected()) {

				if (objectInputStream == null) {
					streamInitialized = setServerObjectInputStream();
				}

				if (objectInputStream != null && streamInitialized) {
					try {
						Object object = objectInputStream.readObject();
						dataPacket = (DataPacket) object;
					} catch (ClassNotFoundException ex) {
						System.out.println("The Serialized Object Not Found");
						dataPacket = null;
					} catch (StreamCorruptedException ex) {
						dataPacket = null;
					} catch (IOException ex) {
						dataPacket = null;
					}
				}
			}
		}

		return dataPacket;

	}

	/**
	 * Sends a {@link DataPacket} to the Server
	 *
	 * @param packet
	 *            A data packet wrapper
	 * @return true if a connection was established, otherwise false
	 */
	public boolean writeDataPacket(DataPacket packet) {
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
					} catch (IOException ex) {
						System.out.println("IO Exception Occured Writing DataPacket");
						ex.printStackTrace();
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
				System.out.println("Socket Error: Failed to close.");
			}
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
				objectOutputStream = null;
			} catch (IOException ex) {
				System.out.println("IO Exception: Failed to close ObjectOutputStream.");
			}
		}

		if (objectInputStream != null) {
			try {
				objectInputStream.close();
				objectInputStream = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean addObserver(PortWatcher networkObserver) {
		return observers.add(networkObserver);
	}

	public synchronized void tellPortWatcherPacketReceived(DataPacket packet) {
		for (PortWatcher observer : observers) {
			observer.readServerMessage(packet, clientServerNetworkInfo.getServerIpAddress());
		}
	}

	public synchronized void tellPortWatcherConnectionChanged(String ipAddress, boolean isConnected) {
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
						System.out.println("NIC Status: " + ((networkInterfaceUp) ? "UP" : "DOWN"));
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
			System.out.println("Network Interface Is Down!!!");
			System.out.println("Attempting to interrupt the network communication thread...");

		}
	}

}
