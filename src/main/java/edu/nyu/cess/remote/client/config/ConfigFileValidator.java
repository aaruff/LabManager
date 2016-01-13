package edu.nyu.cess.remote.client.config;

import edu.nyu.cess.remote.client.validator.HostNameValidator;
import edu.nyu.cess.remote.client.validator.IpValidator;
import edu.nyu.cess.remote.client.validator.PortValidator;
import edu.nyu.cess.remote.client.validator.Validator;
import edu.nyu.cess.remote.common.net.ClientServerNetworkInfo;


/**
 * Validates the config file properties.
 */
public class ConfigFileValidator extends Validator
{
	private ClientServerNetworkInfo hostConfig;

	public ConfigFileValidator(ClientServerNetworkInfo hostConfig)
	{
		this.hostConfig = hostConfig;
	}

	public boolean validate()
	{
		clearErrors();

		IpValidator ipValidator = new IpValidator(hostConfig.getServerIpAddress());
		if ( ! ipValidator.validate()) {
			errors.addAll(ipValidator.getErrors());
			return false;
		}

		PortValidator portValidator = new PortValidator(hostConfig.getServerPort());
		if ( ! portValidator.validate()) {
			errors.addAll(portValidator.getErrors());
			return false;
		}


		HostNameValidator hostNameValidator = new HostNameValidator(hostConfig.getClientName());
		if ( ! hostNameValidator.validate()) {
			errors.addAll(hostNameValidator.getErrors());
			return false;
		}

		return true;
	}
}
