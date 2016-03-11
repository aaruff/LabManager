package edu.nyu.cess.remote.client.validator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * IpValidator Unit Tests
 */
public class IpValidatorTest
{
	@Test
	public void When_TestInvalid_Should_ReturnFalseWithError() throws Exception
	{
		IpValidator validator = new IpValidator();

		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress(null);
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("1");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("1.1");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("1.1.1");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("1.1.1");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("1.1.1 .1");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);

		validator.setIpAddress("aaa.aa.a.a");
		assertFalse(validator.validate());
		assertTrue(validator.getErrors().size() > 0);
	}

	@Test
	public void When_TestValid_Should_ReturnTrueWithNoError() throws Exception
	{
		IpValidator ipValidator = new IpValidator();

		ipValidator.setIpAddress("1.1.1.1");
		assertTrue(ipValidator.validate());
		assertEquals(0, ipValidator.getErrors().size());

		ipValidator.setIpAddress("192.168.1.1");
		assertTrue(ipValidator.validate());
		assertEquals(0, ipValidator.getErrors().size());
	}
}
