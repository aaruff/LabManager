package edu.nyu.cess.remote.server.net;

import edu.nyu.cess.remote.common.net.NetworkInfo;
import edu.nyu.cess.remote.common.net.SocketObserver;

/**
 * The network stream monitor thread is used to periodically (every 40 seconds)
 * poll the client with an empty packet to determine if the socket connection is
 * still established. The termination of this tread is used as a flag to signal
 * that the connection between the server and the client has been broken.
 */
class ClientKeepAliveMonitor implements Runnable
{
    private SocketObserver socketObserver;
    private NetworkInfo networkInfo;

    public ClientKeepAliveMonitor(SocketObserver socketObserver, NetworkInfo networkInfo)
    {
        this.socketObserver = socketObserver;
        this.networkInfo = networkInfo;
    }

    public void run() {
        boolean interfaceState = true;
        /*
         *  Sends an empty packet to the respective client
         *  to determine if the socket connection is still established.
         */
        while (interfaceState) {
            try {
                interfaceState = socketObserver.notifySocketDisconnected(networkInfo);
                if ( ! interfaceState) {
                    ClientMessageSocket.logger.info("Connection broken between server and client " + networkInfo.getClientIpAddress());
                }
                Thread.sleep(40000);
            }
            catch (InterruptedException e) {
                ClientMessageSocket.logger.info(e);
            }
        }
    }
}
