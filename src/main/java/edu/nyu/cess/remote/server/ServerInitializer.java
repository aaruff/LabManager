/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.Server;
/**
 * @author Anwar A. Ruff 
 */
public class ServerInitializer {

	public static void main(String[] args) {
		Server server = new Server();
		server.init();
	}

}
