package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.client.LiteClient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by aruff on 2/9/16.
 */
class RemoveClientRunnable implements Runnable {
    private LabViewFrame labViewFrame;
    String ipAddress;

    public RemoveClientRunnable(LabViewFrame labViewFrame, String ipAddress) {
        this.labViewFrame = labViewFrame;
        this.ipAddress = ipAddress;
    }

    public void run() {
        System.out.println("Removing client " + ipAddress + " from the clientConnectionPanel");

        labViewFrame.clientPanel.remove(labViewFrame.liteClientPanels.get(ipAddress));
        labViewFrame.liteClientPanels.remove(ipAddress);

        labViewFrame.clientDescriptionLabels.remove(ipAddress);
        labViewFrame.applicationStateLabels.remove(ipAddress);

        labViewFrame.clientStartButtons.remove(ipAddress);
        labViewFrame.clientStopButtons.remove(ipAddress);

        String[] hostNames = labViewFrame.clientPool.getHostNames(LiteClient.SORT_BY_HOSTNAME).toArray(new String[labViewFrame.clientPool.size()]);

        labViewFrame.clientPanel.setBorder(new TitledBorder("Computers Connected: " + hostNames.length));

        labViewFrame.clientsLowerBoundComboBox = new JComboBox<>(hostNames);
        labViewFrame.clientsUpperBoundComboBox = new JComboBox<>(hostNames);

        labViewFrame.setApplicationRangePanel(labViewFrame.clientsLowerBoundComboBox, labViewFrame.clientsUpperBoundComboBox);

        labViewFrame.connectedComputersComboBox = new JComboBox<>(hostNames);
        labViewFrame.setMessagePanel(labViewFrame.connectedComputersComboBox);

        labViewFrame.clientsMessageLowerBound = new JComboBox<>(hostNames);
        labViewFrame.clientsMessageUpperBound = new JComboBox<>(hostNames);
        labViewFrame.setMessageRangePanel(labViewFrame.clientsMessageLowerBound, labViewFrame.clientsMessageUpperBound);

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

        labViewFrame.repaint();

    }

}
