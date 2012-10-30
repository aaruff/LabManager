package edu.nyu.cess.remote.common.net;

public interface ClientNetworkInterfaceObservable {

	public boolean addClientNetworkInterfaceObserver(ClientNetworkInterfaceObserver networkObserver);

	public boolean deleteClientNetworkInterfaceObserver(ClientNetworkInterfaceObserver networkObserver);

	public void notifyNetworkPacketReceived(DataPacket dataPacket);

	public void notifyNetworkStatusChanged(String ipAddress, boolean isConnected);

}
