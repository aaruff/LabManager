package edu.nyu.cess.remote.server.app;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * An application configuration collection.
 */
public class AppProfileCollection implements Iterable<AppInfo>
{
	private List<AppInfo> appInfoCollection;

	/**
	 * Construct the collection
	 *
	 * @param appInfoCollection
	 */
	public AppProfileCollection(List<AppInfo> appInfoCollection)
	{
		this.appInfoCollection = appInfoCollection;
	}

	public static void sort(Object[] a, Comparator comparator)
	{

	}

	@Override
	public Iterator<AppInfo> iterator()
	{
		return appInfoCollection.iterator();
	}

}
