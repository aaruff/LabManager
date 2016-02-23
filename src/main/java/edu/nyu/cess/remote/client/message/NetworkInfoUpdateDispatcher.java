package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.apache.log4j.Logger;

/**
 * Created by aruff on 2/12/16.
 */
public class NetworkInfoUpdateDispatcher implements MessageDispatcher
{
	private final static Logger log = Logger.getLogger(NetworkInfoUpdateDispatcher.class);
    private NetworkInfo networkInfo;
    private DispatchControl dispatchControl;

    public NetworkInfoUpdateDispatcher(NetworkInfo networkInfo)
    {
        this.networkInfo = networkInfo;
    }

    @Override
    public void setDispatchControl(DispatchControl dispatchControl)
    {
        this.dispatchControl = dispatchControl;
    }

    @Override public void dispatchMessage(Message message)
    {
		log.info("Dispatching client network info to server");
        dispatchControl.dispatchOutboundMessage(new Message(MessageType.NETWORK_INFO_UPDATE, networkInfo));
    }
}
