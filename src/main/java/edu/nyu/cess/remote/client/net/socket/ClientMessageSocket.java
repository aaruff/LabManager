package edu.nyu.cess.remote.client.net.socket;

import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class handles the initialization, sending, and receiving of messages via the client socket.
 */
public class ClientMessageSocket implements MessageSocket
{
	private final Socket socket;

	/**
	 * Initialize the client message socket with the required ip address and port.
	 * @param ipAddress Server IP address
	 * @param port Server port
	 * @throws IOException Thrown if an IO error occurs while initializing a socket.
     */
	public ClientMessageSocket(String ipAddress, int port) throws IOException
	{
		socket = new Socket(ipAddress, port);
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
}
