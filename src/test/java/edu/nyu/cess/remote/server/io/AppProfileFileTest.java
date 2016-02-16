package edu.nyu.cess.remote.server.io;

import edu.nyu.cess.remote.common.app.AppInfo;
import edu.nyu.cess.remote.server.app.AppInfoCollection;
import org.junit.Test;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppProfileFileTest
{
	@Test
	public void When_GivenValidListFile_Should_ReturnRemoteExecProfileList() throws Exception
	{

		AppInfoCollection appProfileList = null;
		try (InputStream inputStream = getClass().getResourceAsStream("/test-config.yaml")) {
			appProfileList = AppProfilesFile.readFile(inputStream);
		}
		catch(FileNotFoundException e) {
		}

		AppInfo appOne = appProfileList.getAppInfo("App One");

		assertNotNull(appOne);

		assertNotNull(appOne.getName());
		assertNotNull(appOne.getPath());
		assertNotNull(appOne.getOptions());

		assertTrue("app name does not equal \"App One\"", appOne.getName().equals("App One"));
		assertTrue("path does not equal C:\\\\foo\\bar\\one.exe", appOne.getPath().equals("C:\\\\foo\\bar\\one.exe"));
		assertNotNull("option does not equal \"--b\"", appOne.getOptions().equals("--a"));

		AppInfo appTwo = appProfileList.getAppInfo("App Two");

		assertNotNull(appTwo.getName());
		assertNotNull(appTwo.getPath());
		assertNotNull(appTwo.getOptions());

		assertTrue("app name does not equal \"App Two\"", appTwo.getName().equals("App Two"));
		assertTrue("path does not equal C:\\\\foo\\bar\\one.exe", appTwo.getPath().equals("C:\\\\foo\\bar\\two.exe"));
		assertNotNull("option does not equal \"--b\"", appTwo.getOptions().equals("--b"));
	}

	@Test
	public void When_GivenListWithMissingField_Should_SetFieldToNull() throws Exception
	{
		InputStream inputStream = getClass().getResourceAsStream("/missing-field.yaml");
		AppInfoCollection appProfiles = AppProfilesFile.readFile(inputStream);

		assertNull(appProfiles.getAppInfo(""));
	}

	@Test(expected=YAMLException.class)
	public void When_InvalidFileProvided_Should_ThrowYAMLException() throws Exception
	{
		InputStream inputStream = getClass().getResourceAsStream("/non-existent.yaml");
		AppInfoCollection appProfiles = AppProfilesFile.readFile(inputStream);
	}

	@Test(expected=YAMLException.class)
	public void When_InvalidExecFieldNameUsed_Should_ThrowYAMLException() throws Exception
	{
		InputStream inputStream = getClass().getResourceAsStream("/bad-config.yaml");
		AppInfoCollection appProfiles = AppProfilesFile.readFile(inputStream);
	}

}
