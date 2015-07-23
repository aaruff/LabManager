package edu.nyu.cess.remote.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import edu.nyu.cess.remote.common.net.ClientNetworkInterfaceObserver;
import edu.nyu.cess.remote.common.net.HostConfigurationInfo;

public class ServerNetworkInterface {

	ArrayList<ClientNetworkInterfaceObserver> observers = new ArrayList<ClientNetworkInterfaceObserver>();

	private ServerSocket serverSocket;

	private String localHostName;
	private String localIPAddress;

	private int localPortNumber;

	public ServerNetworkInterface() {}

	public ServerNetworkInterface(int portNumber) {
		localPortNumber = portNumber;
	}

	public ServerNetworkInterface(HostConfigurationInfo networkContactInfo) {
		localPortNumber = networkContactInfo.getServerPortNumber();
	}

	/**
	 * Returns the network nodes socket mapped to the remote IP Address
	 * (remoteIPAddress).
	 *
	 * @return network nodes socket
	 */
	public ServerSocket getSocket() {
		return serverSocket;
	}

	/**
	 * Returns the network nodes local host name.
	 *
	 * @return local host name
	 */
	public String getLocalHostName() {
		return localHostName;
	}

	/**
	 * Returns this local network nodes IP Address.
	 *
	 * @return local nodes IP Address
	 */
	public String getLocalIPAddress() {
		return localIPAddress;
	}

	/**
	 * Returns the remote port number the socket will be bound to.
	 *
	 * @return remote port number
	 */
	public int getRemotePortNumber() {
		return localPortNumber;
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
