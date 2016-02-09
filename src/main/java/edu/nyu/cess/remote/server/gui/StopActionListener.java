package edu.nyu.cess.remote.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StopActionListener implements ActionListener
{
    private LabViewFrame labViewFrame;
    String ipAddress;

    public StopActionListener(LabViewFrame labViewFrame, String ipAddress) {
        this.labViewFrame = labViewFrame;
        this.ipAddress = ipAddress;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(ipAddress + " Stop button selected");
        labViewFrame.executionRequestObserver.stopApplication(ipAddress);
    }
}
