package edu.nyu.cess.remote.client.config;

import edu.nyu.cess.remote.client.validator.HostNameValidator;
import edu.nyu.cess.remote.client.validator.IpValidator;
import edu.nyu.cess.remote.client.validator.PortValidator;
import edu.nyu.cess.remote.client.validator.Validator;
import edu.nyu.cess.remote.common.net.NetworkInformation;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;


/**
 * Validates the config file properties.
 */
public class NetworkInformationFileValidator implements Validator
{
	private NetworkInformation networkInformation;
	private ArrayList<String> errors;

	public NetworkInformationFileValidator(NetworkInformation networkInformation)
	{
		this.networkInformation = networkInformation;
	}

	@Override public boolean validate()
	{
		clearErrors();

		IpValidator ipValidator = new IpValidator(networkInformation.getServerIpAddress());
		if ( ! ipValidator.validate()) {
			errors.addAll(ipValidator.getErrors());
			return false;
		}

		PortValidator portValidator = new PortValidator(networkInformation.getServerPort());
		if ( ! portValidator.validate()) {
			errors.addAll(portValidator.getErrors());
			return false;
		}


		HostNameValidator hostNameValidator = new HostNameValidator(networkInformation.getClientName());
		if ( ! hostNameValidator.validate()) {
			errors.addAll(hostNameValidator.getErrors());
			return false;
		}

		return true;
	}

	@Override public ArrayList<String> getErrors()
	{
		return errors;
	}

	/**
	 * Clear errors.
	 */
	private void clearErrors()
	{
		errors = new ArrayList<>();
	}

	public String getAllErrors()
	{
		return StringUtils.join(getErrors(), ", ");
	}

}
