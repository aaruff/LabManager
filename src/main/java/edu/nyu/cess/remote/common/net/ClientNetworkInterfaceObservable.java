package edu.nyu.cess.remote.common.net;

public interface ClientNetworkInterfaceObservable
{
    boolean addObserver(PortWatcher networkObserver);

    void notifyNetworkPacketReceived(DataPacket dataPacket);

    void notifyNetworkStatusChanged(String ipAddress, boolean isConnected);
}
