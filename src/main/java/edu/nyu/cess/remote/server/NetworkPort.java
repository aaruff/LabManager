package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.common.net.PortWatcher;
import edu.nyu.cess.remote.common.net.Socket;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;

public class NetworkPort
{
    final static Logger logger = Logger.getLogger(Port.class);

	private ServerSocket serverSocket;
    private PortWatcher portWatcher;

	private int localPortNumber;

	public NetworkPort(int portNumber, PortWatcher portWatcher) {
		localPortNumber = portNumber;
        this.portWatcher = portWatcher;
	}

	/**
	 * Initializes a Server Socket
	 * @return true is the socket is established, otherwise false
	 */
	public boolean initialize() {
		serverSocket = null;
		boolean result = false;
		try {
			serverSocket = new ServerSocket(localPortNumber);
			System.out.println("Server socket established...");
			result = true;
		}
		catch (ConnectException ex) {
			System.out.println("Network Connection Error");
			System.exit(1);
		}
		catch (IOException ex) {
			System.out.println("IO Exception occured...");
			System.exit(1);
		}

		return result;
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
		System.out.println("Wating for inbound client connection request.");

        String ip = null;
        java.net.Socket socket = null;
        while (socket == null || ip == null || ip.isEmpty()) {
            try {
                socket = serverSocket.accept();
                ip = socket.getInetAddress().getHostAddress();
            } catch (IOException e) {
                logger.debug(e.getStackTrace());
            }
        }

        return new Socket(socket, portWatcher);
	}

}
