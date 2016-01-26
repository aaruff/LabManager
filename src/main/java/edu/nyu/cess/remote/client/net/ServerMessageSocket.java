package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by aruff on 1/25/16.
 */
public class ServerMessageSocket implements MessageSocket
{
	private final Socket socket;

	public ServerMessageSocket(String ipAddress, int port) throws IOException
	{
		socket = new Socket(ipAddress, port);
	}

	@Override public synchronized boolean isConnected()
	{
		return socket != null && socket.isConnected() && ! socket.isClosed();
	}


	@Override public synchronized void sendMessage(Message packet) throws IOException
	{
		if ( ! isConnected()) {
			throw new IOException("Error attempting to send a message with an invalid socket");
		}
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(packet);
		objectOutputStream.flush();
	}

	@Override public synchronized Message readMessage() throws IOException {
		if ( ! isConnected()) {
			throw new IOException("Error attempting to read a message with an invalid socket");
		}

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
