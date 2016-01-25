package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.client.notification.UserNotificationHandler;
import edu.nyu.cess.remote.common.app.*;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerMessageHandler implements NetworkInterfaceObserver, ApplicationStateObserver
{
	final static Logger log = Logger.getLogger(ServerMessageHandler.class);

	private final NetworkInformation networkInfo;

	private final AppExecutionHandler appExecHandler;
	private final UserNotificationHandler userNotificationHandler;

	private MessageSocket messageSocket;

	public ServerMessageHandler(NetworkInformation networkInfo, UserNotificationHandler userNotificationHandler)
	{
		this.appExecHandler = new AppExecutor(getApplicationStateObserverFrom(this));
		this.networkInfo = networkInfo;
		this.userNotificationHandler = userNotificationHandler;
	}

	public void start(NetworkInformation networkInfo)
    {
        // Start the network interface monitor
        Thread netInterfaceMonitorThread = new Thread(new NetworkInterfaceMonitor(getNetworkInterfaceObserverFrom(this), networkInfo));
        netInterfaceMonitorThread.setName("Network Interface Monitor");
        netInterfaceMonitorThread.start();

        // Continuously attempt to establish a socket connection to the server
		while (true) {
			try {
				messageSocket = new MessageSocketHandler(networkInfo.getServerIpAddress(), networkInfo.getServerPort());
			} catch (IOException e) {
				log.error("Error: Failed to create a message socket.");
			}

			while (messageSocket.isConnected()) {
                try {
                    routeIncomingMessage(messageSocket.readMessage(), networkInfo);
                }
                catch (ClassNotFoundException|IOException e) {
                    log.error("Error reading message", e);
                }
            }

			// Wait 2 minutes before trying to create another socket
			try {
				int milliseconds = 2000;
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted.", e);
			}
		}
	}

	/**
	 * {@link NetworkInterfaceObserver}
	 */
    @Override public synchronized void notifyNetworkInterfaceDown()
    {
        stop();

    }

	/**
	 * {@link NetworkInterfaceObserver}
	 */
	private synchronized void stop() {
		close(objectOutputStream);
	}

	/**
	 * {@link ApplicationStateObserver}
	 */
	@Override public void applicationStateUpdate(AppExecution appExecution)
	{
		Message message = new Message(MessageType.STATE_CHANGE, appExecution, networkInfo);
		sendMessage(message);
	}

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */

    /**
	 * Sends the provided message to the server.
	 *
	 * @param message The message
     */
	private void routeIncomingMessage(Message message, NetworkInformation netInfo)
	{
		switch(message.getMessageType()) {
			case APPLICATION_EXECUTION:
                if ( ! AppExecutionValidator.validate(message.getAppExecution())) {
                    log.error("Invalid application execution received and ignored.");
                    return;
                }

                appExecHandler.executeRequest(message.getAppExecution());
				break;
			case USER_NOTIFICATION:
				String text = message.getClientMessage();
				if (text == null || text.isEmpty()) {
                    log.error("Invalid client message received and ignored.");
				}

                userNotificationHandler.notifyUser(text);
				break;
            case NET_INFO_REQUEST:
                sendMessage(new Message(MessageType.NET_INFO_RESPONSE, appExecHandler.getAppExecution(), netInfo));
                break;
            // Unsupported messages on client ignored
			case STATE_CHANGE:
			case KEEP_ALIVE_PING:
			default:
				break;
		}
	}

	/**
	 * Polls the remote network node on a millisecond interval until a
	 * {@link Socket} connection is established.
	 */
	private MessageSocket getSocket(String serverIpAddress, NetworkInformation networkInfo) {

        Socket socket = null;
        while (true) {
            try {
                socket = new Socket(serverIpAddress, networkInfo.getServerPort());
                return new MessageSocketHandler(socket, networkInfo);
            } catch (UnknownHostException e) {
                log.error("Error: No Known Host.", e);

            } catch (ConnectException e) {
                log.error("Error: Failed to connection to server.", e);

            } catch (IOException e) {
                log.error("IO Exception occurred.", e);
            }
            finally {
                if (socket == null) {
                    try {
                        int milliseconds = 2000;
                        Thread.sleep(milliseconds);
                    } catch (InterruptedException e) {
                        log.error("Thread sleep interrupted.", e);
                    }
                }
            }
        }
	}

    private ApplicationStateObserver getApplicationStateObserverFrom(ServerMessageHandler serverMessageHandler)
    {
        return serverMessageHandler;
    }

    private NetworkInterfaceObserver getNetworkInterfaceObserverFrom(ServerMessageHandler serverMessageHandler)
    {
        return serverMessageHandler;
    }

	private void close(Closeable closeable)
	{
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

}
