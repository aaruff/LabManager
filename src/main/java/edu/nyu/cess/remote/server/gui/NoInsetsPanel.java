package edu.nyu.cess.remote.server.gui;

import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class NoInsetsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public NoInsetsPanel(LayoutManager layout) {
		super(layout);
	}

	@Override
	public Insets getInsets() {
		return new Insets(5, 2, 5, 2);
	}
}
