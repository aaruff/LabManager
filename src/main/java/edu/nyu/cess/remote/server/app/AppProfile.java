package edu.nyu.cess.remote.server.app;

/**
 * The application profile class.
 */
public class AppProfile implements Comparable<AppProfile>
{
	private String name;
	private String path;
	private String options;

	public AppProfile(){}

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
	public String getOptions()
	{
		return options;
	}

	/**
	 * Sets the option.
	 * @param options
     */
	public void setOptions(String options)
	{
		this.options = options;
	}

	/**
	 * Compare name properties and returns an integer that is:
	 *    0: This object's name is equivalent to the argument's.
	 *  < 0: This object's profile name lexically precedes the argument's name.
	 *  > 0: This object's profile name lexically succeeds the argument's name.
	 * @param appProfile
	 * @return
     */
	@Override
	public int compareTo(AppProfile appProfile)
	{
		return name.compareTo(appProfile.getName());
	}
}
