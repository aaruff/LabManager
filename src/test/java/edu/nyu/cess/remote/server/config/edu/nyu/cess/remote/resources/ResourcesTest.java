package edu.nyu.cess.remote.server.config.edu.nyu.cess.remote.resources;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class ResourcesTest
{
	@Test
	public void testResourceAvailability()
	{
        InputStream inputStream = getClass().getResourceAsStream("/test-config.yaml");
		assertNotNull(inputStream);
	}
}
