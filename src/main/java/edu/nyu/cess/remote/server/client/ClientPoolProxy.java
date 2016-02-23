/**
 *
 */
package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageObserver;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.server.net.ClientConnectionMonitor;
import edu.nyu.cess.remote.server.net.ClientNameRequestRunnable;
import edu.nyu.cess.remote.server.net.ClientSocket;
import edu.nyu.cess.remote.server.net.MessageMonitorThread;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all clientAppExecutions that are currently connected to the server, and forwards
 * the client state changes to the the client observer.
 */
public class ClientPoolProxy implements ClientAppExeManager, ClientPoolObservable, ClientPool, MessageObserver, ClientDisconnectionObserver
{
	final static Logger log = Logger.getLogger(ClientPoolProxy.class);

	private Map<String, Thread> clientNameRequestThreads = new HashMap<>();
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

		if (clientNameRequestThreads.containsKey(clientIp)) {
			clientNameRequestThreads.remove(clientIp).interrupt();
		}

		clientPoolObserver.notifyClientDisconnected(clientIp);
	}

	/**
	 * {@link MessageObserver}
	 */
	@Override public void notifyMessageReceived(NetworkInfo networkInfo, Message message)
	{
		if ( ! clientSockets.containsKey(networkInfo.getClientIp())) {
			log.error("Message received from an unknown client " + networkInfo.getClientIp());
			return;
		}

		switch(message.getMessageType()) {
			case NETWORK_INFO_UPDATE:
				handleNetworkInfoUpdate(networkInfo, message);
			case APP_EXE_UPDATE:
				AppExe appExe = message.getAppExe();
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

	private void handleNetworkInfoUpdate(NetworkInfo networkInfo, Message message)
	{
		ClientSocket clientSocket = clientSockets.get(networkInfo.getClientIp());
		String currentClientName = networkInfo.getClientHostName();
		String messageClientName = message.getNetworkInfo().getClientHostName();
		String clientIp = networkInfo.getClientIp();

		if ( ! currentClientName.isEmpty() || messageClientName.isEmpty()) {
			log.error("Update received from " + clientIp + " without a client name specified.");
			return;
		}

		if (clientNameRequestThreads.containsKey(clientIp) && clientNameRequestThreads.get(clientIp).isAlive()) {
			clientNameRequestThreads.remove(clientIp).interrupt();
		}

        log.debug("Network Confirmation Message Received from " + clientIp);
        clientSocket.updateClientName(messageClientName);
        clientPoolObserver.notifyNewClientConnected(messageClientName, clientIp);
	}

	/**
	 * {@link ClientPool}
     */
	public void addClientSocket(Socket socket)
	{
		ClientSocket clientSocket = new ClientSocket(socket);
		String clientIp = clientSocket.getClientIp();

		clientSockets.put(clientIp, clientSocket);

		Thread messageMonitorThread = new Thread(new MessageMonitorThread(clientSocket, this));
		messageMonitorThread.start();

		Thread portMonitorThread = new Thread(new ClientConnectionMonitor(clientSocket, this));
		portMonitorThread.start();

		clientNameRequestThreads.put(clientIp, new Thread(new ClientNameRequestRunnable(clientSocket)));
		clientNameRequestThreads.get(clientIp).start();
	}

    /**
     * {@link ClientAppExeManager}
     */
    @Override public void executeApp(String clientIp, AppExe appExe)
    {
		ClientSocket clientSocket = clientSockets.get(clientIp);
		NetworkInfo networkInfo = clientSocket.getNetworkInfo();
		try {
			clientSocket.sendMessage(new Message(MessageType.APP_EXE_REQUEST, appExe, networkInfo));
		} catch (IOException e) {
			log.error("Failed to send application execution request", e);
		}
	}
}
