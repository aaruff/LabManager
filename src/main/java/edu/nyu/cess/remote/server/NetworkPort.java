package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.net.PortWatcher;
import edu.nyu.cess.remote.common.net.Socket;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;

public class NetworkPort
{
    final static Logger log = Logger.getLogger(NetworkPort.class);

	private ServerSocket serverSocket;
    private PortWatcher portWatcher;

	private int localPortNumber;

	public NetworkPort(int portNumber, PortWatcher portWatcher) {
		localPortNumber = portNumber;
        this.portWatcher = portWatcher;
	}

	/**
	 * Initializes a Server Socket
	 */
	public void initialize() {
		serverSocket = null;
		try {
			serverSocket = new ServerSocket(localPortNumber);
			log.debug("Server socket established...");
		}
		catch (ConnectException ex) {
			log.error("Network Connection Error", ex);
			System.exit(1);
		}
		catch (IOException ex) {
			log.error("IO Exception occurred...", ex);
			System.exit(1);
		}
	}

    /**
     *
     * Blocks until a network connection request is received, upon which
     * a Socket is returned.
     *
     * @return ClientSocket socket
     */
	public Socket listenForConnections()
    {
		log.debug("Wating for inbound client connection request.");

        String ip = null;
        java.net.Socket socket = null;
        while (socket == null || ip == null || ip.isEmpty()) {
            try {
                socket = serverSocket.accept();
                ip = socket.getInetAddress().getHostAddress();
            } catch (IOException e) {
                log.debug("Connection Error", e);
            }
        }

        return new Socket(socket, portWatcher);
	}

}
