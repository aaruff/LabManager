package edu.nyu.cess.remote.client.validator;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.ArrayList;

/**
 * IP Address Validator
 */
public class IpValidator implements Validator
{
	private String ip;
	private ArrayList<String> errors;

	public IpValidator() {}

	/**
	 * Sets the ip instance variable.
	 * @param ip
     */
	public IpValidator(String ip)
	{
		this.ip = ip;
	}


	/**
	 * Validates the ip instance variable.
	 *
	 * @return boolean
	 */
	public boolean validate()
	{
		errors = new ArrayList<>();

		if (ip == null) {
			errors.add("IP address is null.");
			return false;
		}

		InetAddressValidator validator = InetAddressValidator.getInstance();
		if ( ! validator.isValid(ip)) {
			errors.add("Invalidly formatted IP Address.");
			return false;
		}
		return true;
	}

	/**
	 * Sets the IP address string for validation.
	 * @param ip
	 */
	public void setIpAddress(String ip)
	{
		this.ip = ip;
	}

	/**
	 * {@link Validator}
	 * @return list of errors
     */
	public ArrayList<String> getErrors()
	{
		return errors;
	}
}
