package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

/**
 * The application profile class.
 */
public class AppInfo implements Comparable<AppInfo>, Serializable
{
	private static final long serialVersionUID = 989301161571879122L;

	private String name;
	private String path;
	private String args;

	public AppInfo()
	{
		name = "";
		path = "";
		args = "";
	}

	public AppInfo(String name, String path, String args)
	{
		this.name = name;
		this.path = path;
		this.args = args;
	}

	/**
	 * Returns the app name.
	 * @return name
     */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the app name.
	 * @param name
     */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the path string.
	 * @return path string
     */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the app path.
	 * @param path
     */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Returns the option.
	 * @return option string
     */
	public String getArgs()
	{
		return args;
	}

	/**
	 * Sets the option.
	 * @param args
     */
	public void setArgs(String args)
	{
		this.args = args;
	}

	public boolean equals(AppInfo other)
	{
		return name.equals(other.getName()) && path.equals(other.getPath()) && args.equals(other.getArgs());
	}

	public AppInfo clone()
	{
		return new AppInfo(name, path, args);
	}

	/**
	 * Compare name properties and returns an integer that is:
	 *    0: This object's name is equivalent to the argument's.
	 *  < 0: This object's profile name lexically precedes the argument's name.
	 *  > 0: This object's profile name lexically succeeds the argument's name.
	 * @param appInfo
	 * @return
     */
	@Override
	public int compareTo(AppInfo appInfo)
	{
		return name.compareTo(appInfo.getName());
	}

	public String toString()
	{
		return String.format("{name=%s, path=%s, args=%s}", name, path, args);
	}
}
