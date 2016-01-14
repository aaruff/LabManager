/**
 *
 */
package edu.nyu.cess.remote.client;

import edu.nyu.cess.remote.client.app.ApplicationStateChangeNotifier;
import edu.nyu.cess.remote.client.app.ExecutionRequestHandler;
import edu.nyu.cess.remote.client.net.ClientSocket;
import edu.nyu.cess.remote.client.notification.UserNotificationHandler;
import edu.nyu.cess.remote.common.app.ExecutionRequest;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.net.Message;
import edu.nyu.cess.remote.common.net.MessageType;
import edu.nyu.cess.remote.common.net.ServerMessageNotification;
import org.apache.log4j.Logger;

/**
 * @author Anwar A. Ruff
 */
public class ServerMessageRouter implements ServerMessageNotification, ApplicationStateChangeNotifier
{
	final static Logger log = Logger.getLogger(ServerMessageRouter.class);

	private ExecutionRequestHandler executionRequestHandler;
	private UserNotificationHandler userNotificationHandler;

	private ClientSocket clientSocket;

	public ServerMessageRouter(ClientSocket clientSocket, ExecutionRequestHandler executionRequestHandler,
							   UserNotificationHandler userNotificationHandler) {
		this.clientSocket = clientSocket;
		this.clientSocket.setServerMessageNotification(this);
		this.executionRequestHandler = executionRequestHandler;
		this.userNotificationHandler = userNotificationHandler;
	}

	@Override public synchronized void notifyApplicationStateChangeOccurred(AppState appState) {
		Message message = new Message(MessageType.STATE_CHANGE, appState);
		clientSocket.writeDataPacket(message);
	}

	@Override public void notifyServerMessageReceived(Message message) {
		log.info("Server message received.");

		switch(message.getMessageType()) {
		case EXECUTION_REQUEST:
			ExecutionRequest executionRequest = (ExecutionRequest) message.getPayload();
			if (executionRequest != null) {
					log.info("Packet Content: ApplicationExecRequest");
                    executionRequestHandler.execute(executionRequest);
			}
			break;
		case USER_NOTIFICATION:
			String text = (String) message.getPayload();
			if (text != null && !text.isEmpty()) {
				userNotificationHandler.notifyUser(text);
			}
			break;
		case STATE_CHANGE:
		case NETWORK_INFO:
			// Not supported by the Client
			break;
		case PING:
			// No processing occurs during a socket test
			break;
		default:
			// Do nothing
			break;
		}
	}
}
