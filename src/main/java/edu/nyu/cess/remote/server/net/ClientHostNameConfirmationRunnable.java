package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocket;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by aruff on 2/12/16.
 */
public class ClientHostNameConfirmationRunnable implements Runnable
{
    final static Logger log = Logger.getLogger(ClientHostNameConfirmationRunnable.class);

    private final MessageSocket messageSocket;
    private final ClientHostNameObservable hostNameObservable;

    public ClientHostNameConfirmationRunnable(MessageSocket messageSocket, ClientHostNameObservable hostNameObservable)
    {
        this.messageSocket = messageSocket;
        this.hostNameObservable = hostNameObservable;
    }

    @Override
    public void run()
    {
        String clientIp = messageSocket.getClientIp();
        String serverIp = messageSocket.getServerIp();
        Message networkUpdateMessage = new Message(MessageType.NETWORK_INFO_UPDATE, new NetworkInfo(clientIp, serverIp));
        try {
            boolean hostnameNotConfirmed = true;
            while (hostnameNotConfirmed) {
                messageSocket.sendMessage(networkUpdateMessage);
                try {
                    Thread.sleep(40000);
                }
                catch (InterruptedException e) {}

                if (hostNameObservable.isHostNameSet()) {
                    hostnameNotConfirmed = false;
                }
            }
        }
        catch (IOException e)
        {
            log.error("Failed to confirm the network host name.", e);
        }
    }
}
