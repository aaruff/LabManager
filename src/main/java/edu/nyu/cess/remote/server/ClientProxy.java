/**
 *
 */
package edu.nyu.cess.remote.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.ClientNetworkInterfaceObserver;
import edu.nyu.cess.remote.common.net.DataPacket;
import edu.nyu.cess.remote.common.net.LiteClientNetworkInterface;

/**
 * The Class ClientProxy.
 */
public class ClientProxy implements ClientNetworkInterfaceObserver, ClientProxyObservable {

	private ServerNetworkInterface serverNetworkInterface;

	private final int PORT_NUMBER = 2600;

	/** Used to contact ClientProxyObservers when client data is recieved. */
	ArrayList<ClientProxyObserver> observers = new ArrayList<ClientProxyObserver>();

	/** The client network interfaces used to communicate with clients. */
	HashMap<String, LiteClientNetworkInterface> clientNetworkInterfaces = new HashMap<String, LiteClientNetworkInterface>();

	public ClientProxy() {

	}

	/**
	 * init() blocks until a socket connections requests is received from a remote clients.
	 * Upon each socket connection request the following steps occur:
	 * 		1. A  LiteClientNetworkInterface is created for management of the socket connection.
	 * 		2. The LiteClientNetworkInterface is added to a collection clientNetworkInterfaces.
	 * 		3. The ClientProxyObserver is notified that I new socket connection has been made.
	 * 		4. The ClientNetworkInterface starts a network communication monitoring thread.
	 */
	public void connectionRequestHandler() {
		serverNetworkInterface = new ServerNetworkInterface(PORT_NUMBER);
		serverNetworkInterface.initializeServerSocketConnection();

		while (true) {
			// Blocks until a socket connection request is received
			Socket clientSocket = serverNetworkInterface.waitForIncomingConnection();
			
			String IPAddress = clientSocket.getInetAddress().getHostAddress();
			if (IPAddress != null && !IPAddress.isEmpty() && clientNetworkInterfaces.get(IPAddress) == null) {
				LiteClientNetworkInterface clientNetworkInterface = new LiteClientNetworkInterface();
				System.out.println("Client Connected: " + clientNetworkInterface.getRemoteIPAddress());

				clientNetworkInterface.setSocket(clientSocket);
				clientNetworkInterface.addObserver(this);
				clientNetworkInterfaces.put(clientNetworkInterface.getRemoteIPAddress(), clientNetworkInterface);

				notifyNetworkClientAdded(clientNetworkInterface.getRemoteIPAddress());

				clientNetworkInterface.startThreadedInboundCommunicationMonitor();
			}
		}
	}

	public void networkPacketUpdate(DataPacket dataPacket, String ipAddress) {
		State appState = null;

		System.out.println("Packet received from client " + ipAddress);

		Object obj = dataPacket.getContent();
		if (obj != null) {
			if ((appState = (State) obj) instanceof State) {
				notifyApplicationStateReceived(appState, ipAddress);
			}
		}
	}

	public void networkStatusUpdate(String ipAddress, boolean isConnected) {

		System.out.println("Client " + ipAddress + " has "
				+ ((isConnected) ? " connected to the server" : " disconnected"));

		if (!isConnected) {
			clientNetworkInterfaces.get(ipAddress).close();
			clientNetworkInterfaces.remove(ipAddress);
		}

		notifyNetworkStatusChange(ipAddress, isConnected);
	}

	public boolean addObserver(ClientProxyObserver clientProxyObserver) {
		return observers.add(clientProxyObserver);
	}

	public boolean deleteObserver(ClientProxyObserver clientProxyObserver) {
		return observers.remove(clientProxyObserver);
	}

	public void notifyApplicationStateReceived(State applicationState, String ipAddress) {
		for (ClientProxyObserver clientProxyObserver : observers) {
			clientProxyObserver.applicationStateUpdate(ipAddress, applicationState);
		}
	}

	public void notifyNetworkClientAdded(String ipAddress) {
		for (ClientProxyObserver observer : observers) {
			observer.newClientUpdate(ipAddress);
		}
	}

	public void notifyNetworkStatusChange(String ipAddress, boolean isConnected) {
		for (ClientProxyObserver observer : observers) {
			observer.networkStatusUpdate(ipAddress, isConnected);
		}
	}

	public void startApplicationOnClient(ExecutionRequest applicationExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(applicationExecutionRequest);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void stopApplicationOnClient(ExecutionRequest stopExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(stopExecutionRequest);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		DataPacket dataPacket = new DataPacket(message);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}
}
