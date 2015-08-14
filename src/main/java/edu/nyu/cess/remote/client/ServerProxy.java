/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.*;

import java.util.ArrayList;

/**
 * @author Anwar A. Ruff 
 */
public class ServerProxy implements ClientNetworkInterfaceObserver, ServerProxyObservable {

	private final ArrayList<ServerProxyObserver> observers = new ArrayList<ServerProxyObserver>();

	private static ClientNetworkInterface networkInterface;

	private final int POLL_INTERVAL = 2000; // miliseconds

	public ServerProxy(HostConfigurationInfo hostConfig) {
		networkInterface = new ClientNetworkInterface(hostConfig);
		networkInterface.addObserver(this);

	}

	public void establishPersistentServerConnection() {

		while (true) {
			networkInterface.setServerSocketConnection(POLL_INTERVAL);
			networkInterface.handleInboundPacketRequests();
			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (InterruptedException e) {
			}
			System.out.println("attempting to reconnect to server...");
		}
	}
	
	public void sendServerApplicationState(State state) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_STATE_CHAGE, state);
		networkInterface.writeDataPacket(dataPacket);
	}

	public void addServerProxyObserver(ServerProxyObserver observer) {
		observers.add(observer);
	}

	public void deleteServerProxyObserver(ServerProxyObserver observer) {
		observers.remove(observer);
	}

	public void notifyApplicationExececutionRequestReceived(ExecutionRequest execRequest) {
		for (ServerProxyObserver observer : observers) {
			observer.updateServerExecutionRequestReceived(execRequest);
		}
	}

	public void notifyObserverNetworkStateChanged(boolean isConnected) {
		for (ServerProxyObserver observer : observers) {
			observer.updateNetworkStateChanged(isConnected);
		}
	}

	public void notifyServerMessageReceived(String message) {
		for (ServerProxyObserver observer : observers) {
			observer.updateServerMessageReceived(message);
		}
	}

	public void updateNetworkPacketReceived(DataPacket dataPacket, String ipAddress) {
		System.out.println("Network Packet Received.");
		
		PacketType dataPacketType = dataPacket.getPacketType();
		if (!(dataPacketType instanceof PacketType)) {
			return;
		}
		
		switch(dataPacket.getPacketType()) {
		case APPLICATION_EXECUTION_REQUEST:
			ExecutionRequest execRequest = (ExecutionRequest) dataPacket.getPayload();
			if (execRequest != null && execRequest instanceof ExecutionRequest) {
					System.out.println("Packet Content: ApplicationExecRequest");
					notifyApplicationExececutionRequestReceived(execRequest);
			}
			break;
		case MESSAGE:
			String message = (String) dataPacket.getPayload();
			if (message != null && !message.isEmpty()) {
				notifyServerMessageReceived(message);
			}
			break;
		case APPLICATION_STATE_CHAGE:
		case HOST_INFO:
			// Not supported by the Client
			break;
		case SOCKET_TEST:
			// No processing occurs during a socket test
			break;
		default:
			// Do nothing
			break;
		}
	}

	public void updateNetworkConnectionStateChanged(String ipAddress, boolean isConnected) {
		notifyObserverNetworkStateChanged(isConnected);
	}

}
