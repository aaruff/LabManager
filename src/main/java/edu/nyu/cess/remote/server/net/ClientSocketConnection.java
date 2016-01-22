package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.Message;
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

	private Thread messageMonitorThread;
	private Thread clientKeepAliveMonitor;

	private NetworkInformation networkInfo;


    public ClientSocketConnection(Socket socket, NetworkInformation networkInfo, MessageHandler messageHandler)
    {
        this.socket = socket;
        this.messageHandler = messageHandler;
		this.networkInfo = networkInfo;
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

	public void close()
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

		if (messageMonitorThread != null) {
			if (messageMonitorThread.isAlive()) {
				messageMonitorThread.interrupt();
				try {
					messageMonitorThread.join();
				} catch (InterruptedException e) {
					logger.error("LiteClientInterface Close(): Failed to join inbound communication thread", e);
				}
			}
		}

		if (clientKeepAliveMonitor != null) {
			if (clientKeepAliveMonitor.isAlive()) {
				clientKeepAliveMonitor.interrupt();

				try {
					clientKeepAliveMonitor.join();
				} catch (InterruptedException e) {
					logger.error("LiteClientInterface Close(): Failed to join network monitor thread", e);
				}
			}
		}
	}

	public void startConnectionMessageMonitor()
	{
		messageMonitorThread = new Thread(new MessageMonitorThread(socket, messageHandler, networkInfo));
		messageMonitorThread.setName("Inbound Message Monitor Thread");
		messageMonitorThread.start();
	}

	public void startClientKeepAliveMonitor()
    {
        clientKeepAliveMonitor = new Thread(new ClientKeepAliveMonitor(messageHandler, networkInfo));
        clientKeepAliveMonitor.setName("Client Server Keep Alive Monitor Thread");
        clientKeepAliveMonitor.start();
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


}
