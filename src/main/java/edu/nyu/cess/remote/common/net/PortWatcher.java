package edu.nyu.cess.remote.common.net;


/**
 * Interface for handling socket packets and state changes.
 */
public interface PortWatcher
{
    void readServerMessage(DataPacket dataPacket, String ipAddress);

    void processStateChange(String ipAddress, boolean isConnected);
}
