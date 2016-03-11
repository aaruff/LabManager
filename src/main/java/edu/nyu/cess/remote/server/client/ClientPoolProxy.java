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
 * ClientPoolProxy handles all client app executions that are currently connected to the server, and forwards
 * the client state changes to the the client observer.
 */
public class ClientPoolProxy implements ClientPoolExecutionManager, ClientPoolObservable, ClientPool, MessageSocketObserver, ClientDisconnectionObserver
{
	private final static Logger log = LoggerFactory.getLogger(ClientPoolProxy.class);

	private Map<String, AppExe> clientAppExecutions = new HashMap<>();
	private Map<String, ClientSocket> clientSockets = new HashMap<>();

	private final Object clientSocketsLock = new Object();
	private final Object clientAppExecutionsLock = new Object();
	private final Object clientPoolObserverLock = new Object();

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
		synchronized (clientAppExecutionsLock) {
			clientAppExecutions.remove(clientIp);
		}
		synchronized (clientSocketsLock) {
			clientSockets.remove(clientIp);
		}

		synchronized (clientPoolObserverLock) {
			clientPoolObserver.notifyClientDisconnected(clientIp);
		}
	}

	/**
	 * {@link MessageSocketObserver}
	 */
	@Override public void notifyMessageReceived(NetworkInfo networkInfo, Message message)
	{
		synchronized (clientSocketsLock) {
			if ( ! clientSockets.containsKey(networkInfo.getClientIp())) {
				log.error("Message received from an unknown computer {}.", networkInfo.getClientIp());
				return;
			}
		}

		switch(message.getMessageType()) {
			case APP_EXE_UPDATE:
                log.debug("App execution update received from computer {}.", networkInfo.getClientName());
				AppExe appExe = message.getAppExe();
                handleAppExeUpdate(networkInfo, appExe);
				break;
			case APP_EXE_REQUEST:
				log.error("Ignored app execution request from computer {}. The server does not perform app executions.",
						networkInfo.getClientName());
				break;
			case KEEP_ALIVE_PING:
			default:
				break;
		}
	}

	/**
	 * {@link MessageSocketObserver}
	 */
	@Override public void notifyMessageSenderState(ConnectionState connectionState)
	{
		// TODO: Create a separate interface, not including this func.
	}

	/**
	 * {@link ClientPool}
     */
	public void addClient(ClientSocket clientSocket)
	{
		String clientIp = clientSocket.getClientIp();

		synchronized (clientSocketsLock) {
			clientSockets.put(clientIp, clientSocket);
		}

		Thread messageMonitorThread = new Thread(new MessageMonitorThread(clientSocket, this));
		messageMonitorThread.start();

		Thread portMonitorThread = new Thread(new ClientConnectionMonitor(clientSocket, this));
		portMonitorThread.start();

		synchronized (clientPoolObserverLock) {
            clientPoolObserver.notifyNewClientConnected(clientSocket.getClientName(), clientIp);
		}
	}

    /**
     * {@link ClientPoolExecutionManager}
     */
    @Override public void executeApp(AppExe appExe, ArrayList<String> ipAddresses)
    {
		long seed = System.nanoTime();
		Collections.shuffle(ipAddresses, new Random(seed));

		synchronized (clientSocketsLock) {
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

	/**
	 * Handles application executions updates sent by the client, and notifies observers of any changes.
	 *
	 * @param networkInfo The client's network information
	 * @param currentAppExe The current application execution information
     */
	private void handleAppExeUpdate(NetworkInfo networkInfo, AppExe currentAppExe)
	{
		String clientName = networkInfo.getClientName();
		String clientIp = networkInfo.getClientIp();
		synchronized (clientAppExecutionsLock) {
			boolean clientHasAppExe = clientAppExecutions.containsKey(clientIp);
			AppExe previousAppExe = clientAppExecutions.get(clientIp);
			if (clientHasAppExe && previousAppExe.isSame(currentAppExe)) {
				log.debug("Duplicate app update from computer {} received and ignored. previous = {}, current = {}",
						clientName, previousAppExe, currentAppExe);
				return;
			}

			log.debug("AppExe ({}) received from {} received. ", currentAppExe, networkInfo.getClientName());
			clientAppExecutions.put(networkInfo.getClientIp(), currentAppExe);
		}

		synchronized (clientPoolObserverLock) {
			clientPoolObserver.notifyClientAppUpdate(currentAppExe, networkInfo.getClientIp());
		}
	}

}
