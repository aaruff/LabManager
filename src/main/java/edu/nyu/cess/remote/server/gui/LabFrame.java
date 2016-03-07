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
import edu.nyu.cess.remote.server.lab.Computer;
import edu.nyu.cess.remote.server.lab.LabLayout;
import edu.nyu.cess.remote.server.lib.ComputerNameAlphaNumericSort;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Anwar A. Ruff
 */
public class LabFrame extends JFrame implements StartStopGroupButtonObserver, StartStopButtonObserver, LabView
{
	final static Logger log = Logger.getLogger(LabFrame.class);

	private static final long serialVersionUID = 1L;

	private final JPanel contentPane = new JPanel(new GridBagLayout());

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

		contentPane.setBackground(Color.white);

		computerLayoutPanel = new ComputerLayoutPanel(labLayout, this);
		contentPane.add(computerLayoutPanel, getClientPanelConstraints());

		appExecutionPanel = new JPanel(new GridBagLayout());
		appExecutionPanel.setBackground(Color.white);
		appExecutionPanel.setOpaque(false);

		appNameComboBox = new JComboBox<>(appNames);
		appExecutionPanel.add(getAppSelectionPanel(appNameComboBox), getAppSelectionPanelConstraints());

		labComputers = labLayout.getAllComputers();
		Collections.sort(labComputers, new ComputerNameAlphaNumericSort());
		String[] names = new String[labComputers.size()];
		for (int i = 0; i < labComputers.size(); ++i) {
			names[i] = labComputers.get(i).getName();
		}

		fromClientComboBox = new JComboBox<>(new DefaultComboBoxModel<>(names));
		toClientComboBox = new JComboBox<>(new DefaultComboBoxModel<>(names));

		// Computer Range Panel
		computerRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		computerRangePanel.setBackground(Color.white);
		computerRangePanel.setOpaque(true);
		computerRangePanel.add(new JLabel("Computer Range: "));
		computerRangePanel.add(fromClientComboBox);
		computerRangePanel.add(new JLabel(" to "));
		computerRangePanel.add(toClientComboBox);
		appExecutionPanel.add(computerRangePanel, getAppExeRangeConstraints());

		// Start Stop Group Button Panel
		String startButtonText = "Start Group";
		JButton startGroupButton = new JButton(startButtonText);
		startGroupButton.setToolTipText("Starts the selected program on all computers in the selected range.");

		String stopButtonText = "Stop Group";
		JButton stopGroupButton = new JButton(stopButtonText);
		stopGroupButton.setToolTipText("Stops the running program on all computers in the selected range");

		startGroupButton.addActionListener(new StartStopGroupButtonListener(this, startButtonText, stopButtonText));
		stopGroupButton.addActionListener(new StartStopGroupButtonListener(this, startButtonText, stopButtonText));

		JPanel startStopButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		startStopButtonPanel.setOpaque(false);
		startStopButtonPanel.add(startGroupButton);
		startStopButtonPanel.add(new JLabel());
		startStopButtonPanel.add(stopGroupButton);
		appExecutionPanel.add(startStopButtonPanel, getStartStopButtonPanelConstraints());

		contentPane.add(appExecutionPanel, getAppControlPanelConstraints());

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
		contentPane.validate();
		pack();
	}

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */


	private GridBagConstraints getAppControlPanelConstraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 0.5;
		constraints.weighty = 0.1;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_END;
		constraints.ipady = 20;
		constraints.insets = new Insets(0, 0, 10, 0);

		return constraints;
	}

	private GridBagConstraints getClientPanelConstraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 0.8;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 0, 0, 0);

		return constraints;
	}

	private GridBagConstraints getAppExeRangeConstraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 0.4;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 5, 0, 0);

		return constraints;
	}

	private JPanel getAppSelectionPanel(JComboBox<String> comboBox)
	{
		JLabel applicationLabel = new JLabel("Select a Program: ");
		JPanel programSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		programSelectionPanel.setBackground(Color.white);
		programSelectionPanel.setOpaque(false);
		programSelectionPanel.add(applicationLabel);
		programSelectionPanel.add(comboBox);
		return programSelectionPanel;
	}

	private GridBagConstraints getAppSelectionPanelConstraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 5, 0, 0);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		return constraints;
	}

	private GridBagConstraints getStartStopButtonPanelConstraints()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 0.5;
		constraints.weighty = 0.1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 5);
		return constraints;
	}

}
