package edu.nyu.cess.remote.client.config;

import edu.nyu.cess.remote.client.validator.HostNameValidator;
import edu.nyu.cess.remote.client.validator.IpValidator;
import edu.nyu.cess.remote.client.validator.PortValidator;
import edu.nyu.cess.remote.client.validator.Validator;


/**
 * Validates the config file properties.
 */
public class ConfigFileValidator extends Validator
{
	private HostConfigInterface hostConfig;

	public ConfigFileValidator(HostConfigInterface hostConfig)
	{
		this.hostConfig = hostConfig;
	}

	public boolean validate()
	{
		clearErrors();

		IpValidator ipValidator = new IpValidator(hostConfig.getIpAddress());
		if ( ! ipValidator.validate()) {
			errors.addAll(ipValidator.getErrors());
			return false;
		}

		PortValidator portValidator = new PortValidator(hostConfig.getPort());
		if ( ! portValidator.validate()) {
			errors.addAll(portValidator.getErrors());
			return false;
		}


		HostNameValidator hostNameValidator = new HostNameValidator(hostConfig.getHostName());
		if ( ! hostNameValidator.validate()) {
			errors.addAll(hostNameValidator.getErrors());
			return false;
		}

		return true;
	}
}
