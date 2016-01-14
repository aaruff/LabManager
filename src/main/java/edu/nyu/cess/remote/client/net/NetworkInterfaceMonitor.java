package edu.nyu.cess.remote.client.net;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Monitors the state of the network interface. If the network interface is
 * down the socket is set to null to trigger an interrupt.
 */
class NetworkInterfaceMonitor implements Runnable
{
	final static Logger log = Logger.getLogger(NetworkInterfaceMonitor.class);

    private Socket socket;

    public NetworkInterfaceMonitor(Socket socket)
	{
        this.socket = socket;
    }

    public void run()
	{
        boolean networkInterfaceUp = true;
        int monitorInterval = 40000;

        while (networkInterfaceUp) {

			NetworkInterface networkInterface;
            try {
                InetAddress inetAddress = socket.getInetAddress();
                networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            } catch (SocketException | UnknownHostException e) {
				log.info("Failed to read IP Address.", e);
                networkInterface = null;
            }

			if (networkInterface != null) {
                try {
                    Thread.sleep(monitorInterval);
                    networkInterfaceUp = networkInterface.isUp();
                    log.info("NIC Status: " + ((networkInterfaceUp) ? "UP" : "DOWN"));
                } catch (InterruptedException e) {
                    networkInterfaceUp = false;
					log.info("Network polling interrupted.", e);
                } catch (SocketException e) {
					log.info("Socket Exception occurred during polling.", e);
                    networkInterfaceUp = false;
                }
            }
            else {
                networkInterfaceUp = false;
            }
        }

        socket.closeSocketConnection();
        log.info("Network Interface Is Down!");
        log.info("Attempting to interrupt the network communication thread.");
    }
}
