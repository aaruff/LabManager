package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.client.ClientDisconnectionObserver;

import java.io.IOException;

/**
 * The network stream monitor thread is used to periodically (every 40 seconds)
 * poll the client with an empty packet to determine if the socket connection is
 * still established. The termination of this tread is used as a flag to signal
 * that the connection between the server and the client has been broken.
 */
class ClientConnectionMonitor implements Runnable
{
    private MessageSocket messageSocket;
    private ClientDisconnectionObserver clientDisconnectionObserver;

    public ClientConnectionMonitor(MessageSocket messageSocket, ClientDisconnectionObserver clientDisconnectionObserver)
    {
        this.messageSocket = messageSocket;
        this.clientDisconnectionObserver = clientDisconnectionObserver;
    }

    public void run() {
        boolean interfaceState = true;
        String clientIp = messageSocket.getClientIp();
        String serverIp = messageSocket.getServerIp();
        Message pingMessage = new Message(MessageType.KEEP_ALIVE_PING, new NetworkInfo("", clientIp, serverIp));
        /*
         *  Sends an empty packet to the respective client
         *  to determine if the socket connection is still established.
         */
        while (interfaceState) {
            try {
                messageSocket.sendMessage(pingMessage);
                Thread.sleep(40000);
            }
            catch (IOException e) {
                clientDisconnectionObserver.notifyClientDisconnected(new NetworkInfo(clientIp, serverIp));
                interfaceState = false;
            }
            catch (InterruptedException e) {
            }
        }
    }
}
