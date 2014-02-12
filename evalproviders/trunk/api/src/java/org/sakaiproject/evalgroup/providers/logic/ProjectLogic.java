package org.sakaiproject.evalgroup.providers.logic;

import java.util.List;
import java.util.Map;

import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;


/**
 * An example logic interface
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public interface ProjectLogic {

	/**
	 * Get a Thing
	 * @return
	 */
	public EvalHierarchyNode getNode(long id);
	
	/**
	 * Get all Things
	 * @return
	 */
	public List<EvalHierarchyNode> getNodes(String ids);
	
	/**
	 * Add a new Thing
	 * @param t	Thing
	 * @return boolean if success, false if not
	 */
	/* public boolean addThing(Thing t); */
	
	public List<EvalHierarchyNode> getParent(long id);
	
	public Map<Long, String> getRules();
	
	public List<String> getRule(long id);
	
	public void assignPerm(String userid, long id, String hierarchyPermConstant);
	
	public void removePerm(String userid, long id, String hierarchyPermConstant);
	
	public List<HierarchyNodePermission> getPerms(String userid, long id, String hierarchyPermConstant);
	
	public List<EvalHierarchyNode> getNodesForUserPerm(String userId, String hierarchyPermConstant);

}
