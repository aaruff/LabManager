package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMessageSocket implements MessageSocket, SocketObserver, MessageObserver
{
	final static Logger logger = Logger.getLogger(ClientMessageSocket.class);

	private Socket socket;

    private MessageObserver messageObserver;
    private SocketObserver socketObserver;

	private Thread messageMonitorThread;
	private Thread clientKeepAliveMonitor;

	private NetworkInfo networkInfo;


    public ClientMessageSocket(Socket socket, NetworkInfo networkInfo, MessageObserver messageObserver, SocketObserver socketObserver)
    {
        this.socket = socket;
        this.messageObserver = messageObserver;
		this.networkInfo = networkInfo;
        this.socketObserver = socketObserver;

        messageMonitorThread = new Thread(new MessageMonitorThread(getMessageSocketInterfaceFrom(this), messageObserver, networkInfo));
        messageMonitorThread.setName("Inbound Message Monitor Thread");
        messageMonitorThread.start();

        clientKeepAliveMonitor = new Thread(new ClientKeepAliveMonitor(socketObserver, networkInfo));
        clientKeepAliveMonitor.setName("Client Server Keep Alive Monitor Thread");
        clientKeepAliveMonitor.start();
    }

	/**
	 * Returns the network information for this connection.
	 * @return network information
     */
	public NetworkInfo getNetworkInformation()
	{
		return networkInfo;
	}

    /**
     * {@link MessageSocket}
     */
    public Message readMessage() throws IOException
    {
        if ( ! socket.isConnected()) {
            throw new IOException("Attempting to read message from an disconnected socket.");
        }

        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        Object object;
        try {
            object = objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
        Message message = (Message) object;

        return message;
    }

    /**
     * {@link MessageSocket}
     */
	@Override public synchronized void sendMessage(Message message) throws IOException
    {
        if ( ! socket.isConnected()) {
            throw new IOException("Attempting to send an error message using a disconnected socket.");
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
	}

    /**
     * {@link MessageSocket}
     */
    @Override public boolean isConnected()
    {
        return socket.isConnected();
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

    private MessageSocket getMessageSocketInterfaceFrom(ClientMessageSocket clientMessageSocket)
    {
        return clientMessageSocket;
    }

}
