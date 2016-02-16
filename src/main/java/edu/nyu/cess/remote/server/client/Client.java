package edu.nyu.cess.remote.server.client;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.net.ClientSocket;
import edu.nyu.cess.remote.server.net.ClientSocketObserver;

import java.net.Socket;

public class Client implements ClientSocketObserver
{
    private ClientSocket clientSocket;

    private final ClientObserver clientObserver;

    private AppExe appExe;

    /**
     * @param socket the socket used for client communication
     * @param clientObserver the client observer
     */
    public Client(Socket socket, ClientObserver clientObserver)
	{
        this.clientObserver = clientObserver;

        clientSocket = new ClientSocket(socket, this);

        appExe = new AppExe("", "", "", AppState.STOPPED);
	}

	public String getHostName()
	{
		return clientSocket.getClientHostName();
	}

	public String getIPAddress() {
		return clientSocket.getClientIp();
	}

	public void appExecution(AppExe appExe)
	{
		clientSocket.sendAppExe(appExe);
	}

    @Override public void notifyClientConnectionLost()
    {
        clientObserver.notifyClientDisconnected(clientSocket.getClientIp());
    }

    @Override public void notifyClientConfirmed()
    {
        clientObserver.notifyClientConfirmed(clientSocket.getClientIp());
    }

    @Override public void notifyAppExeUpdate(AppExe newAppExe)
    {
        if ( ! appExe.isSameState(newAppExe) && appExe.isSameApp(newAppExe)) {
            clientObserver.notifyClientAppExeUpdate(appExe, clientSocket.getClientIp());
        }
    }
}
