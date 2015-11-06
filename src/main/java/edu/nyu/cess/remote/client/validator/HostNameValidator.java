package edu.nyu.cess.remote.client.validator;

import org.apache.commons.lang.StringUtils;

/**
 * The HostName Validator.
 */
public class HostNameValidator extends Validator
{
	private String hostName;

	public HostNameValidator(){}

	public HostNameValidator(String hostName)
	{
		this.hostName = hostName;
	}

	@Override
	public boolean validate()
	{
		clearErrors();

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
