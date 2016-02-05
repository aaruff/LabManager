package edu.nyu.cess.remote.client.config;

import edu.nyu.cess.remote.common.net.PortInfo;
import edu.nyu.cess.remote.client.validator.HostNameValidator;
import edu.nyu.cess.remote.client.validator.IpValidator;
import edu.nyu.cess.remote.client.validator.PortValidator;
import edu.nyu.cess.remote.client.validator.Validator;
import edu.nyu.cess.remote.common.net.NetworkInfo;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;


/**
 * Validates the config file properties.
 */
public class NetInfoFileValidator implements Validator
{
	private NetInfoFile netInfoFile;
	private ArrayList<String> errors;

	public NetInfoFileValidator(NetInfoFile netInfoFile)
	{
		this.netInfoFile = netInfoFile;
	}

	@Override public boolean validate()
	{
		clearErrors();
		NetworkInfo netInfo = netInfoFile.getNetworkInfo();
		PortInfo portInfo = netInfoFile.getPortInfo();

		IpValidator ipValidator = new IpValidator(netInfo.getServerIpAddress());
		if ( ! ipValidator.validate()) {
			errors.addAll(ipValidator.getErrors());
			return false;
		}

		PortValidator portValidator = new PortValidator(portInfo.getNumber());
		if ( ! portValidator.validate()) {
			errors.addAll(portValidator.getErrors());
			return false;
		}


		HostNameValidator hostNameValidator = new HostNameValidator(netInfo.getClientName());
		if ( ! hostNameValidator.validate()) {
			errors.addAll(hostNameValidator.getErrors());
			return false;
		}

		return true;
	}

	public void setNetworkInfoFile(NetInfoFile netInfoFile)
	{
		this.netInfoFile = netInfoFile;
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
