/**
 *
 */
package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageSocketObserver;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.net.ClientConnectionMonitor;
import edu.nyu.cess.remote.server.net.ClientSocket;
import edu.nyu.cess.remote.server.net.MessageMonitorThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This class handles all clientAppExecutions that are currently connected to the server, and forwards
 * the client state changes to the the client observer.
 */
public class ClientPoolProxy implements ClientPoolExecutionManager, ClientPoolObservable, ClientPool, MessageSocketObserver, ClientDisconnectionObserver
{
	private final static Logger log = LoggerFactory.getLogger(ClientPoolProxy.class);

	private Map<String, AppExe> clientAppExecutions = new HashMap<>();
	private Map<String, ClientSocket> clientSockets = new HashMap<>();

    private ClientPoolObserver clientPoolObserver;

	/**
	 * {@link ClientPoolObservable}
     */
    public void addObserver(ClientPoolObserver clientPoolObserver)
    {
        this.clientPoolObserver = clientPoolObserver;
    }

	/**
	 * {@link ClientDisconnectionObserver}
	 */
	@Override public void notifyClientDisconnected(String clientIp)
	{
		clientAppExecutions.remove(clientIp);
		clientSockets.remove(clientIp);

		clientPoolObserver.notifyClientDisconnected(clientIp);
	}

	/**
	 * {@link MessageSocketObserver}
	 */
	@Override public void notifyMessageReceived(NetworkInfo networkInfo, Message message)
	{
		if ( ! clientSockets.containsKey(networkInfo.getClientIp())) {
			log.error("Message received from an unknown client: {} ", networkInfo.getClientIp());
			return;
		}

		switch(message.getMessageType()) {
			case APP_EXE_UPDATE:
                log.debug("App execution update received from: {}", networkInfo.getClientName());
				AppExe appExe = message.getAppExe();
                handleAppExeUpdate(networkInfo, appExe);
				break;
			case APP_EXE_REQUEST:
				// Ignore
				break;
			case KEEP_ALIVE_PING:
				// Ignore
			default:
				break;
		}
	}

	@Override public void notifyMessageSenderState(ConnectionState connectionState)
	{

	}

	private void handleAppExeUpdate(NetworkInfo networkInfo, AppExe latestAppExe)
    {
        boolean hasAppExe = clientAppExecutions.containsKey(networkInfo.getClientIp());
        if (hasAppExe && clientAppExecutions.get(networkInfo.getClientIp()).isSameAppSameState(latestAppExe)) {
            log.debug("AppExe update ({}) has not changed, and will be ignored.", networkInfo.getClientName());
            return;
        }

        log.debug("AppExe ({}) received from {} received. ", latestAppExe, networkInfo.getClientName());
        clientAppExecutions.put(networkInfo.getClientIp(), latestAppExe);
        clientPoolObserver.notifyClientAppUpdate(latestAppExe, networkInfo.getClientIp());
    }

	/**
	 * {@link ClientPool}
     */
	public void addClient(ClientSocket clientSocket)
	{
		String clientIp = clientSocket.getClientIp();

		clientSockets.put(clientIp, clientSocket);

		Thread messageMonitorThread = new Thread(new MessageMonitorThread(clientSocket, this));
		messageMonitorThread.start();

		Thread portMonitorThread = new Thread(new ClientConnectionMonitor(clientSocket, this));
		portMonitorThread.start();

		clientPoolObserver.notifyNewClientConnected(clientSocket.getClientName(), clientIp);
	}

    /**
     * {@link ClientPoolExecutionManager}
     */
    @Override public void executeApp(AppExe appExe, ArrayList<String> ipAddresses)
    {
		long seed = System.nanoTime();
		Collections.shuffle(ipAddresses, new Random(seed));
		for (String ipAddress : ipAddresses) {
			if (clientSockets.containsKey(ipAddress)) {
				ClientSocket clientSocket = clientSockets.get(ipAddress);
				try {
					clientSocket.sendMessage(new Message(MessageType.APP_EXE_REQUEST, appExe, clientSocket.getNetworkInfo()));
                    log.debug("Message sent to client. " + clientSocket.getNetworkInfo().toString());
				} catch (IOException e) {
					log.error("Failed to send application execution request", e);
				}
			}
		}
	}
}
