/**
 *
 */
package edu.nyu.cess.remote.server.message;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.ExeRequestObserver;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.client.ClientPoolController;
import edu.nyu.cess.remote.server.net.ClientConnectionObserver;
import edu.nyu.cess.remote.server.net.ClientMessageSocket;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The Class ClientProxy.
 */
public class ServerSocketManager implements ExeRequestObserver
{
    final static Logger log = Logger.getLogger(ServerSocketManager.class);

    private ClientConnectionObserver clientConnectionObserver;
	private ClientPoolController clientPoolController;

	private HashMap<String, ClientMessageSocket> clientSocketConnections = new HashMap<>();

	public ServerSocketManager(ClientConnectionObserver clientConnectionObserver, ClientPoolController clientPoolController)
    {
        this.clientConnectionObserver = clientConnectionObserver;
		this.clientPoolController = clientPoolController;
	}

	public void startListening(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);

		while (true) {
			// Blocking function: waits a client socket connection
            ClientMessageSocket newClientSocketConnection = listenForNewClientSocketConnection(serverSocket);

			if ( isNotAlreadyConnected(newClientSocketConnection)) {
				NetworkInfo networkInfo = newClientSocketConnection.getNetworkInformation();
				clientSocketConnections.put(networkInfo.getClientIpAddress(), newClientSocketConnection);
				log.info("New socket connection made by " + networkInfo.getClientIpAddress());

				 // TODO: Add an authentication and network information confirmation stage before the client is added to the pool
				//clientConnectionObserver.notifyNewClientConnected(networkInfo);
			}
		}
	}

	/**
	 * {@link ExeRequestObserver}
	 */
	public synchronized void notifyAppExecution(AppExe appExe, String ipAddress)
	{
		NetworkInfo networkInfo = new NetworkInfo();
		networkInfo.setClientIpAddress(ipAddress);
		networkInfo.setServerIpAddress(serverIpAddress);

		Message message = new Message(MessageType.APPLICATION_EXECUTION, appExe, );
		return clientSocketConnections.get(ipAddress).sendMessage(message);
	}

	/**
	 * {@link MessageObserver}
	 */
	public synchronized boolean handleOutboundKeepAlive(NetworkInfo networkInfo)
	{
		networkInfo.setServerIpAddress(serverIpAddress);
		Message message = new Message(MessageType.KEEP_ALIVE_PING, networkInfo);
		return clientSocketConnections.get(networkInfo.getClientIpAddress()).sendMessage(message);
	}

	/**
	 * {@link MessageObserver}
	 */
	public synchronized boolean handleOutboundMessage(String message, String ipAddress)
	{
		Message dataPacket = new Message(MessageType.USER_NOTIFICATION, message);
		return clientSocketConnections.get(ipAddress).sendMessage(dataPacket);
	}

	/**
	 * {@link MessageObserver}
	 */
	public synchronized void notifyInboundMessage(Message message, NetworkInfo networkInfo)
	{
		log.info("Message received from client " + networkInfo.getClientIpAddress());

		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
				// Not supported by the server
				break;
			case STATE_UPDATE:
				//TODO: Implement an AppExecution validator
				AppExe appExe = message.getAppExe();
				if (appExe != null) {
					clientPoolController.updateClientState(networkInfo.getClientIpAddress(), appExe);
				}
				break;
			case APPLICATION_STATE_UPDATE:
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

	private MessageObserver getMessageHandlerFrom(ServerSocketManager clientMessageHandler)
	{
		return clientMessageHandler;
	}

	/**
	 * Returns true if the client socket connection parameter is currently active, otherwise false is returned.
	 * @param clientSocketConnection client socket connection
	 * @return true if client is already connected, otherwise false is returned
	 */
	private boolean isNotAlreadyConnected(ClientMessageSocket clientSocketConnection)
	{
		NetworkInfo networkInfo = clientSocketConnection.getNetworkInformation();
		return ! clientSocketConnections.containsKey(networkInfo.getClientIpAddress());
	}

	/**
	 *
	 * Blocks until a network connection request is received, upon which
	 * a Socket is returned.
	 *
	 * @return ClientSocket socket
	 */
	private ClientMessageSocket listenForNewClientSocketConnection(ServerSocket serverSocket)
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

		NetworkInfo networkInfo = new NetworkInfo("", clientIpAddress, serverIpAddress);
		return new ClientMessageSocket(socketConnection, networkInfo, getMessageHandlerFrom(this));
	}
}
