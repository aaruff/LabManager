package edu.nyu.cess.remote.common.net;

public interface ClientNetworkInterfaceObservable {

	public boolean addObserver(ClientNetworkInterfaceObserver networkObserver);

	public boolean deleteObserver(ClientNetworkInterfaceObserver networkObserver);

	public void notifyObservers(DataPacket dataPacket);

	public void notifyNetworkStatusUpdate(String ipAddress, boolean isConnected);

}
