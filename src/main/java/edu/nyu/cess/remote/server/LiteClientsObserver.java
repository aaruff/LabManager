/**
 *
 */
package edu.nyu.cess.remote.server;

/**
 * @author aruff
 */
public interface LiteClientsObserver
{

	void updateLiteClientAdded(String ipAddress);

	void updateLiteClientRemoved(String ipAddress);

	void updateLiteClientStateChanged(LiteClient liteClient);
	
	void updateLiteClientHostNameChanged(LiteClient liteClient);
}
