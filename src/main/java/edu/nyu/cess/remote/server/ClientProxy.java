/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.*;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.util.HashMap;

/**
 * The Class ClientProxy.
 */
public class ClientProxy implements ClientNetworkInterfaceObserver
{
    final static Logger logger = Logger.getLogger(ClientProxy.class);

    private Server server;

	/** The client network interfaces used to communicate with clients. */
	HashMap<String, ClientSocket> clientSockets = new HashMap<String, ClientSocket>();

	public ClientProxy(Server server)
    {
        this.server = server;
	}

	public void clientRegistrationHandler()
    {
        int PORT_NUMBER = 2600;
        ServerSocketHandler serverSocketHandler = new ServerSocketHandler(PORT_NUMBER);
		serverSocketHandler.initializeServerSocketConnection();

		while (true) {
			// Blocking function call continues upon incoming connection requests.
			Socket requestSocket = serverSocketHandler.waitForIncomingConnection();
            if (requestSocket == null) continue;

			String ip = requestSocket.getInetAddress().getHostAddress();
            if (ip == null || ip.isEmpty()) continue;

            // Ignore connections that have already been established.
            if ( clientSockets.containsKey(ip)) continue;

            clientSockets.put(ip, new ClientSocket(requestSocket));
            clientSockets.get(ip).addObserver(this);
            server.addClient(ip);

            logger.debug("Client Connected: " + ip);
            clientSockets.get(ip).startThreadedInboundCommunicationMonitor();
		}
	}

	public void updateNetworkPacketReceived(DataPacket dataPacket, String ipAddress)
    {
        logger.debug("Packet received from client " + ipAddress);

		switch(dataPacket.getPacketType()) {
		case APPLICATION_EXECUTION_REQUEST:
			// Not supported by the server
			break;
		case APPLICATION_STATE_CHAGE:
			State appState = (State) dataPacket.getPayload();
			if (appState != null && appState instanceof State) {
                server.updateClientState(ipAddress, appState);
			}
			break;
		case HOST_INFO:
			HostInfo hostInfo = (HostInfo) dataPacket.getPayload();
			String hostName = hostInfo.getHostName();
			
			if(hostName != null && !hostName.isEmpty()) {
                server.updateClientHostNameUpdate(hostName, ipAddress);
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

	public void updateNetworkConnectionStateChanged(String ipAddress, boolean isConnected)
    {
        logger.debug("Client " + ipAddress + " has " + ((isConnected) ? " connected to the server" : " disconnected"));
        if (isConnected) {
            return;
        }

        clientSockets.get(ipAddress).close();
        clientSockets.remove(ipAddress);
        server.removeClient(ipAddress);
	}

	public void startApplicationOnClient(ExecutionRequest applicationExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, applicationExecutionRequest);
		clientSockets.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void stopApplicationOnClient(ExecutionRequest stopExecutionRequest, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, stopExecutionRequest);
		clientSockets.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.MESSAGE, message);
		clientSockets.get(ipAddress).writeDataPacket(dataPacket);
	}
}
