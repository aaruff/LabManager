package edu.nyu.cess.remote.server.gui.listeners;

import edu.nyu.cess.remote.server.client.LiteClient;
import edu.nyu.cess.remote.server.client.LiteClientNotFoundException;
import edu.nyu.cess.remote.server.gui.LabViewFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by aruff on 2/9/16.
 */
class MessageClientsInRangeListener implements ActionListener {

    private LabViewFrame labViewFrame;

    public MessageClientsInRangeListener(LabViewFrame labViewFrame) {
        this.labViewFrame = labViewFrame;
    }

    public void actionPerformed(ActionEvent e) {
        String message = labViewFrame.messageTextField.getText();
        labViewFrame.messageTextField.setText("");

        if (message.isEmpty()) {
            labViewFrame.messageJLabel.setText("Messaging Error: No text entered");
            return;
        }

        if (labViewFrame.singleRadioButton.isSelected()) {
            String selectedHost = (String) labViewFrame.connectedComputersComboBox.getSelectedItem();

            if (selectedHost.isEmpty()) {
                labViewFrame.messageJLabel.setText("Messaging Error: A computer was not selected.");
                return;
            }

            try {
                LiteClient client = labViewFrame.clientPool.getByHostname(selectedHost);
                labViewFrame.executionRequestObserver.messageClient(message, client.getIPAddress());
            } catch (LiteClientNotFoundException liteClientException) {
                LabViewFrame.log.error("Requested client not found", liteClientException);
            }
        } else {
            labViewFrame.executionRequestObserver.messageClientInRange(message,
                    (String) labViewFrame.clientsMessageLowerBound.getSelectedItem(), (String) labViewFrame.clientsMessageUpperBound.getSelectedItem());
        }
    }
}
