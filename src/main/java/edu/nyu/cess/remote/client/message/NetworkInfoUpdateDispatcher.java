package edu.nyu.cess.remote.client.message;

import edu.nyu.cess.remote.common.message.Message;
import edu.nyu.cess.remote.common.message.MessageType;
import edu.nyu.cess.remote.common.message.dispatch.DispatchControl;
import edu.nyu.cess.remote.common.message.dispatch.MessageDispatcher;
import edu.nyu.cess.remote.common.net.NetworkInfo;

/**
 * Created by aruff on 2/12/16.
 */
public class NetworkInfoUpdateDispatcher implements MessageDispatcher
{
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
        dispatchControl.dispatchOutboundMessage(new Message(MessageType.NETWORK_INFO_UPDATE, networkInfo));
    }
}
