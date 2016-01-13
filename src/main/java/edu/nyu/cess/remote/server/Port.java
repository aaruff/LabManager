/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * The Class ClientProxy.
 */
public class Port implements PortWatcher
{
    final static Logger log = Logger.getLogger(Port.class);

    private Server server;

	private HashMap<String, SocketHandler> clientSocketConnections = new HashMap<String, SocketHandler>();

	public Port(Server server)
    {
        this.server = server;
	}

	/**
	 * Starts listening for inbound connections on the specified port.
	 * @param portNumber the port used to listenForSocketConnectionOnPort for inbound connections
     */
	public void listenForSocketConnectionOnPort(int portNumber) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        log.debug("Server socket established...");

		while (true) {
			// Blocking function: waits a client socket connection
            SocketHandler socketHandler = listenForClientSocketConnection(serverSocket);

			if ( ! isAlreadyConnected(socketHandler)) {
				clientSocketConnections.put(socketHandler.getRemoteIpAddress(), socketHandler);
				log.debug("New socket connection made by " + socketHandler.getRemoteIpAddress());

				server.addClient(socketHandler.getRemoteIpAddress());
				socketHandler.startThreadedInboundCommunicationMonitor();
			}
		}
	}

    /**
     * Returns true if the client socket connection parameter is currently active, otherwise false is returned.
     * @param socketHandler client socket connection
     * @return true if client is already connected, otherwise false is returned
     */
	private boolean isAlreadyConnected(SocketHandler socketHandler) {
		return clientSocketConnections.containsKey(socketHandler.getRemoteIpAddress());
	}

	public void readServerMessage(Message message, String ipAddress)
    {
        log.debug("Packet received from client " + ipAddress);

		switch(message.getMessageType()) {
		case EXECUTION_REQUEST:
			// Not supported by the server
			break;
		case STATE_CHANGE:
			State appState = (State) message.getPayload();
			if (appState != null && appState instanceof State) {
                server.updateClientState(ipAddress, appState);
			}
			break;
		case HOST_INFO:
			ClientServerNetworkInfo clientServerNetworkInfo = (ClientServerNetworkInfo) message.getPayload();
			String clientName = clientServerNetworkInfo.getClientName();

			if(clientName != null && !clientName.isEmpty()) {
                server.updateClientHostNameUpdate(clientName, ipAddress);
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

        clientSocketConnections.get(ipAddress).close();
        clientSocketConnections.remove(ipAddress);
        server.removeClient(ipAddress);
	}

	public void startApplicationOnClient(ExeRequestMessage applicationExeRequestMessage, String ipAddress) {
		Message message = new Message(MessageType.EXECUTION_REQUEST, applicationExeRequestMessage);
		clientSocketConnections.get(ipAddress).writeDataPacket(message);
	}

	public void stopApplicationOnClient(ExeRequestMessage stopExeRequestMessage, String ipAddress) {
		Message message = new Message(MessageType.EXECUTION_REQUEST, stopExeRequestMessage);
		clientSocketConnections.get(ipAddress).writeDataPacket(message);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		Message dataPacket = new Message(MessageType.USER_NOTIFICATION, message);
		clientSocketConnections.get(ipAddress).writeDataPacket(dataPacket);
	}

	/**
	 *
	 * Blocks until a network connection request is received, upon which
	 * a Socket is returned.
	 *
	 * @return ClientSocket socket
	 */
	private SocketHandler listenForClientSocketConnection(ServerSocket serverSocket)
	{
		log.debug("Waiting for inbound client connection request.");

		String remoteIpAddress = null;
		java.net.Socket socket = null;
		while (socket == null || remoteIpAddress == null || remoteIpAddress.isEmpty()) {
			try {
				socket = serverSocket.accept(); // Blocking call
				remoteIpAddress = socket.getInetAddress().getHostAddress();
			} catch (IOException e) {
				log.debug("Connection Error", e);
			}
		}

		return new SocketHandler(socket, this);
	}
}
