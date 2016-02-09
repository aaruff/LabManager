package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.client.LiteClient;

import java.awt.*;

/**
 * Created by aruff on 2/9/16.
 */
class UpdateClient implements Runnable {
    private LabViewFrame labViewFrame;
    LiteClient liteClient;

    public UpdateClient(LabViewFrame labViewFrame, LiteClient liteClient) {
        this.labViewFrame = labViewFrame;
        this.liteClient = liteClient;
    }

    public void run() {
        if (liteClient.isApplicationRunning()) {
            labViewFrame.applicationStateLabels.get(liteClient.getIPAddress()).setText(
                    "Running: " + liteClient.getApplicationName());
            (labViewFrame.applicationStateLabels.get(liteClient.getIPAddress())).setForeground(new Color(73, 143, 0));
            labViewFrame.clientStartButtons.get(liteClient.getIPAddress()).setEnabled(false);
            labViewFrame.clientStopButtons.get(liteClient.getIPAddress()).setEnabled(true);
            labViewFrame.clientStopButtons.get(liteClient.getIPAddress()).setToolTipText("Stops running application.");
            if (!liteClient.getHostName().isEmpty()) {
                labViewFrame.clientDescriptionLabels.get(liteClient.getIPAddress()).setText("" + liteClient.getHostName());
            }
            labViewFrame.contentPane.validate();
            labViewFrame.repaint();
        } else {
            labViewFrame.applicationStateLabels.get(liteClient.getIPAddress()).setText("Stopped");
            labViewFrame.applicationStateLabels.get(liteClient.getIPAddress()).setForeground(Color.red);
            labViewFrame.clientStartButtons.get(liteClient.getIPAddress()).setEnabled(true);
            labViewFrame.clientStopButtons.get(liteClient.getIPAddress()).setEnabled(false);
            if (!liteClient.getHostName().isEmpty()) {
                labViewFrame.clientDescriptionLabels.get(liteClient.getIPAddress()).setText("" + liteClient.getHostName());
            }
            labViewFrame.contentPane.validate();
            labViewFrame.repaint();
        }
    }
}
