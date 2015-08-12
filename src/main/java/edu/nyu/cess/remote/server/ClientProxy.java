/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
				clientNetworkInterface.addClientNetworkInterfaceObserver(this);

				clientNetworkInterfaces.put(clientNetworkInterface.getRemoteIPAddress(), clientNetworkInterface);

				notifyNewClientConnectionEstablished(clientNetworkInterface.getRemoteIPAddress());

				clientNetworkInterface.startThreadedInboundCommunicationMonitor();
			}
		}
	}

	public void updateNetworkPacketReceived(DataPacket dataPacket, String ipAddress) {
		System.out.println("Packet received from client " + ipAddress);
		
		switch(dataPacket.getPacketType()) {
		case APPLICATION_EXECUTION_REQUEST:
			// Not supported by the server
			break;
		case APPLICATION_STATE_CHAGE:
			State appState = (State) dataPacket.getPayload();
			if (appState != null && appState instanceof State) {
				notifyApplicationStateReceived(appState, ipAddress);
			} 
			break;
		case HOST_INFO:
			HostInfo hostInfo = (HostInfo) dataPacket.getPayload();
			String hostName = hostInfo.getHostName();
			
			if(hostName != null && !hostName.isEmpty()) {
				notifyClientHostNameUpdate(hostName, ipAddress);
			}
			break;
		case MESSAGE:
			// Not supported by the server
			break;
		case SOCKET_TEST:
			// No processing is done when a socket test is received
			break;
		default:
			// Do nothing
			break;
		}
	}

	public void updateNetworkConnectionStateChanged(String ipAddress, boolean isConnected) {

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
			clientProxyObserver.updateApplicationStateChanged(ipAddress, applicationState);
		}
	}
	
	public void notifyClientHostNameUpdate(String hostName, String ipAddress) {
		for (ClientProxyObserver clientProxyObserver : observers) {
			clientProxyObserver.updateClientHostNameUpdate(hostName, ipAddress);
		}
	}

	public void notifyNewClientConnectionEstablished(String ipAddress) {
		for (ClientProxyObserver observer : observers) {
			observer.updateNewClientConnected(ipAddress);
		}
	}

	public void notifyNetworkStatusChange(String ipAddress, boolean isConnected) {
		for (ClientProxyObserver observer : observers) {
			observer.updateClientConnectionStateChanged(ipAddress, isConnected);
		}
	}

	public void startApplicationOnClient(ExecutionRequest applicationExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, applicationExecutionRequest);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void stopApplicationOnClient(ExecutionRequest stopExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, stopExecutionRequest);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.MESSAGE, message);
		clientNetworkInterfaces.get(ipAddress).writeDataPacket(dataPacket);
	}
}
