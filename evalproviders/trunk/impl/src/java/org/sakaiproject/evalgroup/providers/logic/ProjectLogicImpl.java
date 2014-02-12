package org.sakaiproject.evalgroup.providers.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import org.sakaiproject.evalgroup.providers.dao.ProjectDao;
import org.sakaiproject.evalgroup.providers.logic.SakaiProxy;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);

	
	/**
	 * {@inheritDoc}
	 */
	public EvalHierarchyNode getNode(long id) {
		
		//check cache 
		Element element = cache.get(id);
		if(element != null) {
			if(log.isDebugEnabled()) {
				log.debug("Fetching item from cache for: " + id);
			}
			return (EvalHierarchyNode)element.getObjectValue();
		}
		
		//if nothing from cache, get from db and cache it 
		EvalHierarchyNode n = dao.getNode(id);
			
		if(n != null) {
			if(log.isDebugEnabled()) {
				log.debug("Adding item to cache for: " + id);
			}
			cache.put(new Element(id,n));
		}

		return n;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<EvalHierarchyNode> getNodes(String ids) {
		return dao.getNodes(ids);
	}
	
	/**
	 * {@inheritDoc}
	 */
	 /*
	public boolean addThing(Thing t) {
		return dao.addThing(t);
	} */
	
    /**
	 * {@inheritDoc}
	 */
	public List<EvalHierarchyNode> getParent(long id) {
		return dao.getParent(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Map<Long, String> getRules() {
	    return dao.getRules();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<String> getRule(long id) {
	    return dao.getRule(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void assignPerm(String userid, long id, String hierarchyPermConstant) {
	    dao.assignPerm(userid, id, hierarchyPermConstant);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removePerm(String userid, long id, String hierarchyPermConstant) {
	    dao.removePerm(userid, id, hierarchyPermConstant);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<HierarchyNodePermission> getPerms(String userid, long id, String hierarchyPermConstant) {
	    return dao.getPerms(userid, id, hierarchyPermConstant);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<EvalHierarchyNode> getNodesForUserPerm(String userId, String hierarchyPermConstant) {
	    return dao.getNodesForUserPerm(userId, hierarchyPermConstant);
	}
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}
	
	@Setter
	private ProjectDao dao;
	
	@Setter
	private Cache cache;

}
