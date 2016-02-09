/**
 *
 */
package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.Server;
import edu.nyu.cess.remote.server.client.ClientPool;
import edu.nyu.cess.remote.server.client.LiteClient;
import edu.nyu.cess.remote.server.client.LiteClientNotFoundException;
import edu.nyu.cess.remote.server.client.LiteClientsObserver;
import edu.nyu.cess.remote.server.gui.listeners.MessageClientsInRangeListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

/**
 * @author Anwar A. Ruff
 */
public class LabViewFrame extends JFrame implements LiteClientsObserver
{
	final static Logger log = Logger.getLogger(Server.class);

	private static final long serialVersionUID = 1L;

	private final HashMap<String, JPanel> liteClientPanels = new HashMap<>();

	private final HashMap<String, JButton> clientStartButtons = new HashMap<>();
	private final HashMap<String, JButton> clientStopButtons = new HashMap<>();

	private final HashMap<String, JLabel> applicationStateLabels = new HashMap<>();
	private final HashMap<String, JLabel> clientDescriptionLabels = new HashMap<>();

	private final JPanel contentPane = new JPanel(new GridBagLayout());
	private JPanel clientsContainerPanel;
	private JPanel computerRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel applicationMessageRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel messageRangeCards = new JPanel(new CardLayout());
	private final JPanel applicationMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JPanel rangeMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private JPanel appExecutionPanel;
	private final JPanel clientMessagingPanel = new JPanel(new GridBagLayout());

	JTextField messageTextField = new JTextField(50);
	JLabel messageJLabel = new JLabel();

	private JComboBox<String> appNameComboBox;
	private JComboBox clientsLowerBoundComboBox;
	private JComboBox clientsUpperBoundComboBox;
	private JComboBox clientsMessageLowerBound;
	private JComboBox clientsMessageUpperBound;
	private JComboBox connectedComputersComboBox;

	JRadioButton singleRadioButton;
	JRadioButton rangeRadioButton;

	private final ExecutionRequestObserver executionRequestObserver;
	private ClientPool clientPool;
	private String[] appNames;

	public LabViewFrame(ExecutionRequestObserver executionRequestObserver, ClientPool clientPool, String[] appNames)
	{
		super("CESS Lab Manager");
		this.executionRequestObserver = executionRequestObserver;
		this.clientPool = clientPool;
		this.appNames = appNames;

		setLookAndFeel();

		contentPane.setBackground(Color.white);

		clientsContainerPanel = getClientsPanel();
		contentPane.add(clientsContainerPanel, getClientPanelConstraints());

		appExecutionPanel = new JPanel(new GridBagLayout());
		appExecutionPanel.setBackground(Color.white);
		appExecutionPanel.setOpaque(false);

		computerRangePanel = getComputerRangePanel();
		appExecutionPanel.add(computerRangePanel, getAppExeRangeConstraints());

		appNameComboBox = new JComboBox<>(appNames);
		appExecutionPanel.add(getAppSelectionPanel(appNameComboBox), getAppSelectionPanelConstraints());

		appExecutionPanel.add(getStartStopGroupPanel(), getStartStopGroupPanelConstraints());

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
	}

	public void initialize()
	{
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
		clientsPanelConstraints = getConstraint(0, 0, 0.5, 0.0);
		clientsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(messageRadioButtonPanel, clientsPanelConstraints);

		setMessagePanel(new JComboBox());
		setMessageRangePanel(new JComboBox(), new JComboBox());

		clientsPanelConstraints = getConstraint(0, 1, 1.0, 0.0);
		clientsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		messageRangeCards.setOpaque(false);
		clientMessagingPanel.add(messageRangeCards, clientsPanelConstraints);

		JButton messageSendingButton = new JButton("Send Message");
		messageSendingButton.setToolTipText("Send a message to all computers in the selected range.");
		messageSendingButton.addActionListener(new MessageClientsInRangeListener(this));

		rangeMessagePanel.setOpaque(false);
		rangeMessagePanel.add(messageSendingButton);
		rangeMessagePanel.add(messageTextField);

		clientsPanelConstraints = getConstraint(0, 2, 0.5, 0.0);
		clientsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		clientMessagingPanel.add(rangeMessagePanel, clientsPanelConstraints);

		clientsPanelConstraints = getConstraint(0, 3, 1.0, 0.0);
		clientsPanelConstraints.anchor = GridBagConstraints.WEST;
		clientsPanelConstraints.insets = new Insets(0, 5, 0, 0);
		clientMessagingPanel.add(messageJLabel, clientsPanelConstraints);

		clientMessagingPanel.setOpaque(false);

		JTabbedPane controlTab = new JTabbedPane();
		controlTab.add("Program Execution", appExecutionPanel);
		controlTab.add("Message Sending", clientMessagingPanel);

		NoInsetsPanel tabPanel = new NoInsetsPanel(new GridBagLayout());
		//tabPanel.setBorder(new TitledBorder(""));
		tabPanel.setBorder(BorderFactory.createEmptyBorder());
		tabPanel.setBackground(Color.white);
		tabPanel.setOpaque(false);
		clientsPanelConstraints = getConstraint(0, 0, 1, 1);
		clientsPanelConstraints.fill = GridBagConstraints.BOTH;
		tabPanel.add(controlTab, clientsPanelConstraints);

		clientsPanelConstraints = getConstraint(1, 3, 0.5, 0.1);
		clientsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		clientsPanelConstraints.anchor = GridBagConstraints.PAGE_END;
		clientsPanelConstraints.ipady = 20;
		clientsPanelConstraints.insets = new Insets(0, 0, 10, 0);
		contentPane.add(tabPanel, clientsPanelConstraints);

		setContentPane(contentPane);
		pack();
	}

	/**
	 * Returns a {@link GridBagConstraints} with the following parameters set.
	 *
	 * @param gridx grid x coordinate position
	 * @param gridy grid y coordinate position
	 * @param weightx row weight distribution
	 * @param weighty column weight distribution
	 * @return grid constraints
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
	 * Sets the message JPanel.
	 *
	 * @param lowerBound client name with the lowest ordinal value
	 * @param upperBound client name with the greatest ordinal value
     */
	private void setMessageRangePanel(JComboBox lowerBound, JComboBox upperBound) {
		applicationMessageRangePanel.removeAll();
		applicationMessageRangePanel.setOpaque(false);
		applicationMessageRangePanel.add(new JLabel("Select a Computer Range: "));
		applicationMessageRangePanel.add(lowerBound);
		applicationMessageRangePanel.add(new JLabel(" - "));
		applicationMessageRangePanel.add(upperBound);

		messageRangeCards.add(applicationMessageRangePanel, "Computer Range");
	}

	/**
	 * Adds the JCombobox to the message panel.
	 * @param computers computer combo box
     */
	private void setMessagePanel(JComboBox computers) {
		applicationMessagePanel.removeAll();
		applicationMessagePanel.setOpaque(false);
		applicationMessagePanel.add(new JLabel("Select a Computer: "));
		applicationMessagePanel.add(computers);

		messageRangeCards.add(applicationMessagePanel, "One Computer");
	}

	/**
	 * Sets the card layout.
	 * @param radioButtonID Radio button value
     */
	private void setMessageCard(String radioButtonID) {
		CardLayout cl = (CardLayout) (messageRangeCards.getLayout());
		cl.show(messageRangeCards, radioButtonID);
	}

	/**
	 * This method is called when a {@link LiteClient} is added to the
	 * {@link ClientPool} collection.
	 */
	public void updateLiteClientAdded(String ipAddress) {
		try {
			LiteClient liteClient = clientPool.getByIp(ipAddress);
			SwingUtilities.invokeLater(new AddClientRunnable(this, ipAddress, liteClient));
		}
		catch (LiteClientNotFoundException e) {
			log.error("Client not found", e);
		}
	}

	/**
	 * This method is called when a {@link LiteClient} is removed from the
	 * {@link ClientPool} collection.
	 */
	public void updateLiteClientRemoved(String ipAddress) {
		SwingUtilities.invokeLater(new RemoveClientRunnable(this, ipAddress));
	}

	/**
	 * This method is called when a {@link LiteClient}s state, in the
	 * {@link ClientPool} collection, has been updated (not necessarily
	 * changed).
	 */
	public void updateLiteClientStateChanged(LiteClient liteClient) {
		SwingUtilities.invokeLater(new UpdateClient(this, liteClient));
	}

	public void updateLiteClientHostNameChanged(LiteClient liteClient) {
		System.out.println("Hostname chaged to:" + liteClient.getHostName() + "\n");
		SwingUtilities.invokeLater(new UpdateClient(this, liteClient));
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
		String statusText = "Connections: " + clientPool.size();
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
		GridBagConstraints clientsPanelConstraints = getConstraint(0, 0, 1.0, 0.8);
		clientsPanelConstraints.fill = GridBagConstraints.BOTH;
		clientsPanelConstraints.gridwidth = 2;
		clientsPanelConstraints.insets = new Insets(10, 0, 0, 0);

		return clientsPanelConstraints;
	}

	private JPanel getComputerRangePanel() {
		JPanel computerRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		computerRangePanel.setBackground(Color.white);
		computerRangePanel.setOpaque(true);
		computerRangePanel.add(new JLabel("Computer Range: "));
		computerRangePanel.add(new JComboBox<String>());
		computerRangePanel.add(new JLabel(" - "));
		computerRangePanel.add(new JComboBox<String>());

		return computerRangePanel;
	}

	private GridBagConstraints getAppExeRangeConstraints()
	{
		GridBagConstraints constraint = getConstraint(0, 1, 1.0, 0.4);
		constraint.fill = GridBagConstraints.BOTH;
		constraint.insets = new Insets(0, 5, 0, 0);

		return constraint;
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
		GridBagConstraints clientsPanelConstraints = getConstraint(0, 0, 1.0, 0.5);
		clientsPanelConstraints.insets = new Insets(0, 5, 0, 0);
		clientsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		return clientsPanelConstraints;
	}

	private JPanel getStartStopGroupPanel()
	{
		JButton startGroupButton = new JButton("Start Group");
		startGroupButton.setToolTipText("Starts the selected program on all computers in the selected range.");

		JButton stopGroupButton = new JButton("Stop Group");
		stopGroupButton.setToolTipText("Stops the running program on all computers in the selected range");

		startGroupButton.addActionListener(new StartGroupListener(this));
		stopGroupButton.addActionListener(new StopGroupListener(this));

		JPanel startStopButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		startStopButtonPanel.setOpaque(false);
		startStopButtonPanel.add(startGroupButton);
		startStopButtonPanel.add(new JLabel());
		startStopButtonPanel.add(stopGroupButton);
		return startStopButtonPanel;
	}

	private GridBagConstraints getStartStopGroupPanelConstraints()
	{
		GridBagConstraints clientsPanelConstraints = getConstraint(0, 2, 0.5, 0.1);
		clientsPanelConstraints.anchor = GridBagConstraints.WEST;
		clientsPanelConstraints.insets = new Insets(0, 5, 0, 5);
		return clientsPanelConstraints;
	}


}
