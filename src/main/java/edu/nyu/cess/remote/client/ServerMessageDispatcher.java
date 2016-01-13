/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.net.CommunicationNetworkInterface;
import edu.nyu.cess.remote.common.app.ExeRequestMessage;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.PortWatcher;
import org.apache.log4j.Logger;

/**
 * @author Anwar A. Ruff
 */
public class ServerMessageDispatcher implements PortWatcher, ServerProxyObservable
{
	final static Logger log = Logger.getLogger(Client.class);

	private Client client;

	private CommunicationNetworkInterface commNetworkInterface;

	public ServerMessageDispatcher(CommunicationNetworkInterface communicationNetworkInterface, Client client) {
		this.client = client;
		this.commNetworkInterface = communicationNetworkInterface;
		this.commNetworkInterface.addObserver(this);
	}

	/**
	 * Establishes a persistent connection between the client and the server.
	 */
	public void createPersistentServerConnection() {

		while (true) {
			int pollIntervalMilliseconds = 2000; // milliseconds
			commNetworkInterface.setServerSocketConnection(pollIntervalMilliseconds);
			commNetworkInterface.handleClientServerMessaging();
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
		Message message = new Message(MessageType.STATE_CHANGE, state);
		commNetworkInterface.writeDataPacket(message);
	}

	public void notifyObserversMessageReceived(ExeRequestMessage execRequest) {
        client.updateServerExecutionRequestReceived(execRequest);
	}

	public void notifyObserverServerConnectionStatusChanged(boolean isConnected) {
        client.updateNetworkStateChanged(isConnected);
	}

	public void notifyServerMessageReceived(String message) {
        client.updateServerMessageReceived(message);
	}

	public void readServerMessage(Message dataPacket, String ipAddress) {
		log.info("Server message received.");

		MessageType dataMessageType = dataPacket.getMessageType();
		if (!(dataMessageType instanceof MessageType)) {
			return;
		}

		switch(dataPacket.getMessageType()) {
		case EXECUTION_REQUEST:
			ExeRequestMessage execRequest = (ExeRequestMessage) dataPacket.getPayload();
			if (execRequest != null && execRequest instanceof ExeRequestMessage) {
					log.info("Packet Content: ApplicationExecRequest");
					notifyObserversMessageReceived(execRequest);
			}
			break;
		case USER_NOTIFICATION:
			String message = (String) dataPacket.getPayload();
			if (message != null && !message.isEmpty()) {
				notifyServerMessageReceived(message);
			}
			break;
		case STATE_CHANGE:
		case HOST_INFO:
			// Not supported by the Client
			break;
		case PING:
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
