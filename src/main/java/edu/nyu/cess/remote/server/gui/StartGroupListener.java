package edu.nyu.cess.remote.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by aruff on 2/9/16.
 */
class StartGroupListener implements ActionListener {

    private LabViewFrame labViewFrame;

    public StartGroupListener(LabViewFrame labViewFrame) {
        this.labViewFrame = labViewFrame;
    }

    public void actionPerformed(ActionEvent e) {
        String applicationSelected = (String) labViewFrame.clientApplicationsComboBox.getSelectedItem();
        String clientLowerBound = (String) labViewFrame.clientsLowerBoundComboBox.getSelectedItem();
        String clientUpperBound = (String) labViewFrame.clientsUpperBoundComboBox.getSelectedItem();

        labViewFrame.executionRequestObserver.notifyViewObserverStartAppInRangeRequested(applicationSelected, clientLowerBound, clientUpperBound);

    }
}
