package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageObserver;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.client.ClientDisconnectionObserver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket implements MessageSocket, MessageObserver, ClientDisconnectionObserver, ClientHostNameObservable, AppExeSocket
{
	final static Logger logger = Logger.getLogger(ClientSocket.class);

	private Socket socket;
    private volatile SocketState socketState;
    private volatile boolean networkInfoConfirmed;

    private ClientSocketObserver clientSocketObserver;

    private volatile NetworkInfo networkInfo;

    public ClientSocket(Socket socket, ClientSocketObserver clientSocketObserver)
    {
        this.socketState = SocketState.CONNECTED;
        this.socket = socket;
        this.clientSocketObserver = clientSocketObserver;
        this.networkInfoConfirmed = false;

        startNetworkMonitorThreads();
    }

	/**
	 * {@link MessageObserver}
	 */
	@Override synchronized public void notifyMessageReceived(Message message)
	{
		switch(message.getMessageType()) {
			case NETWORK_INFO_UPDATE:
				if ( ! networkInfoConfirmed) {
					String hostName = message.getNetworkInfo().getClientHostName();
					networkInfo = new NetworkInfo(hostName, networkInfo.getClientIp(), networkInfo.getServerIp());
				}
			case APP_EXE_UPDATE:
				AppExe appExe = message.getAppExe();
				break;
			case APP_EXE_REQUEST:
				// Ignore
				break;
			case KEEP_ALIVE_PING:
				// Ignore
			default:
				break;
		}
	}

    /**
     * {@link MessageSocket}
     */
    @Override synchronized public Message readMessage() throws IOException
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
	@Override synchronized public void sendMessage(Message message) throws IOException
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
    @Override synchronized public boolean isConnected()
    {
        return socket.isConnected();
    }

	/**
	 * {@link ClientDisconnectionObserver}
	 */
    @Override synchronized public void notifyClientDisconnected(NetworkInfo networkInfo)
    {
        socketState = SocketState.DISCONNECTED;

        clientSocketObserver.notifyClientConnectionLost();
        try {
            socket.close();
        } catch (IOException e) {
            logger.error("Failed to close the client socket.", e);
        }
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override synchronized public String getClientHostName()
    {
        return networkInfo.getClientHostName();
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override synchronized public String getClientIp()
    {
        return networkInfo.getClientIp();
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override synchronized public String getServerIp()
    {
        return networkInfo.getClientHostName();
    }

	/**
	 * {@link ClientHostNameObservable}
	 */
    @Override synchronized public boolean isHostNameSet()
    {
        return networkInfo.getClientHostName().isEmpty();
    }

	/**
	 * {@link ClientHostNameObservable}
	 */
    @Override public void notifyHostNameConfirmed()
    {
        clientSocketObserver.notifyClientConfirmed();
    }

	/**
	 * {@link AppExe}
     */
	@Override public void sendAppExe(AppExe appExe)
	{
		try {
			sendMessage(new Message(MessageType.APP_EXE_REQUEST, appExe, networkInfo));
		}
		catch (IOException e) {
			logger.error("Failed to send an execution message.", e);
		}
	}

	private void startNetworkMonitorThreads()
	{
		String clientIp = socket.getInetAddress().getHostAddress();
		String serverIp = socket.getLocalAddress().getHostAddress();
		this.networkInfo = new NetworkInfo("", clientIp, serverIp);

		Thread messageMonitorThread = new Thread(new MessageMonitorThread(this, this));
		messageMonitorThread.setName("Inbound Message Monitor Thread");
		messageMonitorThread.start();

		Thread clientConnectionMonitorThread = new Thread(new ClientConnectionMonitor(this, this));
		clientConnectionMonitorThread.setName("Client Connection Monitor Thread");
		clientConnectionMonitorThread.start();

		Thread clientHostNameConfirmationThread = new Thread(new ClientHostNameConfirmationRunnable(this, this));
		clientHostNameConfirmationThread.setName("Client Network Info Update Thread");
		clientHostNameConfirmationThread.start();
	}

}
