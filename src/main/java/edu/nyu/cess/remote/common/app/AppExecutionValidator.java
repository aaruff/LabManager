package edu.nyu.cess.remote.common.app;

public class AppExecutionValidator
{
    public static boolean validate(AppExe exe)
    {
		if (exe == null) {
			return false;
		}

		AppInfo info = exe.getAppInfo();
        return ! (info == null || isEmpty(info.getName()) || isEmpty(info.getPath()) || isEmpty(info.getArgs()) || isNotState(exe.getState()));
    }

    private static boolean isEmpty(String text)
    {
        return (text == null || text.isEmpty());
    }

    private static boolean isNotState(AppState state)
    {
        return state == null;
    }
}
