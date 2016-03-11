package edu.nyu.cess.remote.client.app.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ProcessIOStreamGobbler extends Thread {
	InputStream is;
	String type;

	ProcessIOStreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;

	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println(type + ">" + line);
			}
		} catch (IOException ioe) { /* Forward to handler */
		}
	}
}
