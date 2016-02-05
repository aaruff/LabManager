package edu.nyu.cess.remote.client.validator;

import java.util.ArrayList;

/**
 * Validates Port Numbers
 */
public class PortValidator implements Validator
{
	private Integer port;
	private ArrayList<String> errors;

	public PortValidator(){}

	/**
	 * Initializes the port.
	 *
	 * @param port
     */
	public PortValidator(Integer port)
	{
		this.port = port;
	}

	/**
	 * Validates the port.
	 *
	 * @return boolean
     */
	public boolean validate()
	{
		errors = new ArrayList<>();

		if (port == null) {
			errors.add("IP address is null.");
			return false;
		}

		try {
			if (port < 1024 || port > 49151) {
				errors.add("Port number is not between 1024-49151.");
				return false;
			}
		}
		catch(NumberFormatException n) {
			errors.add("Port format is non-numeric.");
			return false;
		}

		return true;
	}

	/**
	 * Sets the port.
	 *
	 * @param port
     */
	public void setPort(Integer port)
	{
		this.port = port;
	}

	@Override
	public ArrayList<String> getErrors()
	{
		return errors;
	}
}
