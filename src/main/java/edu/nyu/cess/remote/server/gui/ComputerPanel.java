package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.server.gui.listeners.StartStopButtonListener;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;

import javax.swing.*;
import java.awt.*;

public class ComputerPanel extends JPanel
{
    private final String name;

    private final JButton startButton;
    private final JButton stopButton;

	private final JLabel hostNameLabel;
	private final JLabel appExeStateLabel;

	private final Color PANEL_RUNNING_APP_COLOR = new Color(204, 255, 204);
	private final Color PANEL_CONNECTED_COLOR = new Color(255, 255, 255);
	private final Color PANEL_NOT_CONNECTED_COLOR = new Color(255, 194, 194);

    private ConnectionState connectionState;

    public boolean isConnectionState(ConnectionState connectionState)
    {
        return this.connectionState == connectionState;
    }

    public ComputerPanel(String name, String ipAddress, ConnectionState connectionState, StartStopButtonObserver startStopButtonObserver)
    {
		super();
        this.name = name;
		this.connectionState = connectionState;

		/*
		 * Computer Panel Layout & Border
		 */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder((connectionState == ConnectionState.CONNECTED) ? Color.BLACK : Color.LIGHT_GRAY));
		setBackground((connectionState == ConnectionState.CONNECTED) ? PANEL_CONNECTED_COLOR : PANEL_NOT_CONNECTED_COLOR);

		/*
		 * Host Name
		 */
		hostNameLabel = new JLabel("" + name);
        hostNameLabel.setFont(new Font("arial", Font.PLAIN, 18));
		hostNameLabel.setForeground((connectionState == ConnectionState.CONNECTED) ? Color.BLACK : Color.GRAY);

		JPanel clientDescriptionPanel = new JPanel(new FlowLayout());
		clientDescriptionPanel.setOpaque(false);
		clientDescriptionPanel.add(hostNameLabel);
		add(clientDescriptionPanel);

		/*
		 * Computer/Application State
		 */
		JPanel applicationStatePanel = new JPanel(new FlowLayout());
		applicationStatePanel.setOpaque(false);

		appExeStateLabel = new JLabel();
		appExeStateLabel.setText((connectionState == ConnectionState.CONNECTED) ? "Connected" : "Not Connected");
		appExeStateLabel.setFont(new Font("arial", Font.PLAIN, 14));
		appExeStateLabel.setForeground((connectionState == ConnectionState.DISCONNECTED) ? Color.GRAY : Color.BLACK);

		applicationStatePanel.add(appExeStateLabel);
		add(applicationStatePanel);

		/*
		 * Start & Stop Button
		 */
		startButton = new JButton("Start");
		startButton.setFont(new Font("arial", Font.PLAIN, 14));
		startButton.setToolTipText("Starts selected application on " + ipAddress + ".");
		startButton.addActionListener(new StartStopButtonListener(startStopButtonObserver, ipAddress, "Start", "Stop"));
		startButton.setEnabled(connectionState == ConnectionState.CONNECTED);

		stopButton = new JButton("Stop");
		stopButton.setFont(new Font("arial", Font.PLAIN, 14));
		stopButton.setToolTipText("Starts selected application on " + ipAddress + ".");
		stopButton.addActionListener(new StartStopButtonListener(startStopButtonObserver, ipAddress, "Start", "Stop"));
		stopButton.setEnabled(false);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setOpaque(false);
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		add(buttonPanel);
    }

    public void updateAppExe(AppExe appExe)
    {
        switch(appExe.getState()) {
            case STARTED:
                String name = appExe.getAppInfo().getName();
                if (name.length() > 25) {
                    name = name.substring(0, 24) + "...";
                }
                appExeStateLabel.setText(name);
                setBackground(PANEL_RUNNING_APP_COLOR);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                break;
            case STOPPED:
				setBackground(PANEL_CONNECTED_COLOR);
				switch(appExe.getErrorType()) {
					case NO_ERROR:
						appExeStateLabel.setText("Connected");
						appExeStateLabel.setForeground(Color.BLACK);
						startButton.setEnabled(true);
						stopButton.setEnabled(false);
						break;
					case SAME_APP_SAME_STATE:
						appExeStateLabel.setText("Error: Already running");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case SAME_APP_ALREADY_STOPPED:
						appExeStateLabel.setText("Error: Already stopped");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case SAME_APP_ALREADY_RUNNING:
						appExeStateLabel.setText("Error: Already running");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case OTHER_APP_ALREADY_RUNNING:
						appExeStateLabel.setText("Error: Other running");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case FAILED_TO_START:
						appExeStateLabel.setText("Error: Failed start");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case SECURITY_ERROR:
						appExeStateLabel.setText("Security error");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case IO_ERROR:
						appExeStateLabel.setText("Input/Output Error");
						appExeStateLabel.setForeground(Color.RED);
						break;
					case APP_ALREADY_STOPPED:
						appExeStateLabel.setText("Error: App already stopped");
						setBackground(PANEL_CONNECTED_COLOR);
						break;
				}
                break;
        }
    }


	public void updateState(ConnectionState connectionState)
	{
        switch(connectionState) {
            case CONNECTED:
                if (this.connectionState == ConnectionState.DISCONNECTED) {
                    setBackground(PANEL_CONNECTED_COLOR);
                    startButton.setEnabled(true);
                    appExeStateLabel.setText("Connected");
                    appExeStateLabel.setForeground(Color.BLACK);
                    hostNameLabel.setForeground(Color.BLACK);
                    setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    this.connectionState = connectionState;
                }
                break;
            case DISCONNECTED:
                if (this.connectionState == ConnectionState.CONNECTED){
                    startButton.setEnabled(false);
                    setBackground(PANEL_NOT_CONNECTED_COLOR);
                    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    appExeStateLabel.setText("Not Connected");
                    appExeStateLabel.setForeground(Color.GRAY);
                    hostNameLabel.setForeground(Color.GRAY);
                    this.connectionState = connectionState;
                }
                break;
        }
	}

    public String getName()
    {
        return name;
    }
}
