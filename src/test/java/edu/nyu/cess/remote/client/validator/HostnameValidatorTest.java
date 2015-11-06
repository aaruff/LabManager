package edu.nyu.cess.remote.client.validator;

import org.junit.Test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aruff on 11/6/15.
 */
public class HostnameValidatorTest
{
	@Test
	public void When_TestInvalid_Should_ReturnFalseWithError() throws Exception
	{
		HostNameValidator validator = new HostNameValidator();

		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setHostName("");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);
	}
}
