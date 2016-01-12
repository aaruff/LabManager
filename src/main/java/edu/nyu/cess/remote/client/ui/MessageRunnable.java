package edu.nyu.cess.remote.client.ui;

import javax.swing.*;

/**
 * The message runnable class used to display messages sent from the server.
 */
public class MessageRunnable implements Runnable {
    String message;

    public MessageRunnable(String message) {
        this.message = message;
    }

    public void run() {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message, "Experimenter Notification", JOptionPane.WARNING_MESSAGE);
    }

}
