package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class handles the initialization, sending, and receiving of messages via the client socket.
 */
public class ClientMessageSocket implements MessageSocket
{
	private final static Logger log = LoggerFactory.getLogger(MessageSocketManager.class);
	private final NetworkInfo networkInfo;
	private final Socket socket;

	/**
	 * Initialize the client message socket with the required ip address and port.
	 * @param networkInfo client network info
	 * @param port Server port
	 * @throws IOException Thrown if an IO error occurs while initializing a socket.
     */
	public ClientMessageSocket(NetworkInfo networkInfo, int port) throws IOException
	{
		this.networkInfo = networkInfo;
		log.debug("Attempting to create a socket connection to the server({}) from this client({})", networkInfo.getServerIp(), networkInfo.getClientIp());
		socket = new Socket(networkInfo.getServerIp(), port);
	}

	@Override public boolean isConnected()
	{
		return socket.isConnected() && ! socket.isClosed();
	}

	@Override public void sendMessage(Message message) throws IOException
	{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(message);
		objectOutputStream.flush();
	}

	@Override public Message readMessage() throws IOException
	{
		ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
		Object object;
		try {
			object = objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("Error reading object from socket input stream", e);
		}

		return (Message) object;
	}

	@Override public String getClientIp()
	{
		return networkInfo.getClientIp();
	}

	@Override public String getServerIp()
	{
		return networkInfo.getServerIp();
	}

	@Override public String getClientName()
	{
		return networkInfo.getClientName();
	}

	@Override public NetworkInfo getNetworkInfo()
	{
		return networkInfo;
	}
}
