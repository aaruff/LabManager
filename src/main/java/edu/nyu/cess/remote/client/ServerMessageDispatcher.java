/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.config.HostConfigInterface;
import edu.nyu.cess.remote.client.net.CommunicationNetworkInterface;
import edu.nyu.cess.remote.common.app.ExeRequestMessage;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.DataPacket;
import edu.nyu.cess.remote.common.net.PacketType;
import edu.nyu.cess.remote.common.net.PortWatcher;

import java.util.ArrayList;

/**
 * @author Anwar A. Ruff
 */
public class ServerMessageDispatcher implements PortWatcher, ServerProxyObservable {

	private final ArrayList<ServerProxyObserver> observers = new ArrayList<ServerProxyObserver>();

	private static CommunicationNetworkInterface networkInterface;

	/**
	 * Initialize the network interface and add this as an observer.
	 * @param hostConfig
     */
	public ServerMessageDispatcher(HostConfigInterface hostConfig) {
		networkInterface = new CommunicationNetworkInterface(hostConfig);
		networkInterface.addObserver(this);
	}

	/**
	 * Establishes a persistent connection between the client and the server.
	 */
	public void establishPersistentServerConnection() {

		while (true) {
			int pollIntervalMilliseconds = 2000; // milliseconds
			networkInterface.setServerSocketConnection(pollIntervalMilliseconds);
			networkInterface.handleInboundPacketRequests();
			try {
				Thread.sleep(pollIntervalMilliseconds);
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

	public void notifyApplicationExececutionRequestReceived(ExeRequestMessage execRequest) {
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

	public void processDataPacket(DataPacket dataPacket, String ipAddress) {
		System.out.println("Network Packet Received.");

		PacketType dataPacketType = dataPacket.getPacketType();
		if (!(dataPacketType instanceof PacketType)) {
			return;
		}

		switch(dataPacket.getPacketType()) {
		case APPLICATION_EXECUTION_REQUEST:
			ExeRequestMessage execRequest = (ExeRequestMessage) dataPacket.getPayload();
			if (execRequest != null && execRequest instanceof ExeRequestMessage) {
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

	public void processStateChange(String ipAddress, boolean isConnected) {
		notifyObserverNetworkStateChanged(isConnected);
	}

}
