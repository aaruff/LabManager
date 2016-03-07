/**
 *
 */
package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.client.ClientPool;
import edu.nyu.cess.remote.server.client.ClientPoolProxy;
import edu.nyu.cess.remote.server.lab.LabLayout;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class waits for new client connections to be established and sends them to the {@link ClientPoolProxy}
 * to be managed along with other clients.
 */
public class ClientSocketConnectionMonitor
{
    final static Logger log = Logger.getLogger(ClientSocketConnectionMonitor.class);

    private ClientPool clientPool;

    public ClientSocketConnectionMonitor(ClientPool clientPool)
    {
        this.clientPool = clientPool;
	}

	public void monitorNewClientSocketConnections(int port, LabLayout labLayout) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);

        Socket clientSocket;
		while (true) {
            clientSocket = null;
            while (clientSocket == null) {
                try {
                    log.debug("Waiting for inbound client connection request.");
                    clientSocket = serverSocket.accept(); // Blocking call
                } catch (IOException e) {
                    log.error("Connection Error", e);
                }
            }

            String remoteIp = clientSocket.getInetAddress().getHostAddress();
            if (! labLayout.getComputersByIp().containsKey(remoteIp)) {
                clientSocket.close();
                log.error("Connection by " + remoteIp + " was rejected. Only clients in lab-layout.yaml are allowed.");
            }
            else {
                log.debug("Client connected: " + remoteIp);

                String clientName = labLayout.getComputersByIp().get(remoteIp).getName();
                String serverIp = clientSocket.getLocalAddress().getHostAddress();
                NetworkInfo clientNetworkInfo = new NetworkInfo(clientName, remoteIp, serverIp);

                clientPool.addClient(new ClientSocket(clientNetworkInfo, clientSocket));
            }
		}
	}
}
