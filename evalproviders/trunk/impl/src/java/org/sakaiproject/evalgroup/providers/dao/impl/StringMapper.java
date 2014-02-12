package org.sakaiproject.evalgroup.providers.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * RowMapper to handle Things
 *
 *
 */
public class StringMapper implements RowMapper{
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		return rs.getString(1);
	}
	
	
	
}
