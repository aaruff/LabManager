package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.client.LiteClient;
import edu.nyu.cess.remote.server.client.LiteClientNotFoundException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by aruff on 2/9/16.
 */
class StartActionListener implements ActionListener
{
    private LabViewFrame labViewFrame;
    String ipAddress;

    public StartActionListener(LabViewFrame labViewFrame, String ipAddress) {
        this.labViewFrame = labViewFrame;
        this.ipAddress = ipAddress;
    }

    public void actionPerformed(ActionEvent e) {
        String applicationSelected = (String) labViewFrame.clientApplicationsComboBox.getSelectedItem();

        System.out.println(ipAddress + " Start button selected");

        try {
            LiteClient liteClient = labViewFrame.clientPool.getByIp(ipAddress);
            liteClient.setApplicationName(applicationSelected);

            labViewFrame.executionRequestObserver.startApplication(applicationSelected, ipAddress);
        } catch (LiteClientNotFoundException liteClientException) {
            LabViewFrame.log.error("Requested client not found", liteClientException);
        }
    }

}
