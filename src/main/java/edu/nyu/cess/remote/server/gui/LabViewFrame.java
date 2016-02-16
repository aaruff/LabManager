/**
 *
 */
package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.common.app.AppState;
import edu.nyu.cess.remote.server.gui.listeners.StartStopButtonListener;
import edu.nyu.cess.remote.server.gui.listeners.StartStopGroupButtonListener;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;
import edu.nyu.cess.remote.server.gui.observers.StartStopGroupButtonObserver;
import edu.nyu.cess.remote.server.gui.observers.ViewAppExeObserver;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Anwar A. Ruff
 */
public class LabViewFrame extends JFrame implements StartStopGroupButtonObserver, StartStopButtonObserver, LabView
{
	final static Logger log = Logger.getLogger(LabViewFrame.class);

	private static final long serialVersionUID = 1L;

	private final HashMap<String, JPanel> liteClientPanels = new HashMap<>();

	private final HashMap<String, JButton> clientStartButtons = new HashMap<>();
	private final HashMap<String, JButton> clientStopButtons = new HashMap<>();

	private final HashMap<String, JLabel> applicationStateLabels = new HashMap<>();
	private final HashMap<String, JLabel> clientDescriptionLabels = new HashMap<>();

	private final JPanel contentPane = new JPanel(new GridBagLayout());
	private JPanel computerRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private ArrayList<ViewClient> viewClients = new ArrayList<>();

    private final String[] appNames;

    private JPanel clientsContainerPanel;
	private JPanel appExecutionPanel;

	private JComboBox<String> appNameComboBox;
	private JComboBox<String> fromClientComboBox;
	private JComboBox<String> toClientComboBox;

	private ViewAppExeObserver viewAppExeObserver;

	public LabViewFrame(String[] appNames, ViewAppExeObserver viewAppExeObserver)
	{
		this.appNames = appNames;
		this.viewAppExeObserver = viewAppExeObserver;
	}

	public void render()
	{
		setLookAndFeel();

		contentPane.setBackground(Color.white);

		clientsContainerPanel = getClientsPanel();
		contentPane.add(clientsContainerPanel, getClientPanelConstraints());

		appExecutionPanel = new JPanel(new GridBagLayout());
		appExecutionPanel.setBackground(Color.white);
		appExecutionPanel.setOpaque(false);

		appNameComboBox = new JComboBox<>(appNames);
		appExecutionPanel.add(getAppSelectionPanel(appNameComboBox), getAppSelectionPanelConstraints());

		fromClientComboBox = new JComboBox<>();
		toClientComboBox = new JComboBox<>();
		computerRangePanel = getClientRangePanel(fromClientComboBox, toClientComboBox);
		appExecutionPanel.add(computerRangePanel, getAppExeRangeConstraints());

		appExecutionPanel.add(getStartStopButtonPanel(), getStartStopButtonPanelConstraints());

		contentPane.add(appExecutionPanel, getAppControlPanelConstraints());

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);

		setContentPane(contentPane);
		pack();

	}

	/**
	 * {@link StartStopGroupButtonObserver}
     */
	@Override public void notifyGroupExeRequest(AppState state)
	{
		String fromClientName = String.valueOf(fromClientComboBox.getSelectedItem());
		String toClientName = String.valueOf(toClientComboBox.getSelectedItem());
		String app = String.valueOf(appNameComboBox.getSelectedItem());

		if (fromClientName.isEmpty() || toClientName.isEmpty()) {
			log.error("Failed attempt to start/stop clients across an invalid range.");
			return;
		}


		// TODO: Refactor
        //viewAppExeObserver.notifyAppExeRequest(app, state);
	}

	@Override public void notifyExeRequest(AppState appState, String clientName, String clientIp)
	{
		String app = String.valueOf(appNameComboBox.getSelectedItem());
		// TODO: Refactor
		//viewAppExeObserver.notifyAppExeRequest(, , networkInfo, app, appState);
	}

	/**
	 * {@link LabView}
     * @param clientName
     * @param clientIp
     */
	@Override public void addClient(String clientName, String clientIp)
	{
        viewClients.add(new ViewClient(clientName, clientIp));

		JPanel clientPanel = getClientPanel();

		clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));

		JLabel descriptionLabel = new JLabel("" + clientIp);
		descriptionLabel.setForeground(new Color(0, 0, 128));
		clientDescriptionLabels.put(clientIp, descriptionLabel);

		JPanel applicationStatePanel = new JPanel(new FlowLayout());
		applicationStatePanel.setOpaque(false);
		JLabel applicationStateLabel = new JLabel("Stopped");
		applicationStateLabel.setForeground(Color.red);
		applicationStatePanel.add(applicationStateLabel);
		applicationStateLabels.put(clientIp, applicationStateLabel);
		clientPanel.add(applicationStatePanel);

		String startButtonName = "Start";
		JButton startButton = new JButton(startButtonName);
		startButton.setToolTipText("Starts selected application on " + clientIp + ".");
		clientStartButtons.put(clientIp, startButton);

		String stopButtonName = "Stop";
		JButton stopButton = new JButton("Stop");
		clientStopButtons.put(clientIp, stopButton);
		clientStopButtons.get(clientIp).setEnabled(false);

		clientStartButtons.get(clientIp).addActionListener(
				new StartStopButtonListener(this, clientName, clientIp, startButtonName, stopButtonName));
		clientStopButtons.get(clientIp).addActionListener(
				new StartStopButtonListener(this, clientName, clientIp, startButtonName, stopButtonName));

		JPanel clientDescriptionPanel = new JPanel(new FlowLayout());
		clientDescriptionPanel.setOpaque(false);
		clientDescriptionPanel.add(descriptionLabel);
		clientPanel.add(clientDescriptionPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setOpaque(false);
		buttonPanel.add(clientStartButtons.get(clientIp));
		buttonPanel.add(clientStopButtons.get(clientIp));
		clientPanel.add(buttonPanel);

		liteClientPanels.put(clientIp, clientPanel);

		//--------------------------------------------------------------
		// Rebuild range combo boxes
		//--------------------------------------------------------------

		//TODO: Refactor
		/*
		clientPanel.removeAll();

		java.util.List<Client> sortedClients = clientPool.sort(Client.SORT_BY_HOSTNAME);
		for (Client client : sortedClients) {
			clientPanel.add(labViewFrame.liteClientPanels.get(client.getIPAddress()));
		}

		TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
				"Connections: " + sortedClients.size());
		clientPanel.setBorder(titledBorder);

		String[] hostNames = labViewFrame.clientPool.getHostNames(Client.SORT_BY_HOSTNAME).toArray(new String[labViewFrame.clientPool.size()]);
		clientsLowerBoundComboBox = new JComboBox<>(hostNames);
		clientsUpperBoundComboBox = new JComboBox<>(hostNames);

		setApplicationRangePanel(labViewFrame.clientsLowerBoundComboBox, labViewFrame.clientsUpperBoundComboBox);
		setMessagePanel(new JComboBox<>(hostNames));
		setMessageRangePanel(new JComboBox<>(hostNames), new JComboBox<>(hostNames));

		GridBagConstraints constraint = labViewFrame.getConstraint(0, 1, 1.0, 0.0);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(labViewFrame.messageRangeCards, constraint);
		*/

		pack();
		contentPane.validate();
	}

	@Override public void updateClient(String clientIp, AppExe appExe)
	{
		//TODO: Refactor
		/*
		if (client.isApplicationRunning()) {
			labViewFrame.applicationStateLabels.get(client.getIPAddress()).setText(
					"Running: " + client.getApplicationName());
			(labViewFrame.applicationStateLabels.get(client.getIPAddress())).setForeground(new Color(73, 143, 0));
			labViewFrame.clientStartButtons.get(client.getIPAddress()).setEnabled(false);
			labViewFrame.clientStopButtons.get(client.getIPAddress()).setEnabled(true);
			labViewFrame.clientStopButtons.get(client.getIPAddress()).setToolTipText("Stops running application.");
			if (!client.getHostName().isEmpty()) {
				labViewFrame.clientDescriptionLabels.get(client.getIPAddress()).setText("" + client.getHostName());
			}
			labViewFrame.contentPane.validate();
			labViewFrame.repaint();
		} else {
			labViewFrame.applicationStateLabels.get(client.getIPAddress()).setText("Stopped");
			labViewFrame.applicationStateLabels.get(client.getIPAddress()).setForeground(Color.red);
			labViewFrame.clientStartButtons.get(client.getIPAddress()).setEnabled(true);
			labViewFrame.clientStopButtons.get(client.getIPAddress()).setEnabled(false);
			if (!client.getHostName().isEmpty()) {
				labViewFrame.clientDescriptionLabels.get(client.getIPAddress()).setText("" + client.getHostName());
			}
			labViewFrame.contentPane.validate();
			labViewFrame.repaint();
		}
		*/
	}

	@Override
	public void removeClient(String clientIp)
	{
		//TODO: Refactor
		/*
		String clientIpAddress = networkInfo.getClientIp();
		clientsContainerPanel.remove(liteClientPanels.get(clientIpAddress));
		liteClientPanels.remove(clientIpAddress);

		clientDescriptionLabels.remove(clientIpAddress);
		applicationStateLabels.remove(clientIpAddress);

		clientStartButtons.remove(clientIpAddress);
		clientStopButtons.remove(clientIpAddress);

		String[] hostNames = clientPool.getHostNames(Client.SORT_BY_HOSTNAME).toArray(new String[clientPool.size()]);

		clientsContainerPanel.setBorder(new TitledBorder("Computers Connected: " + hostNames.length));

		clientsLowerBoundComboBox = new JComboBox<>(hostNames);
		clientsUpperBoundComboBox = new JComboBox<>(hostNames);

		setApplicationRangePanel(clientsLowerBoundComboBox, clientsUpperBoundComboBox);

		connectedComputersComboBox = new JComboBox<>(hostNames);
		setMessagePanel(connectedComputersComboBox);

		clientsMessageLowerBound = new JComboBox<>(hostNames);
		clientsMessageUpperBound = new JComboBox<>(hostNames);
		setMessageRangePanel(clientsMessageLowerBound, labViewFrame.clientsMessageUpperBound);

		GridBagConstraints constraint = getConstraint(0, 1, 1.0, 0.0);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(messageRangeCards, constraint);

		pack();
		contentPane.validate();

		repaint();
		*/
	}

	/* ----------------------------------------------------
	 *                       PRIVATE
	 * ---------------------------------------------------- */


    private JPanel getClientPanel()
	{
		return new JPanel() {
			private final int gradientSize = 18;
			private final Color lighterColor = new Color(250, 250, 250);
			private final Color darkerColor = new Color(225, 225, 230);
			private final Color edgeColor = new Color(140, 145, 145);
			private final Stroke edgeStroke = new BasicStroke(1);
			private final GradientPaint upperGradient = new GradientPaint(0, 0, lighterColor, 0, gradientSize, darkerColor);
			@Override
			public void paintComponent(Graphics g) {

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				float gradientPerc = (float) gradientSize / getHeight();
				LinearGradientPaint lgp = new LinearGradientPaint(0, 0, 0, getHeight() - 1,
						new float[]{0, gradientPerc, 1 - gradientPerc, 1f},
						new Color[]{lighterColor, darkerColor, darkerColor, lighterColor});
				g2.setPaint(lgp);
				g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
						gradientSize, gradientSize);
				g2.setColor(edgeColor);
				g2.setStroke(edgeStroke);
				g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
						gradientSize, gradientSize);
			}
		};

	}

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

	private void setLookAndFeel()
	{
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception e) {
			log.warn("Unable to set look and feel", e);
		}
	}

	private JPanel getClientsPanel()
	{
		String statusText = "Connections: " + 0;
		int rows = 0;
		int columns = 6;
		int horizontalGap = 10;
		int verticalGap = 10;
		JPanel clientPanel = new JPanel(new GridLayout(rows, columns, horizontalGap, verticalGap));
		clientPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), statusText));
		clientPanel.setBackground(Color.white);

		return clientPanel;
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

	private JPanel getClientRangePanel(JComboBox<String> from, JComboBox<String> to) {
		JPanel computerRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		computerRangePanel.setBackground(Color.white);
		computerRangePanel.setOpaque(true);
		computerRangePanel.add(new JLabel("Computer Range: "));
		computerRangePanel.add(from);
		computerRangePanel.add(new JLabel(" - "));
		computerRangePanel.add(to);

		return computerRangePanel;
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

	private JPanel getStartStopButtonPanel()
	{
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
		return startStopButtonPanel;
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
