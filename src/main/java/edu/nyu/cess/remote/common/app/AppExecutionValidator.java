package edu.nyu.cess.remote.common.app;

public class AppExecutionValidator
{
    public static boolean validate(AppExe exe)
    {
		if (exe == null) {
			return false;
		}

		AppInfo info = exe.getAppInfo();
        return ! (info == null || isEmpty(info.getName()) || isEmpty(info.getPath()) || info.getArgs() == null || exe.getState() == null);
    }

	public static String getValidationError(AppExe exe)
	{
		if (exe == null) {
			return "App Execution Null";
		}

		AppInfo info = exe.getAppInfo();
		if (info == null) {
			return "App Info Null";
		}

		return String.format("Error Empty or null field found in: name=%b, path=%b, args=%b, state=%b",
				isEmpty(info.getName()), isEmpty(info.getPath()), info.getArgs() == null, exe.getState() == null);
	}

    private static boolean isEmpty(String text)
    {
        return (text == null || text.isEmpty());
    }
}
