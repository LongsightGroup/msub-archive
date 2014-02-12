package org.etudes.tool.melete;

import java.util.Comparator;
import org.sakaiproject.site.api.Group;
public class GroupComparator implements Comparator {

	private String m_property = null;

	private boolean m_ascending = true;

	/**
	 * Construct.
	 * 
	 * @param property
	 *            The property name used for the sort.
	 * @param asc
	 *            true if the sort is to be ascending (false for descending).
	 */
	public GroupComparator(String property, boolean ascending) {
		m_property = property;
		m_ascending = ascending;

	} // GroupsComparator

	public int compare(Object o1, Object o2) {
		int rv = 0;

		String t1 = ((Group) o1).getTitle();
		String t2 = ((Group) o2).getTitle();
			
		rv = t1.compareTo(t2);

		if (!m_ascending)
			rv = -rv;

		return rv;
	}
}