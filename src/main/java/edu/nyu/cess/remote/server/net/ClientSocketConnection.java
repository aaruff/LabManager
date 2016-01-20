package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import edu.nyu.cess.remote.server.message.MessageHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketConnection
{
	final static Logger logger = Logger.getLogger(ClientSocketConnection.class);

	private Socket socket;

    private MessageHandler messageHandler;

	private ObjectOutputStream objectOutputStream;

	private Thread inboundCommunicationThread;
	private Thread networkInterfaceMonitorThread;

	private NetworkInformation networkInfo;


    public ClientSocketConnection(Socket socket, MessageHandler messageHandler)
    {
        this.socket = socket;
        this.messageHandler = messageHandler;
		this.networkInfo = new NetworkInformation();
		this.networkInfo.setClientIpAddress(socket.getInetAddress().getHostAddress());
		this.networkInfo.setServerIpAddress(socket.getLocalAddress().getHostAddress());
    }

	/**
	 * Returns the network information for this connection.
	 * @return network information
     */
	public NetworkInformation getNetworkInformation()
	{
		return networkInfo;
	}

	public synchronized boolean sendMessage(Message message)
    {
		boolean streamInitialized = false;

		if (socket != null) {
			if (socket.isConnected()) {

				if (objectOutputStream == null) {
					streamInitialized = initializeObjectOutputStream();
				}

				if (objectOutputStream != null) {
					try {
						objectOutputStream.writeObject(message);
						objectOutputStream.flush();
						streamInitialized = true;
					} catch (IOException e) {
						logger.error("IO Exception Occurred Writing DataPacket", e);
					}
				}
			}
		}

		return streamInitialized;
	}

	public void closeConnection()
    {
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				logger.error("Socket Error: Failed to close.", e);
			}
		}

		if (objectOutputStream != null) {
			try {
				objectOutputStream.close();
				objectOutputStream = null;
			} catch (IOException e) {
				logger.error("IO Exception: Failed to close ObjectOutputStream.", e);
			}
		}

		if (inboundCommunicationThread != null) {
			if (inboundCommunicationThread.isAlive()) {
				inboundCommunicationThread.interrupt();
				try {
					inboundCommunicationThread.join();
				} catch (InterruptedException e) {
					logger.error("LiteClientInterface Close(): Failed to join inbound communication thread", e);
				}
			}
		}

		if (networkInterfaceMonitorThread != null) {
			if (networkInterfaceMonitorThread.isAlive()) {
				networkInterfaceMonitorThread.interrupt();

				try {
					networkInterfaceMonitorThread.join();
				} catch (InterruptedException e) {
					logger.error("LiteClientInterface Close(): Failed to join network monitor thread", e);
				}
			}
		}
	}

	public void startThreadedInboundCommunicationMonitor()
    {
		inboundCommunicationThread = new Thread(new ClientMessageListener(socket, messageHandler));
		inboundCommunicationThread.setName("Inbound communication thread");
		inboundCommunicationThread.start();

        networkInterfaceMonitorThread = new Thread(new ServerClientConnectionMonitor());
        networkInterfaceMonitorThread.setName("Network Interface Monitor");
        networkInterfaceMonitorThread.start();
	}


    private boolean initializeObjectOutputStream()
    {
        boolean result = false;

        if (socket != null) {
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                result = true;
            } catch (IOException e) {
                logger.error("Error occurred retrieving ObjectOutputStream.", e);
            }
        }
        return result;
    }



	/**
	 * The network stream monitor thread is used to periodically (every 40 seconds)
	 * poll the client with an empty packet to determine if the socket connection is
	 * still established. The termination of this tread is used as a flag to signal
	 * that the connection between the server and the client has been broken.
	 */
	private class ServerClientConnectionMonitor implements Runnable
    {
		public void run() {
			boolean interfaceState = true;
			Message packet = new Message(MessageType.PING, null);
			/*
			 *  Sends an empty packet to the respective client
			 *  to determine if the socket connection is still established.
			 */
			while (interfaceState) {
				try {
					interfaceState = sendMessage(packet);
					logger.info(((interfaceState) ? "OPEN CONNECTION: " + clientIpAddress
							: "CLOSED CONNECTION: " + clientIpAddress));
					Thread.sleep(40000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
