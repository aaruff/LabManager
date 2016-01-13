package edu.nyu.cess.remote.common.net;

public interface ClientNetworkInterfaceObservable
{
    boolean addObserver(PortWatcher networkObserver);

    void notifyNetworkPacketReceived(Message message);

    void notifyNetworkStatusChanged(String ipAddress, boolean isConnected);
}
