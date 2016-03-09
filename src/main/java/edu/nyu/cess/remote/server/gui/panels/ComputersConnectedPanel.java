package edu.nyu.cess.remote.server.gui.panels;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by aruff on 3/9/16.
 */
public class ComputersConnectedPanel extends JPanel
{
	private final JLabel numComputersConnectedLabel = new JLabel();

	private int connectedCount = 0;

	public ComputersConnectedPanel()
	{
		super(new MigLayout("", "[center][center]", "[center]"));
		setBackground(Color.white);

		JLabel connectedLabel = new JLabel("Computers Connected: ");
		connectedLabel.setFont(new Font("arial", Font.PLAIN, 14));
		add(connectedLabel);

		numComputersConnectedLabel.setFont(new Font("arial", Font.PLAIN, 14));
		numComputersConnectedLabel.setText(String.valueOf(connectedCount));
		add(numComputersConnectedLabel);
	}

	public void incrementComputersConnected()
	{
		++connectedCount;
		this.numComputersConnectedLabel.setText(String.valueOf(connectedCount));
	}

	public void decrementComputersConnected()
	{
		--connectedCount;
		this.numComputersConnectedLabel.setText(String.valueOf(connectedCount));
	}
}
