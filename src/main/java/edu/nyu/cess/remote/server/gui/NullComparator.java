package edu.nyu.cess.remote.server.gui;

public class NullComparator
{
	private static final int EQUAL = 0;
	private static final int LESS = -1;
	private static final int GREATER = 1;

	/**
	 * Compares two strings one of which is null.
	 *
	 * @param s1 string one
	 * @param s2 string two
     * @return 1 if greater, -1 if less, 0 if equal
     */
	public static int compareNullString(String s1, String s2)
	{
		if (s1 != null && s2 == null) {
			return GREATER;
		}
		else if (s1 == null && s2 != null) {
			return LESS;
		}
		else {
			return EQUAL;
		}
	}
}
