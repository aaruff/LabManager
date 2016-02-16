/**
 *
 */
package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all clients that are currently connected to the server, and forwards
 * the client state changes to the the client observer.
 */
public class ClientSocketPoolManager implements ClientObserver, ClientAppExeManager, ClientStatusObservable, ClientSocketCollection
{
	final static Logger logger = Logger.getLogger(ClientSocketPoolManager.class);

	Map<String, Client> clients = new HashMap<>();

    private ClientPoolObserver observer;

	/**
	 * {@link ClientStatusObservable}
     */
    public void addObserver(ClientPoolObserver observer)
    {
        this.observer = observer;
    }

	/**
	 * {@link ClientSocketCollection}
     */
	public void addClientSocket(Socket socket)
	{
		String clientIp = socket.getInetAddress().getHostAddress();
		clients.put(clientIp, new Client(socket, this));
	}

    /**
     * {@link ClientObserver}
     */
    @Override synchronized public void notifyClientConfirmed(String clientIp)
    {
        String hostName = clients.get(clientIp).getHostName();
        observer.notifyNewClientConnected(hostName, clientIp);
    }

    /**
     * {@link ClientObserver}
     */
    @Override public void notifyClientDisconnected(String clientIp)
    {
        observer.notifyClientDisconnected(clientIp);
    }

    /**
     * {@link ClientObserver}
     */
    @Override public void notifyClientAppExeUpdate(AppExe appExe, String clientIp)
    {
        observer.notifyClientAppUpdate(appExe, clientIp);
    }

    /**
     * {@link ClientAppExeManager}
     */
    @Override public void executeApp(String clientIp, AppExe appExe)
    {
		Client client = clients.get(clientIp);
		client.appExecution(appExe);
    }
}
