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
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * @author Anwar A. Ruff
 */
public class ServerMessageDispatcher implements PortWatcher, ServerProxyObservable
{
	final static Logger log = Logger.getLogger(Client.class);

	private final ArrayList<MessageDispatchObserver> observers = new ArrayList<MessageDispatchObserver>();

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
	public void createPersistentServerConnection() {

		while (true) {
			int pollIntervalMilliseconds = 2000; // milliseconds
			networkInterface.setServerSocketConnection(pollIntervalMilliseconds);
			networkInterface.handleInboundPacketRequests();
			try {
				log.info("Connected to the server...");
				Thread.sleep(pollIntervalMilliseconds);
			} catch (InterruptedException e) {
				log.error("Polling tread sleep interrupted", e);
			}
			log.info("Attempting to reconnect to the server...");
		}
	}

	public void sendServerApplicationState(State state) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_STATE_CHAGE, state);
		networkInterface.writeDataPacket(dataPacket);
	}

	/**
	 * Adds a message dispatch observer to the list of observers.
	 *
	 * @param messageDispatchObserver
     */
	@Override public void addDispatchObserver(MessageDispatchObserver messageDispatchObserver)
	{
		observers.add(messageDispatchObserver);
	}

	public void removeDispatchObserver(MessageDispatchObserver observer) {
		observers.remove(observer);
	}

	public void notifyObserversMessageReceived(ExeRequestMessage execRequest) {
		for (MessageDispatchObserver observer : observers) {
			observer.updateServerExecutionRequestReceived(execRequest);
		}
	}

	public void notifyObserverServerConnectionStatusChanged(boolean isConnected) {
		for (MessageDispatchObserver observer : observers) {
			observer.updateNetworkStateChanged(isConnected);
		}
	}

	public void notifyServerMessageReceived(String message) {
		for (MessageDispatchObserver observer : observers) {
			observer.updateServerMessageReceived(message);
		}
	}

	public void readServerMessage(DataPacket dataPacket, String ipAddress) {
		log.info("Server message received.");

		PacketType dataPacketType = dataPacket.getPacketType();
		if (!(dataPacketType instanceof PacketType)) {
			return;
		}

		switch(dataPacket.getPacketType()) {
		case APPLICATION_EXECUTION_REQUEST:
			ExeRequestMessage execRequest = (ExeRequestMessage) dataPacket.getPayload();
			if (execRequest != null && execRequest instanceof ExeRequestMessage) {
					log.info("Packet Content: ApplicationExecRequest");
					notifyObserversMessageReceived(execRequest);
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
		notifyObserverServerConnectionStatusChanged(isConnected);
	}

}
