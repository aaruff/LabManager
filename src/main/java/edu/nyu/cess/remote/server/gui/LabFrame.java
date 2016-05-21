/**
 *
 */
package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.common.net.ConnectionState;
import edu.nyu.cess.remote.server.gui.listeners.StartStopGroupButtonListener;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;
import edu.nyu.cess.remote.server.gui.observers.StartStopGroupButtonObserver;
import edu.nyu.cess.remote.server.gui.observers.ViewAppExeObserver;
import edu.nyu.cess.remote.server.gui.panels.ComputerLayoutPanel;
import edu.nyu.cess.remote.server.gui.panels.ComputersConnectedPanel;
import edu.nyu.cess.remote.server.lab.Computer;
import edu.nyu.cess.remote.server.lab.LabLayout;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The lab frame lays the the lab manager view, and renders it, and handles any events that occur.
 */
public class LabFrame extends JFrame implements StartStopGroupButtonObserver, StartStopButtonObserver, LabView
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPane = new JPanel(new MigLayout());

	private final ComputersConnectedPanel computersConnectedPanel;
	private final ComputerLayoutPanel computerLayoutPanel;
	private final JPanel computerRangePanel;
	private final JPanel appExecutionPanel;

	private final JComboBox<String> appNameComboBox;

	private final JComboBox<String> toClientComboBox;
	private final JComboBox<String> fromClientComboBox;

	private final ViewAppExeObserver viewAppExeObserver;

	private final ArrayList<Computer> labComputers;

	public LabFrame(String[] appNames, LabLayout labLayout, ViewAppExeObserver viewAppExeObserver)
	{
		this.viewAppExeObserver = viewAppExeObserver;

		// Content Panel
		contentPane.setBackground(Color.white);

		// Computers Connected Panel
		computersConnectedPanel = new ComputersConnectedPanel();
		contentPane.add(computersConnectedPanel, "wrap");

		// Computer Layout Panel
		computerLayoutPanel = new ComputerLayoutPanel(labLayout, this);
		contentPane.add(computerLayoutPanel, "wrap");

		// Application Selection Panel
		appNameComboBox = new JComboBox<>(appNames);
		appNameComboBox.setFont(new Font("arial", Font.PLAIN, 14));
		JLabel applicationLabel = new JLabel("App Name");
		applicationLabel.setFont(new Font("arial", Font.PLAIN, 14));
		JPanel programSelectionPanel = new JPanel(new MigLayout("fillx"));
		programSelectionPanel.setBackground(Color.white);
		programSelectionPanel.add(applicationLabel, "align right");
		programSelectionPanel.add(appNameComboBox);
		contentPane.add(programSelectionPanel, "growx, wrap");

		// Group App Execution Panel

		// Sort lab computers
		labComputers = labLayout.getAllComputers();
		labComputers.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
		String[] names = new String[labComputers.size()];
		for (int i = 0; i < labComputers.size(); ++i) {
			names[i] = labComputers.get(i).getName();
		}

		fromClientComboBox = new JComboBox<>(new DefaultComboBoxModel<>(names));
		fromClientComboBox.setFont(new Font("arial", Font.PLAIN, 14));
		toClientComboBox = new JComboBox<>(new DefaultComboBoxModel<>(names));
		toClientComboBox.setFont(new Font("arial", Font.PLAIN, 14));

		computerRangePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		computerRangePanel.setBackground(Color.white);

		JLabel rangeFromLabel = new JLabel("Computer Range");
		rangeFromLabel.setFont(new Font("arial", Font.PLAIN, 14));
		computerRangePanel.add(rangeFromLabel);
		computerRangePanel.add(fromClientComboBox);
		JLabel rangeToLabel = new JLabel("to");
		rangeToLabel.setFont(new Font("arial", Font.PLAIN, 14));
		computerRangePanel.add(rangeToLabel);
		computerRangePanel.add(toClientComboBox);

		contentPane.add(computerRangePanel, "growx, wrap");

		appExecutionPanel = new JPanel(new MigLayout("fillx"));
		appExecutionPanel.setBackground(Color.white);

		// Start Stop Group Button Panel
		String startButtonText = "Start";
		JButton startGroupButton = new JButton(startButtonText);
		startGroupButton.setFont(new Font("arial", Font.PLAIN, 14));
		startGroupButton.setToolTipText("Starts the selected program on all computers in the selected range.");

		String stopButtonText = "Stop";
		JButton stopGroupButton = new JButton(stopButtonText);
		stopGroupButton.setFont(new Font("arial", Font.PLAIN, 14));
		stopGroupButton.setToolTipText("Stops the running program on all computers in the selected range");

		startGroupButton.addActionListener(new StartStopGroupButtonListener(this, startButtonText, stopButtonText));
		stopGroupButton.addActionListener(new StartStopGroupButtonListener(this, startButtonText, stopButtonText));

		JPanel startStopButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		startStopButtonPanel.setOpaque(false);
		startStopButtonPanel.add(startGroupButton);
		startStopButtonPanel.add(new JLabel());
		startStopButtonPanel.add(stopGroupButton);

		contentPane.add(startStopButtonPanel, "growx,wrap");

		setContentPane(contentPane);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
	}

	/**
	 * {@link StartStopGroupButtonObserver}
     */
	@Override public void notifyGroupExeRequest(AppState state)
	{
		int start = fromClientComboBox.getSelectedIndex();
		int stop = toClientComboBox.getSelectedIndex();

		int smallestId = (start < stop) ? start : stop;
		int largestId = (start == smallestId) ? stop : start;
		ArrayList<String> ipAddresses = new ArrayList<>();
		for (int i = smallestId; i <= largestId; ++i) {
			ipAddresses.add(labComputers.get(i).getIp());
		}

		String appName = String.valueOf(appNameComboBox.getSelectedItem());

		viewAppExeObserver.notifyAppExeRequest(appName, state, ipAddresses);
	}

	/**
	 * {@link StartStopButtonObserver}
     */
	@Override public void notifyExeRequest(AppState appState, String clientIp)
	{
		String appName = String.valueOf(appNameComboBox.getSelectedItem());
		ArrayList<String> ipAddresses = new ArrayList<>();
		ipAddresses.add(clientIp);
		viewAppExeObserver.notifyAppExeRequest(appName, appState, ipAddresses);
	}

	/**
	 * {@link LabView}
     */
	@Override public void addClient(String clientName, String clientIp)
	{
		computerLayoutPanel.updateComputerConnectionState(clientIp, ConnectionState.CONNECTED);
		computersConnectedPanel.incrementComputersConnected();
		contentPane.validate();
		pack();
	}

	/**
	 * {@link LabView}
	 */
	@Override public void updateClient(String clientIp, AppExe appExe)
	{
		computerLayoutPanel.updateAppExeState(clientIp, appExe);
		contentPane.validate();
		pack();
	}

	/**
	 * {@link LabView}
	 */
	@Override
	public void removeClient(String clientIp)
	{
		computerLayoutPanel.updateComputerConnectionState(clientIp, ConnectionState.DISCONNECTED);
		computersConnectedPanel.decrementComputersConnected();
		contentPane.validate();
		pack();
	}
}
