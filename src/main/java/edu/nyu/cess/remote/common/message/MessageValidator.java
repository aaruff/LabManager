package edu.nyu.cess.remote.common.message;

/**
 * Message class validator.
 */
public class MessageValidator
{
	private String errorMessage;

	public boolean validate(Message message)
	{
		errorMessage = "";

		if (message == null) {
			errorMessage = "Error: Message null";
			return false;
		}

		if (message.getMessageType() == null) {
			errorMessage = "Error: Message type null";
			return false;
		}

		if (message.getNetworkInfo() == null) {
			errorMessage = "Error: Network info null";
			return false;
		}

		return true;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
