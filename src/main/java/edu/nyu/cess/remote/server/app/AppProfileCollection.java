package edu.nyu.cess.remote.server.app;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * An application configuration collection.
 */
public class AppProfileCollection implements Iterable<AppProfile>
{
	private List<AppProfile> appProfileCollection;

	/**
	 * Construct the collection
	 *
	 * @param appProfileCollection
	 */
	public AppProfileCollection(List<AppProfile> appProfileCollection)
	{
		this.appProfileCollection = appProfileCollection;
	}

	public static void sort(Object[] a, Comparator comparator)
	{

	}

	@Override
	public Iterator<AppProfile> iterator()
	{
		return appProfileCollection.iterator();
	}

}
