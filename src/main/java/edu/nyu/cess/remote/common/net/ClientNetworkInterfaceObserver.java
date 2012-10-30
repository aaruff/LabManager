package edu.nyu.cess.remote.common.net;


/**
 * An asynchronous update interface for receiving notifications about Network
 * information as it occurs.
 */
public interface ClientNetworkInterfaceObserver {

	/**
	 * This method is called when a network packet has been received from the
	 * {@link NetworkClientInterface}.
	 * 
	 * @param dataPacket
	 *            the data packet
	 * @param ipAddress
	 *            the ip address
	 */
	public void updateNetworkPacketReceived(DataPacket dataPacket, String ipAddress);

	/**
	 * This method is called when the {@link NetworkClientInterface} connection
	 * status has changed.
	 * 
	 * @param isConnected
	 *            the is connected
	 */
	public void updateNetworkConnectionStateChanged(String ipAddress, boolean isConnected);

}
