package edu.nyu.cess.remote.common.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SocketHandler
{
	private java.net.Socket socket;

    private ServerMessageNotification serverMessageNotification;

	private Thread inboundCommunicationThread;
	private Thread networkInterfaceMonitorThread;

	private String remoteIPAddress;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

    public SocketHandler(java.net.Socket socket, ServerMessageNotification serverMessageNotification)
    {
        this.socket = socket;
        this.serverMessageNotification = serverMessageNotification;
        remoteIPAddress = this.socket.getInetAddress().getHostAddress();
    }

    /**
     * Returns the ip address associated with this socket.
     *
     * @return String IP address
     */
    public String getRemoteIpAddress()
    {
        return remoteIPAddress;
    }

	public boolean writeDataPacket(Message packet)
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

    private Message readDataPacket()
    {
        Message message = null;
        boolean streamInitialized = true;

        if (socket.isConnected()) {

            if (objectInputStream == null) {
                streamInitialized = initializeObjectInputStream();
            }

            if (objectInputStream != null && streamInitialized) {
                try {
                    Object object = objectInputStream.readObject();
                    message = (Message) object;
                } catch (ClassNotFoundException ex) {
                    System.out.println("The Serialized Object Not Found");
                    message = null;
                } catch (StreamCorruptedException ex) {
                    message = null;
                } catch (IOException ex) {
                    message = null;
                }
            }
        }

        return message;

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
			Message packet = new Message(MessageType.PING, null);
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
			Message message;

			System.out.println("Waiting for message from Client " + remoteIPAddress);
			while ((message = readDataPacket()) != null) {
				System.out.println("Data Packet Received");
                serverMessageNotification.notifyServerMessageReceived(message);
			}

			System.out.println("Client " + remoteIPAddress + " connection closed...");
		}
	}

}
