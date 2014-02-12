package org.sakaiproject.evalgroup.providers.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

import org.springframework.jdbc.core.RowMapper;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;

/**
 * RowMapper to handle Things
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class PermMapper implements RowMapper{
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public HierarchyNodePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		HierarchyNodePermission perm = new HierarchyNodePermission();
		
		perm.setId(rs.getLong("ID"));
		perm.setUserId(rs.getString("userId"));
		perm.setNodeId(rs.getString("nodeId"));
		perm.setPermission(rs.getString("permission"));
		
		return perm;
	}
	
	
	
}
