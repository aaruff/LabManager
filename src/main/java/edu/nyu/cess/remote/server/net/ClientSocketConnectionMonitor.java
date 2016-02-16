/**
 *
 */
package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.server.client.ClientSocketCollection;
import edu.nyu.cess.remote.server.client.ClientSocketPoolManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class waits for new client connections to be established and sends them to the {@link ClientSocketPoolManager}
 * to be managed along with other clients.
 */
public class ClientSocketConnectionMonitor
{
    final static Logger log = Logger.getLogger(ClientSocketConnectionMonitor.class);

    private ClientSocketCollection clientSocketCollection;

    public ClientSocketConnectionMonitor(ClientSocketCollection clientSocketCollection)
    {
        this.clientSocketCollection = clientSocketCollection;
	}

	public void monitorNewClientSocketConnections(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);

        Socket clientSocket;
		while (true) {
            clientSocket = null;
            while (clientSocket == null) {
                try {
                    log.info("Waiting for inbound client connection request.");
                    clientSocket = serverSocket.accept(); // Blocking call
                } catch (IOException e) {
                    log.error("Connection Error", e);
                }
            }

			log.info("Client socket connected.");
            clientSocketCollection.addClientSocket(clientSocket);
		}
	}
}
