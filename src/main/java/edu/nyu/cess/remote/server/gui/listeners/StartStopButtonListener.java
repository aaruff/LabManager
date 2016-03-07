package edu.nyu.cess.remote.server.gui.listeners;

import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by aruff on 2/9/16.
 */
public class StartStopButtonListener implements ActionListener
{
	final static Logger log = Logger.getLogger(StartStopGroupButtonListener.class);

	private StartStopButtonObserver startStopButtonObserver;
	private final String clientIp;
	private final String startButtonName;
	private final String stopButtonName;

	public StartStopButtonListener(StartStopButtonObserver startStopButtonObserver, String clientIp, String startButtonName, String stopButtonName) {
		this.startStopButtonObserver = startStopButtonObserver;
		this.clientIp = clientIp;
		this.startButtonName = startButtonName;
		this.stopButtonName = stopButtonName;
	}

    public void actionPerformed(ActionEvent e)
	{
		JButton startStopGroupButton = (JButton) e.getSource();
		String name = startStopGroupButton.getText();

		AppState appState = AppState.STOPPED;
		if (name.equals(startButtonName)) {
			appState = AppState.STARTED;
		}
		else if (name.equals(stopButtonName)) {
			appState = AppState.STOPPED;
		}
		else {
			log.error("Button text does not match provided start/stop button text.");
		}

		log.debug(appState.toString() + " button selected.");
		startStopButtonObserver.notifyExeRequest(appState, clientIp);
    }
}
