package edu.nyu.cess.remote.server.gui.panels;

import edu.nyu.cess.remote.common.app.AppExe;
import edu.nyu.cess.remote.server.gui.ComputerPanel;
import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;
import edu.nyu.cess.remote.server.lab.Computer;
import edu.nyu.cess.remote.server.lab.LabLayout;
import edu.nyu.cess.remote.server.lab.Row;
import edu.nyu.cess.remote.common.net.ConnectionState;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aruff on 2/25/16.
 */
public class ComputerLayoutPanel extends JPanel
{
    private final String COMPUTERS_CONNECTED = "Computers Connected: ";

	private final Map<String, ComputerPanel> computerPanels = new HashMap<>();
    private final JLabel computersConnectedLabel;

    private int connectedComputers = 0;

	public ComputerLayoutPanel(LabLayout labLayout, StartStopButtonObserver startStopButtonObserver)
	{
		super(new MigLayout());

        setBackground(Color.white);

        computersConnectedLabel = new JLabel(COMPUTERS_CONNECTED + connectedComputers);
        computersConnectedLabel.setFont(new Font("arial", Font.PLAIN, 14));
        JPanel clientsConnectedPanel = new JPanel(new MigLayout());
        clientsConnectedPanel.setBackground(Color.white);
        clientsConnectedPanel.add(computersConnectedLabel);
        add(clientsConnectedPanel, "dock north");

		for (Row row : labLayout.getRows()) {
			int counter = 1;
			for (Computer computer : row.getComputers()) {
				String computerName = computer.getName();
				String computerIp = computer.getIp();
				ComputerPanel computerPanel = new ComputerPanel(computerName, computerIp, ConnectionState.DISCONNECTED, startStopButtonObserver);
				computerPanels.put(computerIp, computerPanel);
				add(computerPanel, (counter == row.getComputers().size()) ? "wrap" : "");
				++counter;
			}
		}
	}

	public void updateComputerConnectionState(String computerIp, ConnectionState connectionState)
	{
        ComputerPanel computerPanel = computerPanels.get(computerIp);
        switch(connectionState) {
            case CONNECTED:
                if (computerPanel.isConnectionState(ConnectionState.DISCONNECTED)) {
                    computerPanel.updateState(ConnectionState.CONNECTED);
                    ++connectedComputers;
                }
                break;
            case DISCONNECTED:
                if (computerPanel.isConnectionState(ConnectionState.CONNECTED)) {
                    computerPanel.updateState(ConnectionState.DISCONNECTED);
                    --connectedComputers;
                }
                break;
        }

        computersConnectedLabel.setText(COMPUTERS_CONNECTED + connectedComputers);
	}

	public void updateAppExeState(String computerIp, AppExe appExe)
	{
        ComputerPanel computerPanel = computerPanels.get(computerIp);
        computerPanel.updateAppExe(appExe);

	}
}
