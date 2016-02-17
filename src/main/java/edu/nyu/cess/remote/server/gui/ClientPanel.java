package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.gui.listeners.StartStopButtonListener;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

/**
 * Created by aruff on 2/15/16.
 */
public class ClientPanel extends JPanel implements Comparable<ClientPanel>
{
    public static final SortByHostname SORT_BY_HOSTNAME = new SortByHostname();
    public static final SortByIp SORT_BY_IP = new SortByIp();

    private final String name;
    private final String ipAddress;

	private final JLabel hostNameLabel;
	private final JLabel appExeStateLabel;

    public ClientPanel(String name, String ipAddress, StartStopButtonObserver startStopButtonObserver)
    {
		super();
        this.name = name;
        this.ipAddress = ipAddress;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		hostNameLabel = new JLabel("" + name);
		hostNameLabel.setForeground(new Color(0, 0, 128));
		JPanel clientDescriptionPanel = new JPanel(new FlowLayout());
		clientDescriptionPanel.setOpaque(false);
		clientDescriptionPanel.add(hostNameLabel);
		add(clientDescriptionPanel);

		JPanel applicationStatePanel = new JPanel(new FlowLayout());
		applicationStatePanel.setOpaque(false);
		appExeStateLabel = new JLabel("Stopped");
		appExeStateLabel.setForeground(Color.red);
		applicationStatePanel.add(appExeStateLabel);
		add(applicationStatePanel);

		String startButtonName = "Start";
		String stopButtonName = "Stop";

		JButton startButton = new JButton(startButtonName);
		JButton stopButton = new JButton(stopButtonName);

		startButton.addActionListener(new StartStopButtonListener(startStopButtonObserver, ipAddress, startButtonName, stopButtonName));
		stopButton.addActionListener(new StartStopButtonListener(startStopButtonObserver, ipAddress, startButtonName, stopButtonName));
		startButton.setToolTipText("Starts selected application on " + ipAddress + ".");
		startButton.setToolTipText("Starts selected application on " + ipAddress + ".");
		stopButton.setEnabled(false);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setOpaque(false);
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		add(buttonPanel);
    }

    public String getName()
    {
        return name;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }


    /**
     * Compares this client's hostname to the client parameter, based upon the lexical ordering of the hostname.
     * Note: A client having a null name, is considered less than one that doesn't. Two null clients are considered
     * equal.
     *
     * @param client the client being compared to.
     * @return the comparison result: 0 if equal, -1 if less, or 1 if greater
     */
    @Override public int compareTo(ClientPanel client)
    {
        return name.compareTo(client.getName());
    }

    public static class SortByHostname implements Comparator<ClientPanel>
    {
        /**
         * Compares one client to another by their host name.
         * @param c1 client one
         * @param c2 client two
         * @return returns 1 if greater, -1 if less, 0 if equal
         */
        @Override public int compare(ClientPanel c1, ClientPanel c2)
        {
            if (c1.getName() == null || c2.getName() == null) {
                NullComparator.compareNullString(c1.getName(), c2.getName());
            }

            return c1.getName().compareTo(c2.getName());
        }
    }

    /**
     * Provides the sort by IP address method.
     */
    public static class SortByIp implements Comparator<ClientPanel>
    {
        /**
         * Compares one client to another by their IP address.
         * @param c1 client one
         * @param c2 client two
         * @return returns 1 if greater, -1 if less, 0 if equal
         */
        @Override public int compare(ClientPanel c1, ClientPanel c2)
        {
            if (c1.getIpAddress() == null || c2.getIpAddress() == null) {
                NullComparator.compareNullString(c1.getIpAddress(), c2.getIpAddress());
            }

            return c1.getIpAddress().compareTo(c2.getIpAddress());
        }
    }
}
