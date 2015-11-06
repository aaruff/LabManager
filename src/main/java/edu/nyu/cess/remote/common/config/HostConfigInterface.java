package edu.nyu.cess.remote.common.config;

/**
 * The interface for host config information.
 */
public interface HostConfigInterface
{
	/**
	 * @return String IP address
     */
	String getIpAddress();

	/**
	 * @return String port
     */
	String getPort();

	/**
	 *
	 * @return String host name
     */
	String getHostName();
}
