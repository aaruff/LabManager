package edu.nyu.cess.remote.client.validator;

import java.util.ArrayList;

/**
 * Created by aruff on 11/5/15.
 */
public abstract class Validator
{
	protected ArrayList<String> errors;

	public abstract boolean validate();

	/**
	 * Clear errors.
	 */
	protected void clearErrors()
	{
		errors = new ArrayList<>();
	}

	/**
	 * Return errors
	 *
	 * @return String
	 */
	public ArrayList<String> getErrors()
	{
		return errors;
	}
}
