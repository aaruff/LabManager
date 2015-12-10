/**
 *
 */
package edu.nyu.cess.remote.server;

import edu.nyu.cess.remote.server.ui.NoInsetsPanel;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

;

/**
 * @author Anwar A. Ruff
 */
public class DashboardView extends JFrame implements ActionListener, LiteClientsObserver {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, JPanel> liteClientPanels = new HashMap<String, JPanel>();

	private final HashMap<String, JButton> clientStartButtons = new HashMap<String, JButton>();
	private final HashMap<String, JButton> clientStopButtons = new HashMap<String, JButton>();

	private final HashMap<String, JLabel> applicationStateLabels = new HashMap<String, JLabel>();
	private final HashMap<String, JLabel> clientDescriptionLabels = new HashMap<String, JLabel>();

	private final JPanel contentPane = new JPanel(new GridBagLayout());
	private final JPanel clientPanel = new JPanel(new GridLayout(0, 6, 10, 10));
	private final JPanel applicationExecutionRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel applicationMessageRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel messageRangeCards = new JPanel(new CardLayout());
	private final JPanel applicationMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel rangeMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private final JPanel applicationSelectionPanel = new JPanel(new GridBagLayout());
	private final JPanel clientMessagingPanel = new JPanel(new GridBagLayout());

	JTextField messageTextField = new JTextField(50);
	JLabel messageJLabel = new JLabel();

	private JComboBox clientApplicationsComboBox;
	private JComboBox clientsLowerBoundComboBox;
	private JComboBox clientsUpperBoundComboBox;
	private JComboBox clientsMessageLowerBound;
	private JComboBox clientsMessageUpperBound;
	private JComboBox connectedComputersComboBox;

	JRadioButton singleRadioButton;
	JRadioButton rangeRadioButton;

	private final Server server;

	public DashboardView(Server server) {
		super("CESS Application Remote");
		this.server = server;
	}

	/**
	 *
	 */
	public void buildGUI() {

		/*
		 * Attempt To Set Look And Feel
		 */
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception e) {}

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		/*
		 * Clients Panel
		 */
		TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
				"Connections: " + server.getClientPool().size());
		clientPanel.setBorder(titledBorder);
		//clientPanel.setBorder(new TitledBorder("Computers Connected: " + server.getLiteClients().size()));
		clientPanel.setBackground(Color.white);

		GridBagConstraints constraint = getConstraint(0, 0, 1.0, 0.8);
		constraint.fill = GridBagConstraints.BOTH;
		constraint.gridwidth = 2;
		constraint.insets = new Insets(10, 0, 0, 0);

		contentPane.setBackground(Color.white);
		contentPane.add(clientPanel, constraint);

		/*
		 * Applicaiton Range Execution Panel
		 */
		setApplicationRangePanel(new JComboBox(), new JComboBox());

		applicationSelectionPanel.setBackground(Color.white);
		applicationSelectionPanel.setOpaque(false);

		JLabel applicationLabel = new JLabel("Select a Program: ");

		clientApplicationsComboBox = new JComboBox(server.getApplicationNames());

		JPanel programSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		programSelectionPanel.setBackground(Color.white);
		programSelectionPanel.setOpaque(false);
		programSelectionPanel.add(applicationLabel);
		programSelectionPanel.add(clientApplicationsComboBox);

		constraint = getConstraint(0, 0, 1.0, 0.5);
		constraint.insets = new Insets(0, 5, 0, 0);
		constraint.fill = GridBagConstraints.HORIZONTAL;

		applicationSelectionPanel.add(programSelectionPanel, constraint);

		JButton startApplicationButton = new JButton("Start Group");
		startApplicationButton.setToolTipText("Starts the selected program on all computers in the selected range.");

		JButton killApplicationButton = new JButton("Stop Group");
		killApplicationButton.setToolTipText("Stops the running program on all computers in the selected range");

		startApplicationButton.addActionListener(new StartClientsInRangeListener());
		killApplicationButton.addActionListener(new StopClientsInRangeListener());

		JPanel buttonRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonRangePanel.setOpaque(false);
		buttonRangePanel.add(startApplicationButton);
		buttonRangePanel.add(new JLabel());
		buttonRangePanel.add(killApplicationButton);

		constraint = getConstraint(0, 2, 0.5, 0.1);
		constraint.anchor = GridBagConstraints.WEST;
		constraint.insets = new Insets(0, 5, 0, 5);
		applicationSelectionPanel.add(buttonRangePanel, constraint);

		JPanel messageRadioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		messageRadioButtonPanel.setOpaque(false);
		messageRadioButtonPanel.setBorder(new TitledBorder("Select a Message Option"));

		rangeRadioButton = new JRadioButton("Computer Range");
		rangeRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setMessageCard(((JRadioButton) e.getItem()).getText());
			}
		});

		singleRadioButton = new JRadioButton("One Computer");
		singleRadioButton.setSelected(true);
		singleRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setMessageCard(((JRadioButton) e.getItem()).getText());
			}
		});

		ButtonGroup messageButtonGroup = new ButtonGroup();
		messageButtonGroup.add(singleRadioButton);
		messageButtonGroup.add(rangeRadioButton);

		messageRadioButtonPanel.add(singleRadioButton);
		messageRadioButtonPanel.add(rangeRadioButton);
		constraint = getConstraint(0, 0, 0.5, 0.0);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(messageRadioButtonPanel, constraint);

		setMessagePanel(new JComboBox());
		setMessageRangePanel(new JComboBox(), new JComboBox());

		constraint = getConstraint(0, 1, 1.0, 0.0);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		messageRangeCards.setOpaque(false);
		clientMessagingPanel.add(messageRangeCards, constraint);

		JButton messageSendingButton = new JButton("Send Message");
		messageSendingButton.setToolTipText("Send a message to all computers in the selected range.");
		messageSendingButton.addActionListener(new MessageClientsInRangeListener());

		rangeMessagePanel.setOpaque(false);
		rangeMessagePanel.add(messageSendingButton);
		rangeMessagePanel.add(messageTextField);

		constraint = getConstraint(0, 2, 0.5, 0.0);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(rangeMessagePanel, constraint);

		constraint = getConstraint(0, 3, 1.0, 0.0);
		constraint.anchor = GridBagConstraints.WEST;
		constraint.insets = new Insets(0, 5, 0, 0);
		clientMessagingPanel.add(messageJLabel, constraint);

		clientMessagingPanel.setOpaque(false);

		JTabbedPane controlTab = new JTabbedPane();
		controlTab.add("Program Execution", applicationSelectionPanel);
		controlTab.add("Message Sending", clientMessagingPanel);

		NoInsetsPanel tabPanel = new NoInsetsPanel(new GridBagLayout());
		//tabPanel.setBorder(new TitledBorder(""));
		tabPanel.setBorder(BorderFactory.createEmptyBorder());
		tabPanel.setBackground(Color.white);
		tabPanel.setOpaque(false);
		constraint = getConstraint(0, 0, 1, 1);
		constraint.fill = GridBagConstraints.BOTH;
		tabPanel.add(controlTab, constraint);

		constraint = getConstraint(1, 3, 0.5, 0.1);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.anchor = GridBagConstraints.PAGE_END;
		constraint.ipady = 20;
		constraint.insets = new Insets(0, 0, 10, 0);
		contentPane.add(tabPanel, constraint);

		setContentPane(contentPane);
		pack();
	}

	/**
	 * Returns a {@link GridBagConstraints} with the following parameters set.
	 *
	 * @param gridx
	 *            grid x coordinate position
	 * @param gridy
	 *            grid y coordinate position
	 * @param weightx
	 *            row weight distribution
	 * @param weighty
	 *            column weight distribution
	 * @return
	 */
	public GridBagConstraints getConstraint(int gridx, int gridy, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.gridx = gridx;
		constraints.gridy = gridy;

		return constraints;
	}

	/**
	 * Sets the application range panel, which contains two {@link JComboBox}s.
	 *
	 * @param lowerBound
	 *            the {@link JComboBox} used for selecting a lower bound client.
	 * @param upperBound
	 *            the {@link JComboBox} used for selecting an upper bound
	 *            client.
	 */
	private void setApplicationRangePanel(JComboBox lowerBound, JComboBox upperBound) {
		applicationExecutionRangePanel.removeAll();
		applicationExecutionRangePanel.setBackground(Color.white);
		applicationExecutionRangePanel.setOpaque(true);
		applicationExecutionRangePanel.add(new JLabel("Computer Range: "));
		applicationExecutionRangePanel.add(lowerBound);
		applicationExecutionRangePanel.add(new JLabel(" - "));
		applicationExecutionRangePanel.add(upperBound);

		GridBagConstraints constraint = getConstraint(0, 1, 1.0, 0.4);
		constraint.fill = GridBagConstraints.BOTH;
		constraint.insets = new Insets(0, 5, 0, 0);

		applicationSelectionPanel.add(applicationExecutionRangePanel, constraint);
	}

	private void setMessageRangePanel(JComboBox lowerBound, JComboBox upperBound) {
		applicationMessageRangePanel.removeAll();
		applicationMessageRangePanel.setOpaque(false);
		applicationMessageRangePanel.add(new JLabel("Select a Computer Range: "));
		applicationMessageRangePanel.add(lowerBound);
		applicationMessageRangePanel.add(new JLabel(" - "));
		applicationMessageRangePanel.add(upperBound);

		messageRangeCards.add(applicationMessageRangePanel, "Computer Range");
	}

	private void setMessagePanel(JComboBox computers) {
		applicationMessagePanel.removeAll();
		applicationMessagePanel.setOpaque(false);
		applicationMessagePanel.add(new JLabel("Select a Computer: "));
		applicationMessagePanel.add(computers);

		messageRangeCards.add(applicationMessagePanel, "One Computer");
	}

	private void setMessageCard(String radioButtonID) {
		CardLayout cl = (CardLayout) (messageRangeCards.getLayout());
		cl.show(messageRangeCards, radioButtonID);
	}

	/**
	 * This method is called when a {@link LiteClient} is added to the
	 * {@link ClientPool} collection.
	 */
	public void updateLiteClientAdded(String ipAddress) {
		ClientPool clientPool = server.getClientPool();
		LiteClient liteClient = clientPool.getByIp(ipAddress);
		SwingUtilities.invokeLater(new AddClientRunnable(ipAddress, liteClient));
	}

	/**
	 * This method is called when a {@link LiteClient} is removed from the
	 * {@link ClientPool} collection.
	 */
	public void updateLiteClientRemoved(String ipAddress) {
		SwingUtilities.invokeLater(new RemoveClientRunnable(ipAddress));
	}

	/**
	 * This method is called when a {@link LiteClient}s state, in the
	 * {@link ClientPool} collection, has been updated (not necessarily
	 * changed).
	 */
	public void updateLiteClientStateChanged(LiteClient liteClient) {
		SwingUtilities.invokeLater(new UpdateClient(liteClient));
	}

	public void updateLiteClientHostNameChanged(LiteClient liteClient) {
		System.out.println("Hostname chaged to:" + liteClient.getHostName() + "\n");
		SwingUtilities.invokeLater(new UpdateClient(liteClient));
	}

	/**
	 * Overriden actionPerform required when extending JPanel.
	 */
	public void actionPerformed(ActionEvent e) {}

	private class UpdateClient implements Runnable {
		LiteClient liteClient;

		public UpdateClient(LiteClient liteClient) {
			this.liteClient = liteClient;
		}

		public void run() {
			if (liteClient.isApplicationRunning()) {
				applicationStateLabels.get(liteClient.getIPAddress()).setText(
						"Running: " + liteClient.getApplicationName());
				(applicationStateLabels.get(liteClient.getIPAddress())).setForeground(new Color(73, 143, 0));
				clientStartButtons.get(liteClient.getIPAddress()).setEnabled(false);
				clientStopButtons.get(liteClient.getIPAddress()).setEnabled(true);
				clientStopButtons.get(liteClient.getIPAddress()).setToolTipText("Stops running application.");
				if(!liteClient.getHostName().isEmpty()) {
					clientDescriptionLabels.get(liteClient.getIPAddress()).setText("" + liteClient.getHostName());
				}
				contentPane.validate();
				repaint();
			}
			else {
				applicationStateLabels.get(liteClient.getIPAddress()).setText("Stopped");
				applicationStateLabels.get(liteClient.getIPAddress()).setForeground(Color.red);
				clientStartButtons.get(liteClient.getIPAddress()).setEnabled(true);
				clientStopButtons.get(liteClient.getIPAddress()).setEnabled(false);
				if(!liteClient.getHostName().isEmpty()) {
					clientDescriptionLabels.get(liteClient.getIPAddress()).setText("" + liteClient.getHostName());
				}
				contentPane.validate();
				repaint();
			}
		}
	}

	private class AddClientRunnable implements Runnable {
		String ipAddress;
		LiteClient liteClient;

		public AddClientRunnable(String ipAddress, LiteClient liteClient) {
			this.ipAddress = ipAddress;
			this.liteClient = liteClient;
		}

		public void run() {
			System.out.println("Adding client " + ipAddress + " to the clientConnectionPanel");

			JPanel panel = new JPanel() {
				 private final int gradientSize = 18;
				    private final Color lighterColor = new Color(250, 250, 250);
				    private final Color darkerColor = new Color(225, 225, 230);
				    private final Color edgeColor = new Color(140, 145, 145);
				    private final Stroke edgeStroke = new BasicStroke(1);
				    private final GradientPaint upperGradient = new GradientPaint(
				            0, 0, lighterColor,
				            0, gradientSize, darkerColor);

				@Override
			    public void paintComponent(Graphics g) {

			        Graphics2D g2 = (Graphics2D) g;
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                            RenderingHints.VALUE_ANTIALIAS_ON);
			        float gradientPerc = (float)gradientSize/getHeight();
			        LinearGradientPaint lgp = new LinearGradientPaint(0,0,0,getHeight()-1,
			           new float[] {0, gradientPerc, 1-gradientPerc, 1f},
			           new Color[] {lighterColor, darkerColor, darkerColor, lighterColor});
			        g2.setPaint(lgp);
			        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1,
			            gradientSize, gradientSize);
			        g2.setColor(edgeColor);
			        g2.setStroke(edgeStroke);
			        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1,
			            gradientSize, gradientSize);
			    }
			};
			//panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			JLabel descriptionLabel;
			if (!liteClient.getHostName().isEmpty()) {
				descriptionLabel = new JLabel("" + liteClient.getHostName());
			}
			else {
				descriptionLabel = new JLabel("" + liteClient.getIPAddress());
			}
			descriptionLabel.setForeground(new Color(0, 0, 128));
			clientDescriptionLabels.put(ipAddress, descriptionLabel);

			JPanel applicationStatePanel = new JPanel(new FlowLayout());
			applicationStatePanel.setOpaque(false);
			JLabel applicationStateLabel = new JLabel("Stopped");
			applicationStateLabel.setForeground(Color.red);
			applicationStatePanel.add(applicationStateLabel);
			applicationStateLabels.put(ipAddress, applicationStateLabel);
			panel.add(applicationStatePanel);

			JButton startButton = new JButton("Start");
			startButton.setToolTipText("Starts selected application on " + ipAddress + ".");
			clientStartButtons.put(ipAddress, startButton);
			clientStartButtons.get(ipAddress).addActionListener(new StartAction(ipAddress));

			JButton stopButton = new JButton("Stop");
			clientStopButtons.put(ipAddress, stopButton);
			clientStopButtons.get(ipAddress).addActionListener(new StopAction(ipAddress));
			clientStopButtons.get(ipAddress).setEnabled(false);

			JPanel clientDescriptionPanel = new JPanel(new FlowLayout());
			clientDescriptionPanel.setOpaque(false);
			clientDescriptionPanel.add(descriptionLabel);
			panel.add(clientDescriptionPanel);

			JPanel buttonPanel = new JPanel(new FlowLayout());
			buttonPanel.setOpaque(false);
			buttonPanel.add(clientStartButtons.get(ipAddress));
			buttonPanel.add(clientStopButtons.get(ipAddress));
			panel.add(buttonPanel);

			liteClientPanels.put(ipAddress, panel);

			clientPanel.removeAll();

			LiteClient[] sortedClients = server.getClientPool().getSortedLiteClients();
			String[] clientHostNames = new String[sortedClients.length];
			for (int i = 0; i < sortedClients.length; ++i) {
				clientHostNames[i] = sortedClients[i].getHostName();
				clientPanel.add(liteClientPanels.get(sortedClients[i].getIPAddress()));
			}

			TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
					"Connections: " + sortedClients.length);
			clientPanel.setBorder(titledBorder);

			clientsLowerBoundComboBox = new JComboBox(clientHostNames);
			clientsUpperBoundComboBox = new JComboBox(clientHostNames);

			setApplicationRangePanel(clientsLowerBoundComboBox, clientsUpperBoundComboBox);
			setMessagePanel(new JComboBox(clientHostNames));
			setMessageRangePanel(new JComboBox(clientHostNames), new JComboBox(clientHostNames));

			GridBagConstraints constraint = getConstraint(0, 1, 1.0, 0.0);
			constraint.fill = GridBagConstraints.HORIZONTAL;
			clientMessagingPanel.add(messageRangeCards, constraint);

			pack();
			contentPane.validate();

			if (singleRadioButton.isSelected()) {
				setMessageCard(singleRadioButton.getText());
			}
			else {
				setMessageCard(rangeRadioButton.getText());
			}
		}
	}

	private class RemoveClientRunnable implements Runnable {
		String ipAddress;

		public RemoveClientRunnable(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public void run() {
			System.out.println("Removing client " + ipAddress + " from the clientConnectionPanel");

			clientPanel.remove(liteClientPanels.get(ipAddress));
			liteClientPanels.remove(ipAddress);

			clientDescriptionLabels.remove(ipAddress);
			applicationStateLabels.remove(ipAddress);

			clientStartButtons.remove(ipAddress);
			clientStopButtons.remove(ipAddress);

			LiteClient[] sortedClients = server.getClientPool().getSortedLiteClients();
			String[] clientHostNames = new String[sortedClients.length];
			for (int i = 0; i < sortedClients.length; ++i) {
				clientHostNames[i] = sortedClients[i].getHostName();
			}

			clientPanel.setBorder(new TitledBorder("Computers Connected: " + clientHostNames.length));

			clientsLowerBoundComboBox = new JComboBox(clientHostNames);
			clientsUpperBoundComboBox = new JComboBox(clientHostNames);

			setApplicationRangePanel(clientsLowerBoundComboBox, clientsUpperBoundComboBox);

			connectedComputersComboBox = new JComboBox(clientHostNames);
			setMessagePanel(connectedComputersComboBox);

			clientsMessageLowerBound = new JComboBox(clientHostNames);
			clientsMessageUpperBound = new JComboBox(clientHostNames);
			setMessageRangePanel(clientsMessageLowerBound, clientsMessageUpperBound);

			GridBagConstraints constraint = getConstraint(0, 1, 1.0, 0.0);
			constraint.fill = GridBagConstraints.HORIZONTAL;
			clientMessagingPanel.add(messageRangeCards, constraint);

			pack();
			contentPane.validate();

			if (singleRadioButton.isSelected()) {
				setMessageCard(singleRadioButton.getText());
			}
			else {
				setMessageCard(rangeRadioButton.getText());
			}

			repaint();

		}

	}

	private class MessageClientsInRangeListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ClientPool clientPool = server.getClientPool();
			String message = messageTextField.getText();
			messageTextField.setText("");

			if (message.isEmpty()) {
				messageJLabel.setText("Messaging Error: No text entered");
				return;
			}

			if (singleRadioButton.isSelected()) {
				String hostNameSelected = (String) connectedComputersComboBox.getSelectedItem();

				if (hostNameSelected.isEmpty()) {
					messageJLabel.setText("Messaging Error: A computer was not selected.");
					return;
				}
				server.messageClient(message, clientPool.getByHostname(hostNameSelected).getIPAddress());
			}
			else {
				server.messageClientInRange(message,
						(String) clientsMessageLowerBound.getSelectedItem(), (String) clientsMessageUpperBound.getSelectedItem());
			}
		}
	}

	private class StopClientsInRangeListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String clientLowerBound = (String) clientsLowerBoundComboBox.getSelectedItem();
			String clientUpperBound = (String) clientsUpperBoundComboBox.getSelectedItem();
			server.stopAppInRange(clientLowerBound, clientUpperBound);
		}
	}

	private class StartClientsInRangeListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String applicationSelected = (String) clientApplicationsComboBox.getSelectedItem();
			String clientLowerBound = (String) clientsLowerBoundComboBox.getSelectedItem();
			String clientUpperBound = (String) clientsUpperBoundComboBox.getSelectedItem();

			server.startAppInRange(applicationSelected, clientLowerBound, clientUpperBound);

		}
	}

	private class StartAction implements ActionListener {
		String ipAddress;

		public StartAction(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public void actionPerformed(ActionEvent e) {
			String applicationSelected = (String) clientApplicationsComboBox.getSelectedItem();

			System.out.println(ipAddress + " Start button selected");

			ClientPool clientPool = server.getClientPool();
			LiteClient liteClient = clientPool.getByIp(ipAddress);
			liteClient.setApplicationName(applicationSelected);

			server.startApplication(applicationSelected, ipAddress);
		}

	}

	private class StopAction implements ActionListener {
		String ipAddress;

		public StopAction(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println(ipAddress + " Stop button selected");
			server.stopApplication(ipAddress);
		}

	}


}
