package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket implements MessageSocket
{
	final static Logger logger = Logger.getLogger(ClientSocket.class);

	private final Object inboundSocketLock = new Object();
	private final Object outboundSocketLock = new Object();

	private Socket socket;

    private volatile NetworkInfo networkInfo;

    public ClientSocket(NetworkInfo networkInfo, Socket socket)
    {
		this.networkInfo = networkInfo;
		this.socket = socket;
    }

    /**
     * {@link MessageSocket}
     */
    @Override public Message readMessage() throws IOException
    {
		synchronized (inboundSocketLock) {
			if (!socket.isConnected()) {
				throw new IOException("Attempting to read message from an disconnected socket.");
			}

			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

			Object object;
			try {
				object = objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
			return (Message) object;
		}
    }

    /**
     * {@link MessageSocket}
     */
	@Override public void sendMessage(Message message) throws IOException
    {
		synchronized (outboundSocketLock) {
			if ( ! socket.isConnected()) {
				throw new IOException("Attempting to send an error message using a disconnected socket.");
			}

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectOutputStream.writeObject(message);
			objectOutputStream.flush();
		}
	}

	/**
	 * {@link MessageSocket}
	 */
	public NetworkInfo getNetworkInfo()
	{
		return networkInfo;
	}

    /**
     * {@link MessageSocket}
     */
    @Override public boolean isConnected()
    {
        return socket.isConnected();
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override public String getClientName()
    {
        return networkInfo.getClientName();
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override public String getClientIp()
    {
        return networkInfo.getClientIp();
    }

	/**
	 * {@link MessageSocket}
	 */
    @Override public String getServerIp()
    {
        return networkInfo.getClientName();
    }
}
