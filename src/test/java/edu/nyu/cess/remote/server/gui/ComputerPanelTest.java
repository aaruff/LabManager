package edu.nyu.cess.remote.server.gui;

import edu.nyu.cess.remote.server.gui.observers.StartStopButtonObserver;
import edu.nyu.cess.remote.common.net.ConnectionState;

import javax.swing.*;
import java.util.Scanner;

import static org.mockito.Mockito.mock;
/**
 * Created by aruff on 2/25/16.
 */
public class ComputerPanelTest
{

	public void displayComputerPanelWithState(ConnectionState connectionState)
	{
		StartStopButtonObserver startStopButtonObserver = mock(StartStopButtonObserver.class);
		ComputerPanel computerPanel = new ComputerPanel("name", "1.1.1.1", connectionState, startStopButtonObserver);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(computerPanel);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		ComputerPanelTest computerPanelTest = new ComputerPanelTest();

		System.out.println("Test Type: (c = connected, d = disconnected)");
		Scanner scanner = new Scanner(System.in);
		String testType = scanner.next().trim();

		switch(testType) {
			case "c":
				computerPanelTest.displayComputerPanelWithState(ConnectionState.CONNECTED);
				break;
			case "d":
				computerPanelTest.displayComputerPanelWithState(ConnectionState.DISCONNECTED);
		}
	}
}
