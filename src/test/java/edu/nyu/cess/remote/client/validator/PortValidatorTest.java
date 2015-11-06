package edu.nyu.cess.remote.client.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aruff on 11/6/15.
 */
public class PortValidatorTest
{

	@Test
	public void When_TestInvalid_Should_ReturnFalseWithError() throws Exception
	{
		PortValidator portValidator = new PortValidator();

		assertFalse(portValidator.validate());
		assertTrue(portValidator.getErrors().size() > 0);

		portValidator.setPort("");
		assertFalse(portValidator.validate());
		assertTrue(portValidator.getErrors().size() > 0);

		portValidator.setPort("ab");
		assertFalse(portValidator.validate());
		assertTrue(portValidator.getErrors().size() > 0);

		// Rejects values < 1024
		portValidator.setPort("1023");
		assertFalse(portValidator.validate());
		assertTrue(portValidator.getErrors().size() > 0);

		// Rejects values > 49151
		portValidator.setPort("49152");
		assertFalse(portValidator.validate());
		assertTrue(portValidator.getErrors().size() > 0);
	}

	@Test
	public void When_TestValid_Should_ReturnTrueWithNoError() throws Exception
	{
		PortValidator portValidator = new PortValidator();

		portValidator.setPort("1204");
		assertTrue(portValidator.validate());
		assertEquals(0, portValidator.getErrors().size());

		portValidator.setPort("49151");
		assertTrue(portValidator.validate());
		assertEquals(0, portValidator.getErrors().size());
	}
}
