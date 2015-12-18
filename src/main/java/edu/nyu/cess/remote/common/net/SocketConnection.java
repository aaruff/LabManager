package edu.nyu.cess.remote.common.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SocketConnection
{
	private java.net.Socket socket;

    private PortWatcher portWatcher;

	private Thread inboundCommunicationThread;
	private Thread networkInterfaceMonitorThread;

	private String remoteIPAddress;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

    public SocketConnection(java.net.Socket socket, PortWatcher portWatcher)
    {
        this.socket = socket;
        this.portWatcher = portWatcher;
        remoteIPAddress = this.socket.getInetAddress().getHostAddress();
    }

    /**
     * Returns the ip address associated with this socket.
     *
     * @return String IP address
     */
    public String getIP()
    {
        return remoteIPAddress;
    }

	public boolean writeDataPacket(DataPacket packet)
    {
		boolean streamInitialized = false;

		if (socket != null) {
			if (socket.isConnected()) {

				if (objectOutputStream == null) {
					streamInitialized = initializeObjectOutputStream();
				}

				if (objectOutputStream != null) {
					try {
						objectOutputStream.writeObject(packet);
						objectOutputStream.flush();
						streamInitialized = true;
					} catch (IOException ex) {
						System.out.println("IO Exception Occured Writing DataPacket");
					}
				}
			}
		}

		return streamInitialized;
	}

	public void close()
    {
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
				System.out.println("IO Exception: Failed to close ObjectOutputStream.");
			}
		}

		if (inboundCommunicationThread != null) {
			if (inboundCommunicationThread.isAlive()) {
				inboundCommunicationThread.interrupt();
				try {
					inboundCommunicationThread.join();
				} catch (InterruptedException e) {
					System.out.println("LiteClientInterface Close(): Failed to join inbound communication thread");
				}
			}
		}

		if (networkInterfaceMonitorThread != null) {
			if (networkInterfaceMonitorThread.isAlive()) {
				networkInterfaceMonitorThread.interrupt();

				try {
					networkInterfaceMonitorThread.join();
				} catch (InterruptedException e) {
					System.out.println("LiteClientInterface Close(): Failed to join network monitor thread");
				}
			}
		}
	}

	public void startThreadedInboundCommunicationMonitor()
    {
		inboundCommunicationThread = new Thread(new InboundPacketListener());
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
            } catch (IOException ex) {
                System.out.println("Error occured retrieving ObjectOutputStream.");
            }
        }
        return result;
    }

    private DataPacket readDataPacket()
    {
        DataPacket dataPacket = null;
        boolean streamInitialized = true;

        if (socket.isConnected()) {

            if (objectInputStream == null) {
                streamInitialized = initializeObjectInputStream();
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

        return dataPacket;

    }

    private boolean initializeObjectInputStream()
    {
        boolean result = false;

        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    result = true;
                } catch (IOException ex) {
                    objectInputStream = null;
                    result = false;
                }
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
			DataPacket packet = new DataPacket(PacketType.SOCKET_TEST, null);
			/*
			 *  Sends an empty packet to the respective client
			 *  to determine if the socket connection is still established.
			 */
			while (interfaceState) {
				try {
					interfaceState = writeDataPacket(packet);
					System.out.println(((interfaceState) ? "OPEN CONNECTION: " + remoteIPAddress
							: "CLOSED CONNECTION: " + remoteIPAddress));
					Thread.sleep(40000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private class InboundPacketListener implements Runnable
    {
		public void run() {
			DataPacket dataPacket;

            portWatcher.processStateChange(remoteIPAddress, true);

			System.out.println("Waiting for message from Client " + remoteIPAddress);
			while ((dataPacket = readDataPacket()) != null) {
				System.out.println("Data Packet Received");
                portWatcher.processDataPacket(dataPacket, remoteIPAddress);
			}

            portWatcher.processStateChange(remoteIPAddress, false);

			System.out.println("Client " + remoteIPAddress + " connection closed...");
		}
	}

}
