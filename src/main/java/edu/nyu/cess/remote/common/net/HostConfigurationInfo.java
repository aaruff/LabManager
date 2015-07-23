package edu.nyu.cess.remote.common.net;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The <code>HostConfigurationInfo</code> stores the necessary network contact
 * information needed to open a socket with a remote network node. Specifically,
 * an IP Address (version 4), and Port number.
 * 
 * @author Anwar A. Ruff
 */
public class HostConfigurationInfo
{
	private String ip;
	private int port;
	private String hostname;

    public HostConfigurationInfo(String ip, int port, String hostname)
    {
        this.ip = ip;
        this.port = port;
        this.hostname = hostname;
    }

	/**
	 * Sets the IP Address
	 * 
	 * @param ip
	 *            IP Address
	 */
	public void setIPAddress(String ip) {
		this.ip = ip;
	}

    /**
     * Sets the Host Name.
     * @param hostname
     */
    public void setHostName(String hostname)
    {
        this.hostname = hostname;
    }

	/**
	 * Sets the Port Number
	 * 
	 * @param port
	 */
	public void setPortNumber(int port) {
		this.port = port;
	}

	/**
	 * Returns an IP Address (IP version 4)
	 * 
	 * @return IP Address
	 */
	public String getServerIp() {
		return ip;
	}

    /**
     * Returns the host name.
     * @return Host name
     */
    public String getLocalHostName()
    {
        return hostname;
    }

	/**
	 * Returns a Port Number
	 * 
	 * @return Port Number
	 */
	public int getServerPortNumber() {
		return port;
	}

	/**
	 * Reads the remote network nodes IP Address and port number (respectively)
	 * using the {@link File} parameter, and sets them locally.
	 * 
	 * @param file
	 *            comma delimited file containing the IP Address and port number
	 * @return Network contact information retrieved from the {@link File}, or
	 *         null if the file reading process or data validation failed.
	 */
	public boolean readFromFile(File file) {
		String[] socketInfo = null;
		Boolean validIPAddress = true;
        Boolean validPortNumber = true;
        Boolean exceptionError = false;
        Boolean result = false;

		final int IP_ADDRESS = 0;
        final int PORT_NUMBER = 1;
        final int HOST_NAME = 2;
		int tempPortNumber = 0;

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            socketInfo = (bufferedReader.readLine()).split(",");
            bufferedReader.close();

			System.out.println("Reading network file information from: " + file.getAbsolutePath());
		} catch (FileNotFoundException ex) {
			exceptionError = true;
            System.err.println("File not found." + ex.getMessage());
			System.exit(1);
		} catch (IOException ex) {
			exceptionError = true;
			System.err.println("IO Exception Occured.");
			System.exit(1);
		}

        if (socketInfo.length == 3) {
            hostname = socketInfo[HOST_NAME];
        }
        else {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                System.err.println("Failed to determine host name.");
                System.exit(1);
            }
        }

		// If socketInfo consists of an IP Address and a Port Number
		if (socketInfo.length == 2) {

			// 4 octets in an IP Address
			String[] octets = socketInfo[IP_ADDRESS].split(".");
			if (octets.length == 4) {

				// check the range of each octet
				for (String octet : octets) {
					try {
						validIPAddress &= (Integer.parseInt(octet) >= 0 && Integer.parseInt(octet) <= 223);
					} catch (NumberFormatException ex) {
						exceptionError = true;
					}
				}
			}

			try {
				// check the port number range
				tempPortNumber = Integer.parseInt(socketInfo[PORT_NUMBER]);
				validPortNumber &= (tempPortNumber >= 1024 && tempPortNumber <= 49151);
			} catch (NumberFormatException ex) {
				exceptionError = true;
			}
		}

		if (validPortNumber && validIPAddress && !exceptionError) {
			System.out.println("Network info read is valid.");
			System.out.println("IP Address: " + socketInfo[IP_ADDRESS]);
			System.out.println("Port Number: " + tempPortNumber);

			this.ip = socketInfo[IP_ADDRESS];
			this.port = tempPortNumber;
			result = true;
		}

		return result;
	}
}
