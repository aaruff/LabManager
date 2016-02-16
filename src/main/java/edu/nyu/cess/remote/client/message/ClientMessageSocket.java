package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.net.NetworkInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class handles the initialization, sending, and receiving of messages via the client socket.
 */
public class ClientMessageSocket implements MessageSocket
{
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
		socket = new Socket(networkInfo.getServerIp(), port);
	}

	@Override public synchronized boolean isConnected()
	{
		return socket.isConnected() && ! socket.isClosed();
	}

	@Override public synchronized void sendMessage(Message message) throws IOException
	{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(message);
		objectOutputStream.flush();
	}

	@Override public synchronized Message readMessage() throws IOException
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

	@Override public String getClientHostName()
	{
		return networkInfo.getClientHostName();
	}
}
