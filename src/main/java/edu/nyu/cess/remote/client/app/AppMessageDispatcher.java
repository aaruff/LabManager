package edu.nyu.cess.remote.client.app;

import edu.nyu.cess.remote.client.app.process.AppExeObservable;
import edu.nyu.cess.remote.client.app.process.AppExecutor;
import edu.nyu.cess.remote.client.app.process.ProcessStateObserver;
import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppExecutionValidator;
import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the unpacking, routing, and sending of application messages.
 */
public class AppMessageDispatcher implements ProcessStateObserver, MessageDispatcher
{
	private final static Logger log = LoggerFactory.getLogger(AppMessageDispatcher.class);

	private DispatchControl dispatchControl;
	private NetworkInfo networkInfo;
	private AppExecutor appHandler;

	private Object dispatcherControlStateLock = new Object();
	private ConnectionState dispatcherControlState = ConnectionState.DISCONNECTED;

	/**
	 * Initializes the AppMessenger class with the required handler, sender, and network information.
	 * @param appHandler the application execution handler
	 * @param networkInfo network information required for sending messages
     */
	public AppMessageDispatcher(AppExecutor appHandler, AppExeObservable appExeObservable, NetworkInfo networkInfo)
	{
		this.appHandler = appHandler;
		this.networkInfo = networkInfo;
        appExeObservable.setStateObserver(this);
	}

	/**
	 * {@link MessageDispatcher}
     */
	public void setDispatchControl(DispatchControl dispatchControl)
	{
		this.dispatchControl = dispatchControl;
	}

	@Override public void notifyDispatcherControlState(ConnectionState connectionState)
	{
		synchronized (dispatcherControlStateLock) {
			this.dispatcherControlState = connectionState;
		}

		if (connectionState == ConnectionState.CONNECTED) {
			// Send an update when a new connection is established
			notifyStateChange(appHandler.getExecution());
		}
	}

	/**
	 * {@link MessageDispatcher}
     */
	public void dispatchMessage(Message message)
	{

		switch(message.getMessageType()) {

			case APP_EXE_REQUEST:
				if ( ! AppExecutionValidator.validate(message.getAppExe())) {
					// TODO: Add more information
					log.error("Invalid app execution received and ignored.");
					return;
				}

				log.debug("Dispatching APP_EXE_REQUEST {}", message.getAppExe());
				appHandler.executeRequest(message.getAppExe());
				break;
			case APP_EXE_UPDATE:
				log.debug("APP_EXE_UPDATE received, sending {}", appHandler.getExecution());
				notifyStateChange(appHandler.getExecution());
				break;
			default:
				log.error("AppMessageDispatcher received an unsupported message type ({})", message.getAppExe());
				break;
		}
	}

	/**
	 * {@link ProcessStateObserver}
     */
	@Override public void notifyStateChange(AppExe appExe)
	{
		synchronized (dispatcherControlStateLock) {
			if (dispatcherControlState == ConnectionState.CONNECTED) {
				log.debug("Sending state change ({}) to the server.", appExe);
				Message message = new Message(MessageType.APP_EXE_UPDATE, appExe, networkInfo);
				dispatchControl.dispatchOutboundMessage(message);
			}
			else {
				log.error("Dispatch Controller Down: Failed to send state change ({}) to the server.", appExe);
			}
		}
	}
}
