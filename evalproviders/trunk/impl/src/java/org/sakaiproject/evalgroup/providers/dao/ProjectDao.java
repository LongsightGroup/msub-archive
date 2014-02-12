package org.sakaiproject.evalgroup.providers.dao;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;

/**
 * DAO interface for our project
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface ProjectDao {

	/**
	 * Gets a single Node from the db
	 * 
	 * @return a Node or null if no result
	 */
	public EvalHierarchyNode getNode(long id);
	
	/**
	 * Get a group of Nodes
	 * @return a list of items, an empty list if no items
	 */
	public List<EvalHierarchyNode> getNodes(String ids);

    /**
     * Get a list of Nodes who have this parent (the children)
     * @return a Node
     */
	public List<EvalHierarchyNode> getParent(long id);
	
	/**
	 * Get all the rules in the system
	 * @return List of strings, which are the rule patterns
	 */
	public Map<Long, String> getRules();
	
	/**
	 * Get the rule(s) for a particular node
	 * @return List of strings, which are the rule patterns
	 */
	public List<String> getRule(long id);
	
	/** 
	 * Assign a user has a specific hierarchy permission at a specific hierarchy node
	 */
	public void assignPerm(String userid, long id, String hierarchyPermConstant);
	
	/** 
	 * Remove a specific hierarchy permission at a specific hierarchy node for a user
	 */
	public void removePerm(String userid, long id, String hierarchyPermConstant);
	
	/** 
	 * Get the exisiting hierarchy permissisions for a user at a specific hierarchy node
	 */
	public List<HierarchyNodePermission> getPerms(String userid, long id, String hierarchyPermConstant);
	
	/**
	 * Get the nodes that this user has some sort of hierarchy permissions over
	 */
	public List<EvalHierarchyNode> getNodesForUserPerm(String userId, String hierarchyPermConstant);
	
}
