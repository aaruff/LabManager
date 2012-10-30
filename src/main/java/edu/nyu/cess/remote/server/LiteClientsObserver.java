/**
 *
 */
package edu.nyu.cess.remote.server;

/**
 * @author akira
 *
 */
public interface LiteClientsObserver {

	public void updateLiteClientAdded(String ipAddress);

	public void updateLiteClientRemoved(String ipAddress);

	public void updateLiteClientStateChanged(LiteClient liteClient);
	
	public void updateLiteClientHostNameChanged(LiteClient liteClient);
}
