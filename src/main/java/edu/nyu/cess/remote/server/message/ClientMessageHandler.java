/**
 *
 */
package edu.nyu.cess.remote.server.message;

import edu.nyu.cess.remote.common.app.AppExecution;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import edu.nyu.cess.remote.server.client.ClientPoolController;
import edu.nyu.cess.remote.server.net.ClientConnectionObserver;
import edu.nyu.cess.remote.server.net.ClientSocketConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The Class ClientProxy.
 */
public class ClientMessageHandler implements MessageHandler
{
    final static Logger log = Logger.getLogger(ClientMessageHandler.class);

    private ClientConnectionObserver clientConnectionObserver;
	private ClientPoolController clientPoolController;

	private HashMap<String, ClientSocketConnection> clientSocketConnections = new HashMap<>();

	public ClientMessageHandler(ClientConnectionObserver clientConnectionObserver, ClientPoolController clientPoolController)
    {
        this.clientConnectionObserver = clientConnectionObserver;
		this.clientPoolController = clientPoolController;
	}

	/**
	 * Starts listening for inbound connections on the specified port.
	 * @param portNumber the port used to handleNewClientSocketConnectionOn for inbound connections
     */
	public void handleNewClientSocketConnectionOn(int portNumber) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        log.info("Server socket established...");

		while (true) {
			// Blocking function: waits a client socket connection
            ClientSocketConnection newClientSocketConnection = listenForNewClientSocketConnection(serverSocket);

			if ( isNotAlreadyConnected(newClientSocketConnection)) {
				NetworkInformation networkInfo = newClientSocketConnection.getNetworkInformation();
				clientSocketConnections.put(networkInfo.getClientIpAddress(), newClientSocketConnection);
				log.info("New socket connection made by " + networkInfo.getClientIpAddress());

				 // TODO: Add an authentication and network information confirmation stage before the client is added to the pool
				clientConnectionObserver.notifyNewClientConnected(networkInfo);

				newClientSocketConnection.startThreadedInboundCommunicationMonitor();
			}
		}
	}

    /**
     * Returns true if the client socket connection parameter is currently active, otherwise false is returned.
     * @param clientSocketConnection client socket connection
     * @return true if client is already connected, otherwise false is returned
     */
	private boolean isNotAlreadyConnected(ClientSocketConnection clientSocketConnection)
	{
		NetworkInformation networkInfo = clientSocketConnection.getNetworkInformation();
		return ! clientSocketConnections.containsKey(networkInfo.getClientIpAddress());
	}

	public void handleInboundMessage(Message message)
    {
		NetworkInformation networkInfo = message.getNetworkInfo();
        log.debug("Packet received from client " + networkInfo.getClientIpAddress());

		switch(message.getMessageType()) {
		case APPLICATION_EXECUTION:
			// Not supported by the server
			break;
		case STATE_CHANGE:
			//TODO: Implement an AppExecution validator
			AppExecution appExecution = message.getAppExecution();
			if (appExecution != null) {
                clientPoolController.updateClientState(networkInfo.getClientIpAddress(), appExecution);
			}
			break;
		case NETWORK_INFO:
			NetworkInformation networkInformation = message.getNetworkInfo();
			String clientName = networkInformation.getClientName();

			if(clientName != null && !clientName.isEmpty()) {
                clientPoolController.updateClientHostNameUpdate(clientName, networkInfo.getClientIpAddress());
			}
			break;
		case USER_NOTIFICATION:
			// Not supported by the server
			break;
		case PING:
			// No processing is done when a socket test is received
			break;
		default:
			// Do nothing
			break;
		}
	}

	public void processStateChange(String ipAddress, boolean isConnected)
    {
        log.debug("Client " + ipAddress + " has " + ((isConnected) ? " connected to the server" : " disconnected"));
        if (isConnected) {
            return;
        }

        clientSocketConnections.get(ipAddress).closeConnection();
        clientSocketConnections.remove(ipAddress);
        clientPoolController.removeClient(ipAddress);
	}

	public void startApplicationOnClient(AppExecution applicationAppExecution, String ipAddress)
	{
		NetworkInformation networkInfo = new NetworkInformation();
		networkInfo.setClientIpAddress(ipAddress);

		Message message = new Message(MessageType.APPLICATION_EXECUTION, applicationAppExecution);
		clientSocketConnections.get(ipAddress).sendMessage(message);
	}

	public void stopApplicationOnClient(AppExecution stopAppExecution, String ipAddress) {
		Message message = new Message(MessageType.APPLICATION_EXECUTION, stopAppExecution);
		clientSocketConnections.get(ipAddress).sendMessage(message);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		Message dataPacket = new Message(MessageType.USER_NOTIFICATION, message);
		clientSocketConnections.get(ipAddress).sendMessage(dataPacket);
	}

	/**
	 *
	 * Blocks until a network connection request is received, upon which
	 * a Socket is returned.
	 *
	 * @return ClientSocket socket
	 */
	private ClientSocketConnection listenForNewClientSocketConnection(ServerSocket serverSocket)
	{
		log.info("Waiting for inbound client connection request.");

		String clientIpAddress = null;
		Socket socketConnection = null;
		while (socketConnection == null || clientIpAddress == null || clientIpAddress.isEmpty()) {
			try {
				socketConnection = serverSocket.accept(); // Blocking call
				clientIpAddress = socketConnection.getInetAddress().getHostAddress();
			} catch (IOException e) {
				log.debug("Connection Error", e);
			}
		}

		return new ClientSocketConnection(socketConnection, this);
	}
}
