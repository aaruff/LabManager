package edu.nyu.cess.remote.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler
{
	private ServerSocket serverSocket;

	private int localPortNumber;

	public ServerSocketHandler(int portNumber) {
		localPortNumber = portNumber;
	}

	/**
	 * Initializes a Server Socket
	 * @return true is the socket is established, otherwise false
	 */
	public boolean initializeServerSocketConnection() {
		serverSocket = null;
		boolean result = false;
		try {
			serverSocket = new ServerSocket(localPortNumber);
			System.out.println("Server socket established...");
			result = true;
		}
		catch (ConnectException ex) {
			System.out.println("Network Connection Error");
			System.exit(1);
		}
		catch (IOException ex) {
			System.out.println("IO Exception occured...");
			System.exit(1);
		}

		return result;
	}

	/**
	 * Blocks until a network connection request is received, upon which
	 * a {@link Socket} is returned.
	 * @return a Socket if successfully established, otherwise null
	 */
	public Socket waitForIncomingConnection() {
		Socket clientSocket = null;
		System.out.println("Wating for inbound client connection request.");
		try {
			clientSocket = serverSocket.accept();
			System.out.println("Client connection accepted");
		}
		catch (IOException e) {}

		return clientSocket;
	}

}
