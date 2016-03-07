package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.client.ClientDisconnectionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The network stream monitor thread is used to periodically (every 40 seconds)
 * poll the client with an empty packet to determine if the socket connection is
 * still established. The termination of this tread is used as a flag to signal
 * that the connection between the server and the client has been broken.
 */
public class ClientConnectionMonitor implements Runnable
{
	private final static Logger log = LoggerFactory.getLogger(ClientConnectionMonitor.class);
    private MessageSocket messageSocket;
    private ClientDisconnectionObserver clientDisconnectionObserver;

    public ClientConnectionMonitor(MessageSocket messageSocket, ClientDisconnectionObserver clientDisconnectionObserver)
    {
        this.messageSocket = messageSocket;
        this.clientDisconnectionObserver = clientDisconnectionObserver;
    }

    public void run() {
        boolean interfaceState = true;
		String clientName = messageSocket.getClientName();
        String clientIp = messageSocket.getClientIp();
        String serverIp = messageSocket.getServerIp();
        Message appUpdateMessage = new Message(MessageType.APP_EXE_UPDATE, new NetworkInfo(clientName, clientIp, serverIp));
        /*
         *  Sends an empty packet to the respective client
         *  to determine if the socket connection is still established.
         */
        while (interfaceState) {
            try {
                messageSocket.sendMessage(appUpdateMessage);
                Thread.sleep(60000);
            }
            catch (IOException e) {
				log.info("Socket connection to ({}) lost", clientName);
                interfaceState = false;
            }
            catch (InterruptedException e) {
				interfaceState = false;
            }
        }

		clientDisconnectionObserver.notifyClientDisconnected(clientIp);
    }
}
