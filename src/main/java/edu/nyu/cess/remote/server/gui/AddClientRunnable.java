package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.client.LiteClient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

/**
 * Created by aruff on 2/9/16.
 */
class AddClientRunnable implements Runnable {
    private LabViewFrame labViewFrame;
    String ipAddress;
    LiteClient liteClient;

    public AddClientRunnable(LabViewFrame labViewFrame, String ipAddress, LiteClient liteClient) {
        this.labViewFrame = labViewFrame;
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
        //panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel descriptionLabel;
        if (!liteClient.getHostName().isEmpty()) {
            descriptionLabel = new JLabel("" + liteClient.getHostName());
        } else {
            descriptionLabel = new JLabel("" + liteClient.getIPAddress());
        }
        descriptionLabel.setForeground(new Color(0, 0, 128));
        labViewFrame.clientDescriptionLabels.put(ipAddress, descriptionLabel);

        JPanel applicationStatePanel = new JPanel(new FlowLayout());
        applicationStatePanel.setOpaque(false);
        JLabel applicationStateLabel = new JLabel("Stopped");
        applicationStateLabel.setForeground(Color.red);
        applicationStatePanel.add(applicationStateLabel);
        labViewFrame.applicationStateLabels.put(ipAddress, applicationStateLabel);
        panel.add(applicationStatePanel);

        JButton startButton = new JButton("Start");
        startButton.setToolTipText("Starts selected application on " + ipAddress + ".");
        labViewFrame.clientStartButtons.put(ipAddress, startButton);
        labViewFrame.clientStartButtons.get(ipAddress).addActionListener(new StartActionListener(labViewFrame, ipAddress));

        JButton stopButton = new JButton("Stop");
        labViewFrame.clientStopButtons.put(ipAddress, stopButton);
        labViewFrame.clientStopButtons.get(ipAddress).addActionListener(new StopActionListener(labViewFrame, ipAddress));
        labViewFrame.clientStopButtons.get(ipAddress).setEnabled(false);

        JPanel clientDescriptionPanel = new JPanel(new FlowLayout());
        clientDescriptionPanel.setOpaque(false);
        clientDescriptionPanel.add(descriptionLabel);
        panel.add(clientDescriptionPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(labViewFrame.clientStartButtons.get(ipAddress));
        buttonPanel.add(labViewFrame.clientStopButtons.get(ipAddress));
        panel.add(buttonPanel);

        labViewFrame.liteClientPanels.put(ipAddress, panel);

        labViewFrame.clientPanel.removeAll();

        java.util.List<LiteClient> sortedClients = labViewFrame.clientPool.sort(LiteClient.SORT_BY_HOSTNAME);
        for (LiteClient client : sortedClients) {
            labViewFrame.clientPanel.add(labViewFrame.liteClientPanels.get(client.getIPAddress()));
        }

        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                "Connections: " + sortedClients.size());
        labViewFrame.clientPanel.setBorder(titledBorder);

        String[] hostNames = labViewFrame.clientPool.getHostNames(LiteClient.SORT_BY_HOSTNAME).toArray(new String[labViewFrame.clientPool.size()]);
        labViewFrame.clientsLowerBoundComboBox = new JComboBox<>(hostNames);
        labViewFrame.clientsUpperBoundComboBox = new JComboBox<>(hostNames);

        labViewFrame.setApplicationRangePanel(labViewFrame.clientsLowerBoundComboBox, labViewFrame.clientsUpperBoundComboBox);
        labViewFrame.setMessagePanel(new JComboBox<>(hostNames));
        labViewFrame.setMessageRangePanel(new JComboBox<>(hostNames), new JComboBox<>(hostNames));

        GridBagConstraints constraint = labViewFrame.getConstraint(0, 1, 1.0, 0.0);
        constraint.fill = GridBagConstraints.HORIZONTAL;
        labViewFrame.clientMessagingPanel.add(labViewFrame.messageRangeCards, constraint);

        labViewFrame.pack();
        labViewFrame.contentPane.validate();

        if (labViewFrame.singleRadioButton.isSelected()) {
            labViewFrame.setMessageCard(labViewFrame.singleRadioButton.getText());
        } else {
            labViewFrame.setMessageCard(labViewFrame.rangeRadioButton.getText());
        }
    }
}
