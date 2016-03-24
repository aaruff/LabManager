package edu.nyu.cess.remote.server.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by aruff on 3/9/16.
 */
public class ImageCollection
{
	private final static Logger logger = LoggerFactory.getLogger(ConfigFileLoader.class);

	private HashMap<String, ImageIcon> imageIcons = new HashMap<>();

	public static final String NETWORK_ICON = "NetworkIcon";
	private static final String NETWORK_ICON_FILE = "/production/server/images/client-network.png";

	public ImageCollection()
	{
		URL imageURL = getClass().getResource(NETWORK_ICON_FILE);
		if (imageURL != null) {
			imageIcons.put(NETWORK_ICON,new ImageIcon(new ImageIcon(imageURL).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
		}
		else {
			imageIcons.put(NETWORK_ICON, new ImageIcon());
			logger.error("Failed to load image {}.", NETWORK_ICON_FILE);
		}
	}

	public ImageIcon getNetworkImage()
	{
		return imageIcons.get(NETWORK_ICON);
	}
}
