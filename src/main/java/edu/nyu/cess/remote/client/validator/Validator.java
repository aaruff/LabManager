package edu.nyu.cess.remote.client.validator;

import java.util.ArrayList;

public interface Validator
{
	boolean validate();
	ArrayList<String> getErrors();
}
