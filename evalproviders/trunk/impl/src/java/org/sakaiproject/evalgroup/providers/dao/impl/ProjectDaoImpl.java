package org.sakaiproject.evalgroup.providers.dao.impl;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Calendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.log4j.Logger;

import org.sakaiproject.component.cover.ServerConfigurationService;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;

import org.sakaiproject.evalgroup.providers.dao.ProjectDao;
//import org.sakaiproject.evalgroup.providers.model.Thing;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;

/**
 * Implementation of ProjectDao
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class ProjectDaoImpl extends JdbcDaoSupport implements ProjectDao {

	private static final Logger log = Logger.getLogger(ProjectDaoImpl.class);
	private static final String ORACLE_VENDOR = "oracle";
	
	private PropertiesConfiguration statements;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public EvalHierarchyNode getNode(long id) {
		
		if(log.isDebugEnabled()) {
			log.debug("getNode(" + id + ")");
		}
		
		try {
			return (EvalHierarchyNode) getJdbcTemplate().queryForObject(getStatement("select.node"),
				new Object[]{Long.valueOf(id)},
				new NodeMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<EvalHierarchyNode> getNodes(String ids) {
		if(log.isDebugEnabled()) {
			log.debug("getNodes("+ids+")");
		}
		
		try {
			return getJdbcTemplate().query(getStatement("select.nodes"),
			    new Object[]{ids},
				new NodeMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<EvalHierarchyNode> getParent(long id) {
	    if(log.isDebugEnabled()) {
			log.debug("getParent(" + id + ")");
		}
		
		try {
			return (List<EvalHierarchyNode>) getJdbcTemplate().query(getStatement("select.parent"),
				new Object[]{Long.valueOf(id)},
				new NodeMapper()				
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map<Long, String> getRules() {
	    if(log.isDebugEnabled()) {
			log.debug("getRules()");
		}
		Map<Long, String> rules = new HashMap();
		
		try {
		    List<Map> rows = getJdbcTemplate().queryForList(getStatement("select.rules"));
		    for (Map row : rows) {
		        rules.put((Long) row.get("NODE"), (String) row.get("RULE"));
		    }
		    /* return (Map) getJdbcTemplate().queryForObject(getStatement("select.rules"),
		        new RuleMapper()
		    ); */ 
		} catch (DataAccessException ex) {
		    log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
		    return null;
		}
		return rules;
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRule(long id) {
	    if(log.isDebugEnabled()) {
			log.debug("getRule("+id+")");
		}
		
		try {
		    return getJdbcTemplate().query(getStatement("select.rule"),
		        new Object[]{Long.valueOf(id)},
		        new StringMapper()
		    );  
		} catch (DataAccessException ex) {
		    log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
		    return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void assignPerm(String userid, long id, String hierarchyPermConstant) {
	    if(log.isDebugEnabled()) {
	        log.debug("assignPerm("+userid+" "+id+" "+hierarchyPermConstant+")");
	    }
	    
	    try {
	        
	        Connection conn = getJdbcTemplate().getDataSource().getConnection();
	        
	        if (conn == null) {
	            log.debug("conn is null!");
	        }
	        
	        Calendar cal = Calendar.getInstance();
	        
	        try {
	            PreparedStatement ps = conn.prepareStatement(getStatement("insert.perm"));
	        
	            ps.setString(1, userid);
	            ps.setLong(2, Long.valueOf(id));
	            ps.setString(3, hierarchyPermConstant);
	            ps.setDate(4, new Date(cal.getTimeInMillis()));
	            ps.setDate(5, new Date(cal.getTimeInMillis()));
	            ps.executeUpdate();
	            ps.close();
	        
	            conn.commit();
	        } catch (Exception e) {
	            log.error("Error executing query: " + e.getClass() + ":" + e.getMessage());
	        } finally {
	            conn.close();
	        }   
	        
	    } catch (DataAccessException ex) {
	        log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
	    } catch (Exception e) {
	        log.error("Error executing query: " + e.getClass() + ":" + e.getMessage());
	    }
	    
	    log.debug("Finished assignPerm()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void removePerm(String userid, long id, String hierarchyPermConstant) {
	    if(log.isDebugEnabled()) {
			log.debug("getRule("+id+")");
		}
		
	    try {
	        Connection conn = getJdbcTemplate().getDataSource().getConnection();
	        
	        if (conn == null) {
	            log.debug("conn is null!");
	        }
	        
	        Calendar cal = Calendar.getInstance();
	        
	        try {
	            PreparedStatement ps = conn.prepareStatement(getStatement("remove.perm"));
	        
	            ps.setString(1, userid);
	            ps.setLong(2, Long.valueOf(id));
	            ps.setString(3, hierarchyPermConstant);
	            ps.executeUpdate();
	            ps.close();
	        
	            conn.commit();
	        } catch (Exception e) {
	            log.error("Error executing query: " + e.getClass() + ":" + e.getMessage());
	        } finally {
	            conn.close();
	        }
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
		} catch (Exception e) {
   	        log.error("Error executing query: " + e.getClass() + ":" + e.getMessage());
   	    }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<HierarchyNodePermission> getPerms(String userid, long id, String hierarchyPermConstant) {
	    if(log.isDebugEnabled()) {
	        log.debug("getPerms("+userid+" "+id+" "+hierarchyPermConstant+")");
	    }
	    
	    try {
	        return (List<HierarchyNodePermission>) getJdbcTemplate().query(getStatement("select.perms"),
				new Object[]{userid, Long.valueOf(id), hierarchyPermConstant},
				new PermMapper()				
			);
	    } catch (DataAccessException ex) {
	        log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
	        return null;
	    }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<EvalHierarchyNode> getNodesForUserPerm(String userId, String hierarchyPermConstant) {
		if(log.isDebugEnabled()) {
			log.debug("getNodesForUserPerm("+userId+", "+hierarchyPermConstant+")");
		}
		
		try {
			return getJdbcTemplate().query(getStatement("select.nodes.user"),
			    new Object[]{userId, hierarchyPermConstant},
				new NodeMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	/** This tool does not provide an interface for adding rules...
	@SuppressWarnings("unchecked")
	public boolean addThing(Thing t) {
		
		if(log.isDebugEnabled()) {
			log.debug("addThing( " + t.toString() + ")");
		}
		
		try {
			getJdbcTemplate().update(getStatement("insert.thing"),
				new Object[]{t.getName()}
			);
			return true;
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return false;
		}
	} */
	
	/**
	 * init
	 */
	public void init() {
		log.info("init()");
		
		//setup the vendor
		String vendor = ServerConfigurationService.getInstance().getString("vendor@org.sakaiproject.db.api.SqlService", null);
		
		//initialise the statements
		initStatements(vendor);
		
		//setup tables if we have auto.ddl enabled.
		boolean autoddl = ServerConfigurationService.getInstance().getBoolean("auto.ddl", true);
		if(autoddl) {
			initTables(vendor);
		}
	}
	
	/**
	 * Sets up our tables
	 */
	private void initTables(String vendor) {
		try {
			getJdbcTemplate().execute(getStatement("create.table1"));
			getJdbcTemplate().execute(getStatement("create.table2"));
			
			if( ORACLE_VENDOR.equalsIgnoreCase( vendor ) ) {
			    getJdbcTemplate().execute( getStatement( "create.sequence1" ) );
			    getJdbcTemplate().execute( getStatement( "create.sequence2" ) );
			}
		} catch (DataAccessException ex) {
			log.info("Error creating tables: " + ex.getClass() + ":" + ex.getMessage());
			return;
		}
	}
	
	/**
	 * Loads our SQL statements from the appropriate properties file
	 
	 * @param vendor	DB vendor string. Must be one of mysql, oracle, hsqldb
	 */
	private void initStatements(String vendor) {
		
		URL url = getClass().getClassLoader().getResource(vendor + ".properties"); 
		
		try {
			statements = new PropertiesConfiguration(); //must use blank constructor so it doesn't parse just yet (as it will split)
			statements.setReloadingStrategy(new InvariantReloadingStrategy());	//don't watch for reloads
			statements.setThrowExceptionOnMissing(true);	//throw exception if no prop
			statements.setDelimiterParsingDisabled(true); //don't split properties
			statements.load(url); //now load our file
			statements.setAutoSave(true);
		} catch (ConfigurationException e) {
			log.error(e.getClass() + ": " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Get an SQL statement for the appropriate vendor from the bundle
	
	 * @param key
	 * @return statement or null if none found. 
	 */
	private String getStatement(String key) {
		try {
			return statements.getString(key);
		} catch (NoSuchElementException e) {
			log.error("Statement: '" + key + "' could not be found in: " + statements.getFileName());
			return null;
		}
	}
}
