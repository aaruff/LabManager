package edu.nyu.cess.remote.server.app;

import edu.nyu.cess.remote.common.app.AppInfo;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aruff on 2/16/16.
 */
public class AppInfoValidatorTest
{

	@Test
	public void When_InvalidAppInfoIsValidated_FalseReturned() throws Exception
	{
		assertFalse(AppInfoValidator.validate(null));

		AppInfo appInfo = new AppInfo();
		assertFalse(AppInfoValidator.validate(appInfo));

		appInfo.setName("");
		appInfo.setPath("");
		appInfo.setOptions("");
		assertFalse(AppInfoValidator.validate(appInfo));

		appInfo.setName("");
		appInfo.setPath("C:\\foo\\bar");
		appInfo.setOptions("-a -b -c");
		assertFalse(AppInfoValidator.validate(appInfo));

		appInfo.setName("foo");
		appInfo.setPath("");
		appInfo.setOptions("");
		assertFalse(AppInfoValidator.validate(appInfo));

		appInfo.setName("foo");
		appInfo.setPath("C:\\foo\\bar");
		appInfo.setOptions(null);
		assertFalse(AppInfoValidator.validate(appInfo));
	}

	@Test
	public void When_InvalidAppInfoCollectionIsValidated_FalseReturned() throws Exception
	{
		HashMap<String, AppInfo> appInfoMap = new HashMap<>();
		appInfoMap.put("", new AppInfo());
		appInfoMap.put("", null);
		assertFalse(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));

		appInfoMap = new HashMap<>();
		appInfoMap.put("doo", new AppInfo());
		appInfoMap.get("doo").setName("correct");
		appInfoMap.get("doo").setPath("C:\\foo\\bar");
		appInfoMap.get("doo").setOptions("-f -b");

		appInfoMap.put("foo", new AppInfo());
		appInfoMap.get("foo").setName("");
		appInfoMap.get("foo").setPath("");
		appInfoMap.get("foo").setOptions("");
		assertFalse(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));

		appInfoMap = new HashMap<>();
		appInfoMap.put("doo", new AppInfo());
		appInfoMap.get("doo").setName("correct");
		appInfoMap.get("doo").setPath("C:\\foo\\bar");
		appInfoMap.get("doo").setOptions("-f -b");

		appInfoMap.put("foo", new AppInfo());
		appInfoMap.get("foo").setName("foo");
		appInfoMap.get("foo").setPath("");
		appInfoMap.get("foo").setOptions(null);
		assertFalse(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));

		appInfoMap = new HashMap<>();
		appInfoMap.put("doo", new AppInfo());
		appInfoMap.get("doo").setName("correct");
		appInfoMap.get("doo").setPath("C:\\foo\\bar");
		appInfoMap.get("doo").setOptions("-f -b");

		appInfoMap.put("foo", new AppInfo());
		appInfoMap.get("foo").setName("foo");
		appInfoMap.get("foo").setPath("");
		appInfoMap.get("foo").setOptions("");
		assertFalse(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));
	}

	@Test
	public void When_ValidAppInfoCollectionIsValidated_TrueReturned() throws Exception
	{
		HashMap<String, AppInfo> appInfoMap = new HashMap<>();
		appInfoMap.put("doo", new AppInfo());
		appInfoMap.get("doo").setName("correct");
		appInfoMap.get("doo").setPath("C:\\foo\\bar");
		appInfoMap.get("doo").setOptions("-f -b");

		assertTrue(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));

		appInfoMap = new HashMap<>();
		appInfoMap.put("doo", new AppInfo());
		appInfoMap.get("doo").setName("correct");
		appInfoMap.get("doo").setPath("C:\\foo\\bar");
		appInfoMap.get("doo").setOptions("-f -b");

		appInfoMap.put("foo", new AppInfo());
		appInfoMap.get("foo").setName("foo");
		appInfoMap.get("foo").setPath("/var/lib/foo");
		appInfoMap.get("foo").setOptions("");
		assertTrue(AppInfoValidator.validateCollection(new AppInfoCollection(appInfoMap)));
	}

	@Test
	public void When_ValidAppInfoIsInvalidated_TrueReturned() throws Exception
	{
		AppInfo appInfo = new AppInfo();

		appInfo.setName("foo");
		appInfo.setPath("C:\\foo\\bar");
		appInfo.setOptions("-a -b -c");
		assertTrue(AppInfoValidator.validate(appInfo));

		appInfo.setName("foo");
		appInfo.setPath("C:\\foo\\bar");
		appInfo.setOptions("");
		assertTrue(AppInfoValidator.validate(appInfo));
	}

}
