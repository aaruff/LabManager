/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.app.ExeRequestMessage;
import edu.nyu.cess.remote.common.app.State;
import edu.nyu.cess.remote.common.net.*;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * The Class ClientProxy.
 */
public class Port implements PortWatcher
{
    final static Logger logger = Logger.getLogger(Port.class);

    private Server server;

	private HashMap<String, SocketConnection> sockets = new HashMap<String, SocketConnection>();

	public Port(Server server)
    {
        this.server = server;
	}

	/**
	 * Starts listening for inbound connections on the specified port.
	 * @param port the port used to listen for inbound connections
     */
	public void listen(int port)
    {
        NetworkPort networkPort = new NetworkPort(port, this);
		networkPort.initialize();

		while (true) {
			// Blocking function call continues upon incoming connection requests.
            SocketConnection socketConnection = networkPort.listenForConnections();

            // Ignore connections that have already been established.
            if (sockets.containsKey(socketConnection.getIP())) {
                continue;
            }

            sockets.put(socketConnection.getIP(), socketConnection);
            server.addClient(socketConnection.getIP());
            socketConnection.startThreadedInboundCommunicationMonitor();
		}
	}

	public void processDataPacket(DataPacket dataPacket, String ipAddress)
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

	public void processStateChange(String ipAddress, boolean isConnected)
    {
        logger.debug("Client " + ipAddress + " has " + ((isConnected) ? " connected to the server" : " disconnected"));
        if (isConnected) {
            return;
        }

        sockets.get(ipAddress).close();
        sockets.remove(ipAddress);
        server.removeClient(ipAddress);
	}

	public void startApplicationOnClient(ExeRequestMessage applicationExeRequestMessage, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, applicationExeRequestMessage);
		sockets.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void stopApplicationOnClient(ExeRequestMessage stopExeRequestMessage, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.APPLICATION_EXECUTION_REQUEST, stopExeRequestMessage);
		sockets.get(ipAddress).writeDataPacket(dataPacket);
	}

	public void sendMessageToClient(String message, String ipAddress) {
		DataPacket dataPacket = new DataPacket(PacketType.MESSAGE, message);
		sockets.get(ipAddress).writeDataPacket(dataPacket);
	}
}
