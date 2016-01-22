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
	 * {@link MessageHandler}
     */
	public void initializeMessageHandler() throws IOException
    {
		int port = 2600;
        ServerSocket serverSocket = new ServerSocket(port);

		while (true) {
			// Blocking function: waits a client socket connection
            ClientSocketConnection newClientSocketConnection = listenForNewClientSocketConnection(serverSocket);

			if ( isNotAlreadyConnected(newClientSocketConnection)) {
				NetworkInformation networkInfo = newClientSocketConnection.getNetworkInformation();
				clientSocketConnections.put(networkInfo.getClientIpAddress(), newClientSocketConnection);
				log.info("New socket connection made by " + networkInfo.getClientIpAddress());

				 // TODO: Add an authentication and network information confirmation stage before the client is added to the pool
				//clientConnectionObserver.notifyNewClientConnected(networkInfo);

				newClientSocketConnection.startConnectionMessageMonitor();
				newClientSocketConnection.startClientKeepAliveMonitor();
			}
		}
	}

	/**
	 * {@link MessageHandler}
	 */
	public synchronized boolean handleOutboundAppExecution(AppExecution applicationAppExecution, String ipAddress)
	{
		NetworkInformation networkInfo = new NetworkInformation();
		networkInfo.setClientIpAddress(ipAddress);
		networkInfo.setServerIpAddress(serverIpAddress);

		Message message = new Message(MessageType.APPLICATION_EXECUTION, applicationAppExecution, );
		return clientSocketConnections.get(ipAddress).sendMessage(message);
	}

	/**
	 * {@link MessageHandler}
	 */
	public synchronized boolean handleOutboundKeepAlive(NetworkInformation networkInfo)
	{
		networkInfo.setServerIpAddress(serverIpAddress);
		Message message = new Message(MessageType.KEEP_ALIVE_PING, networkInfo);
		return clientSocketConnections.get(networkInfo.getClientIpAddress()).sendMessage(message);
	}

	/**
	 * {@link MessageHandler}
	 */
	public synchronized boolean handleOutboundMessage(String message, String ipAddress)
	{
		Message dataPacket = new Message(MessageType.USER_NOTIFICATION, message);
		return clientSocketConnections.get(ipAddress).sendMessage(dataPacket);
	}

	/**
	 * {@link MessageHandler}
	 */
	public synchronized void handleInboundMessage(Message message, NetworkInformation networkInfo)
	{
		log.info("Message received from client " + networkInfo.getClientIpAddress());

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
			case NETWORK_INFO_UPDATE:
				clientConnectionObserver.notifyNewClientConnected(message.getNetworkInfo());

				if(clientName != null && !clientName.isEmpty()) {
					clientPoolController.updateClientHostNameUpdate(clientName, networkInfo.getClientIpAddress());
				}
				break;
			case USER_NOTIFICATION:
				// Not supported by the server
				break;
			case KEEP_ALIVE_PING:
				// No processing is done when a socket test is received
				break;
			default:
				// Do nothing
				break;
		}
	}

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */

	public void processStateChange(String ipAddress, boolean isConnected)
    {
        log.info("Client " + ipAddress + " has " + ((isConnected) ? " connected to the server" : " disconnected"));
        if (isConnected) {
            return;
        }

        clientSocketConnections.get(ipAddress).close();
        clientSocketConnections.remove(ipAddress);
        clientPoolController.removeClient(ipAddress);
	}

	private MessageHandler getMessageHandlerFrom(ClientMessageHandler clientMessageHandler)
	{
		return clientMessageHandler;
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
		String serverIpAddress = null;
		Socket socketConnection = null;
		while (socketConnection == null || clientIpAddress == null || clientIpAddress.isEmpty()) {
			try {
				socketConnection = serverSocket.accept(); // Blocking call
				clientIpAddress = socketConnection.getInetAddress().getHostAddress();
				serverIpAddress = socketConnection.getLocalAddress().getHostAddress();
			} catch (IOException e) {
				log.error("Connection Error", e);
			}
		}

		NetworkInformation networkInfo = new NetworkInformation();
		networkInfo.setClientIpAddress(clientIpAddress);
		networkInfo.setClientIpAddress(serverIpAddress);
		return new ClientSocketConnection(socketConnection, networkInfo, getMessageHandlerFrom(this));
	}
}
