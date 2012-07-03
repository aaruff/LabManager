package edu.nyu.cess.remote.client;

public class ClientInitializer {

	public static void main(String[] args) {
		Client client = new Client();
		client.initServerConnection();
	}

}
