package edu.nyu.cess.remote.server.gui;

import java.util.Comparator;

/**
 * Created by aruff on 2/15/16.
 */
public class ViewClient implements Comparable<ViewClient>
{
    public static final SortByHostname SORT_BY_HOSTNAME = new SortByHostname();
    public static final SortByIp SORT_BY_IP = new SortByIp();

    private final String name;
    private final String ipAddress;

    public ViewClient(String name, String ipAddress)
    {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName()
    {
        return name;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }


    /**
     * Compares this client's hostname to the client parameter, based upon the lexical ordering of the hostname.
     * Note: A client having a null name, is considered less than one that doesn't. Two null clients are considered
     * equal.
     *
     * @param client the client being compared to.
     * @return the comparison result: 0 if equal, -1 if less, or 1 if greater
     */
    @Override public int compareTo(ViewClient client)
    {
        return name.compareTo(client.getName());
    }

    public static class SortByHostname implements Comparator<ViewClient>
    {
        /**
         * Compares one client to another by their host name.
         * @param c1 client one
         * @param c2 client two
         * @return returns 1 if greater, -1 if less, 0 if equal
         */
        @Override public int compare(ViewClient c1, ViewClient c2)
        {
            if (c1.getName() == null || c2.getName() == null) {
                NullComparator.compareNullString(c1.getName(), c2.getName());
            }

            return c1.getName().compareTo(c2.getName());
        }
    }

    /**
     * Provides the sort by IP address method.
     */
    public static class SortByIp implements Comparator<ViewClient>
    {
        /**
         * Compares one client to another by their IP address.
         * @param c1 client one
         * @param c2 client two
         * @return returns 1 if greater, -1 if less, 0 if equal
         */
        @Override public int compare(ViewClient c1, ViewClient c2)
        {
            if (c1.getIpAddress() == null || c2.getIpAddress() == null) {
                NullComparator.compareNullString(c1.getIpAddress(), c2.getIpAddress());
            }

            return c1.getIpAddress().compareTo(c2.getIpAddress());
        }
    }
}
