package edu.nyu.cess.remote.common.app;

public class AppExecutionValidator
{
    public static boolean validate(AppExe exe)
    {
        return ! (exe == null || isEmpty(exe.getName()) || isEmpty(exe.getPath()) || isEmpty(exe.getArgs()) || isNotState(exe.getState()));
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
