package edu.nyu.cess.remote.common.net;

public interface ClientNetworkInterfaceObservable
{
    boolean addClientNetworkInterfaceObserver(ClientNetworkInterfaceObserver networkObserver);

    void notifyNetworkPacketReceived(DataPacket dataPacket);

    void notifyNetworkStatusChanged(String ipAddress, boolean isConnected);
}
