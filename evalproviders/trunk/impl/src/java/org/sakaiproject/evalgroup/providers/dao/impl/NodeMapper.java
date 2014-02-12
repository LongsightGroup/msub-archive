package org.sakaiproject.evalgroup.providers.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

import org.springframework.jdbc.core.RowMapper;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;

/**
 * RowMapper to handle Things
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class NodeMapper implements RowMapper{
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public EvalHierarchyNode mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		EvalHierarchyNode node = new EvalHierarchyNode();
		
		node.id = String.valueOf(rs.getLong("ID"));
		node.title = rs.getString("NAME");
		node.description = rs.getString("NAME");
		Set<String> parent = new HashSet(); 
		parent.add(rs.getString("PARENT_ID"));
		node.directParentNodeIds = parent;
		
		return node;
	}
	
	
	
}
