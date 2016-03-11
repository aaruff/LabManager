package edu.nyu.cess.remote.client.validator;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * The HostName Validator.
 */
public class HostNameValidator implements Validator
{
	private String hostName;
	private ArrayList<String> errors;

	public HostNameValidator(){}

	public HostNameValidator(String hostName)
	{
		this.hostName = hostName;
	}

	@Override
	public boolean validate()
	{
		errors = new ArrayList<>();

		if (hostName == null) {
			errors.add("Host name is null.");
			return false;
		}

		if (StringUtils.isEmpty(hostName)) {
			errors.add("No host name provided.");
			return false;
		}

		return true;
	}

	@Override
	public ArrayList<String> getErrors()
	{
		return errors;
	}

	/**
	 * Sets hostname.
	 *
	 * @param hostName
     */
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
}
