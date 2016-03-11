package edu.nyu.cess.remote.server.io;

import edu.nyu.cess.remote.server.lab.LabLayout;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class LabLayoutFileTest
{
	@Test
	public void whenGivenValidFilePropertiesSet() throws Exception
	{
		LabLayout labLayout = null;
		try (InputStream inputStream = getClass().getResourceAsStream("/lab-layout.yaml")) {
			labLayout = LabLayoutFile.readFile(inputStream);
		}
		catch(FileNotFoundException e) {
		}

		assertNotNull(labLayout);
	}

}
