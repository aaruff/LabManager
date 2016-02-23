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
public class ClientNameRequestRunnable implements Runnable
{
    final static Logger log = Logger.getLogger(ClientNameRequestRunnable.class);

    private final MessageSocket messageSocket;

    public ClientNameRequestRunnable(MessageSocket messageSocket)
    {
        this.messageSocket = messageSocket;
    }

    @Override
    public void run()
    {
        String clientIp = messageSocket.getClientIp();
        String serverIp = messageSocket.getServerIp();
        Message networkUpdateMessage = new Message(MessageType.NETWORK_INFO_UPDATE, new NetworkInfo(clientIp, serverIp));
		boolean interrupted = Thread.currentThread().isInterrupted();
        while ( ! interrupted) {
            log.debug("Sending client " + clientIp + " a host name confirmation message.");
			try {
                messageSocket.sendMessage(networkUpdateMessage);
                Thread.sleep(1000);
				interrupted = Thread.currentThread().isInterrupted();
            }
            catch (InterruptedException e) {
				interrupted = true;
                log.debug("Thread interrupted while attempting to sleep.");
            }
			catch (IOException e) {
				interrupted = true;
				log.error("Failed to confirm the network host name.", e);
			}
        }
    }
}
