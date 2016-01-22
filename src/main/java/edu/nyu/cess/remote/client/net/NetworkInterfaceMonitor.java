package edu.nyu.cess.remote.client.net;

import edu.nyu.cess.remote.common.net.NetworkInformation;
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

    private MessageHandlerController messageHandlerController;
	private NetworkInformation networkInfo;

    public NetworkInterfaceMonitor(MessageHandlerController messageHandlerController, NetworkInformation networkInfo)
	{
        this.messageHandlerController = messageHandlerController;
		this.networkInfo = networkInfo;
    }

    public void run()
	{
        boolean networkInterfaceUp = true;
        int monitorInterval = 40000;

        while (networkInterfaceUp) {

			NetworkInterface networkInterface;
            try {
				InetAddress inetAddress = InetAddress.getByName(networkInfo.getClientIpAddress());
				networkInterface = NetworkInterface.getByInetAddress(inetAddress);
				if (networkInterface != null) {
					Thread.sleep(monitorInterval);
					networkInterfaceUp = networkInterface.isUp();
				}
				else {
					networkInterfaceUp = false;
				}
			}
            catch (InterruptedException e) {
                networkInterfaceUp = false;
                log.error("Sleep during network interface checking failed.", e);
            } catch (SocketException e) {
                log.error("Socket exception occurred during network interface status check.", e);
                networkInterfaceUp = false;
            }
			catch (UnknownHostException e) {
				log.error("Unknown host error while attempting to retrieve the client IP address.", e);
				networkInterfaceUp = false;
			}

			log.info("NIC Status: " + ((networkInterfaceUp) ? "UP" : "DOWN"));
        }

        messageHandlerController.stopMessageHandler();
        log.info("Network Interface Is Down!");
        log.info("Attempting to interrupt the network communication thread.");
    }
}
