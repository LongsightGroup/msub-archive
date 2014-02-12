package org.sakaiproject.evalgroup.providers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

import org.sakaiproject.evaluation.providers.EvalHierarchyProvider;
import org.sakaiproject.evaluation.constant.EvalConstants;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.evaluation.logic.externals.EvalExternalLogic;

import org.sakaiproject.hierarchy.HierarchyService;
import org.sakaiproject.hierarchy.dao.model.HierarchyNodePermission;
import org.sakaiproject.hierarchy.model.HierarchyNode;

import org.sakaiproject.evalgroup.providers.logic.ProjectLogic;
//import org.apache.wicket.spring.injection.annot.SpringBean;

import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.exception.IdUnusedException;



public class SimpleEvalHierarchyProviderImpl implements EvalHierarchyProvider {

  private static final Log log = LogFactory.getLog(SimpleEvalHierarchyProviderImpl.class);

  protected EvalExternalLogic externalLogic;
  public void setExternalLogic(EvalExternalLogic externalLogic) {
    this.externalLogic = externalLogic;
  }
  
  private HierarchyService hierarchyService;
  public void setHierarchyService(HierarchyService hierarchyService) {
    this.hierarchyService = hierarchyService;
  }
  
  //@SpringBean(name="org.sakaiproject.evalgroup.providers.logic.ProjectLogic")
  protected ProjectLogic projectLogic;
  public void setProjectLogic(ProjectLogic projectLogic) {
      this.projectLogic = projectLogic;
  }

  protected static  String PERM_ASSIGN_EVALUATION_COPY;

  protected static  String PERM_TA_ROLE_COPY;	
	

	
	/**
   * Initialize this provider
   */
  public void init() {
    log.info("init");
    
  }

	/**
    * Get the hierarchy root node of the eval hierarchy
    * 
    * @return the {@link EvalHierarchyNode} representing the root of the hierarchy
    * @throws IllegalStateException if no node can be obtained
    */
  public EvalHierarchyNode getRootLevelNode() {
	  EvalHierarchyNode rootNode = new EvalHierarchyNode();
	  rootNode.title = "Root";
	  rootNode.id = "1";
	  Set<String> children = new TreeSet<String>();
	  for (EvalHierarchyNode node1 : getChildNodes(rootNode.id, false)) {
	  	children.add(node1.id);
	  }
	  
	  rootNode.childNodeIds = children;
	  
	  Set<String> directChildren = new TreeSet<String>();
	  for (EvalHierarchyNode node2 : getChildNodes(rootNode.id, true)) {
	  	directChildren.add(node2.id);
	  }
	  
	  rootNode.directChildNodeIds = directChildren;
	  
	  return rootNode;
  }

   /**
    * Get the node object for a specific node id
    * 
    * @param nodeId a unique id for a hierarchy node
    * @return a {@link EvalHierarchyNode} object or null if none found
    */
   public EvalHierarchyNode getNodeById(String nodeId) {
       log.debug("getNodeById("+nodeId+")");
       if (nodeId.equals("null") || nodeId.equals("")) {
           return null;
       }

	  EvalHierarchyNode node = new EvalHierarchyNode();
	  try {
	      node = projectLogic.getNode(Long.parseLong(nodeId));
	      System.out.println("getNodeById: getNode()");
	  	  
	  	  if (node == null) {
	  	  	  return null;
	  	  }  
	  } catch (NumberFormatException e) {
  	      log.error("NumberFormatException on nodeId: "+nodeId);
	  } catch (Exception ex) {
	      log.error("Exception: getNodeById: " + ex.getMessage());
		  System.err.println("Exception: " + ex.getMessage());
	  } 
	  
	  Set<String> children = new TreeSet<String>();
	  for (EvalHierarchyNode childNode1 : getChildNodes(node.id, false)) {
	  	children.add(childNode1.id);
	  }
	  
	  node.childNodeIds = children;
	  
	  Set<String> directChildren = new TreeSet<String>();
	  for (EvalHierarchyNode childNode2 : getChildNodes(node.id, true)) {
	  	directChildren.add(childNode2.id);
	  }
	  
	  node.directChildNodeIds = directChildren;
	  
	  Set<String> parents = new TreeSet<String>();
	  
	  for (String parentId : node.directParentNodeIds) {
	      while (parentId != null) {
	          parents.add(parentId);
	          EvalHierarchyNode parent = getNodeById(parentId);
	          for (String parentId2 : parent.directParentNodeIds) {
	              parentId = parentId2;
	          }   
	      }
	  }
	  
	  node.parentNodeIds = parents;
	  
	  return node;
   }

   /**
    * Get a set of nodes based on an array of nodeIds,
    * allows efficient lookup of nodes
    * 
    * @param nodeIds unique ids for hierarchy nodes
    * @return a set of {@link EvalHierarchyNode} objects based on the given ids
    */
   public Set<EvalHierarchyNode> getNodesByIds(String[] nodeIds) {
	  Set<EvalHierarchyNode> nodes = new TreeSet<EvalHierarchyNode>();
	  log.debug("getNodesByIds()");
	  
	  StringBuffer nodeIdList = new StringBuffer();
	  if (nodeIds.length > 0) {
	     nodeIdList.append(nodeIds[0]);
	     for (int i=1; i<nodeIds.length; i++) {
	        nodeIdList.append(", ");
	        nodeIdList.append(nodeIds[i]);
	     }
	  }
	  
	  try {
	     List<EvalHierarchyNode> tmpNodes = projectLogic.getNodes(nodeIdList.toString());
	     // normally this would be enough, but we need to get the child nodes too.
	     
	     for (Iterator it = tmpNodes.iterator(); it.hasNext();) {
	        EvalHierarchyNode newnode = (EvalHierarchyNode) it.next();
	        
	        Set<String> children = new TreeSet<String>();
			for (EvalHierarchyNode childNode1 : getChildNodes(newnode.id, false)) {
				children.add(childNode1.id);
			}
			  
			newnode.childNodeIds = children;
			  
			Set<String> directChildren = new TreeSet<String>();
			for (EvalHierarchyNode childNode2 : getChildNodes(newnode.id, true)) {
				directChildren.add(childNode2.id);
			}
			  
			newnode.directChildNodeIds = directChildren;
	        
	        nodes.add(newnode);
	     }
	  } catch (Exception ex) {
	     log.error("Exception: getNodesByIds: " + ex.getMessage());
		 System.err.println("Exception: " + ex.getMessage());
	  } 
	  return nodes;
   }

   /**
    * Get all children nodes for this node in the hierarchy, 
    * will return no nodes if this is not a parent node
    * 
    * @param nodeId a unique id for a hierarchy node
    * @param directOnly if true then only include the nodes 
    * which are directly connected to this node, 
    * else return every node that is a child of this node
    * @return a Set of {@link EvalHierarchyNode} objects representing 
    * all children nodes for the specified parent,
    * empty set if no children found
    */
   public Set<EvalHierarchyNode> getChildNodes(String nodeId, boolean directOnly) {
      Set<EvalHierarchyNode> nodes = new TreeSet<EvalHierarchyNode>();
	  
	  try {
	      if (nodeId == null) {
	          log.warn("nodeId is null");
	          return nodes;
	      }
	      
	     List<EvalHierarchyNode> tmpNodes = projectLogic.getParent(Long.parseLong(nodeId));
	     log.warn("Found this many childern for node "+nodeId+": "+tmpNodes.size());
	     
	     for (Iterator it = tmpNodes.iterator(); it.hasNext();) {
	        EvalHierarchyNode newnode = (EvalHierarchyNode) it.next();
	        
	        log.warn("Childnode is: "+newnode.title+" ("+newnode.id+")");
	        newnode.directParentNodeIds = new TreeSet<String>();
	        newnode.directParentNodeIds.add(nodeId);
	        
	        if(!directOnly) {
	            Set<EvalHierarchyNode> gNodes = getChildNodes(newnode.id, directOnly);
	            nodes.addAll(gNodes); // make a flat list
	            Set<String> grandChildren = new TreeSet<String>();
	            Set<String> grandChildrenDirect = new TreeSet<String>();
		    for (EvalHierarchyNode grandChildNode : gNodes) {
			grandChildren.add(grandChildNode.id);
			log.warn("grandChildNode is: "+grandChildNode.title);
				
			if (grandChildNode.directParentNodeIds.contains(newnode.id)) {
			    log.warn("Adding grandChildNode "+grandChildNode.title);
			    grandChildrenDirect.add(grandChildNode.id);
			}
		    }
		    newnode.childNodeIds = grandChildren;
		    newnode.directChildNodeIds = grandChildrenDirect;
				
		} else {
                    List<EvalHierarchyNode> tmpChildNodes = projectLogic.getParent(Long.parseLong(newnode.id));
                    Set<String> grandChildren = new TreeSet<String>();
                    for (Iterator it2 = tmpChildNodes.iterator(); it2.hasNext();) {
                            EvalHierarchyNode newChildNode = (EvalHierarchyNode) it2.next();
                            grandChildren.add(newChildNode.id);
                    }
                    newnode.childNodeIds = grandChildren;
                    newnode.directChildNodeIds = grandChildren;
                }
	        nodes.add(newnode);
	            
	     }
	     
	  } catch (NumberFormatException e) {
	      log.error("NumberFormatException on nodeId: "+nodeId); 
	  } catch (Exception ex) {
	     log.error("Exception: getChildNodes: " + ex.getCause());
	     ex.printStackTrace();
		 System.err.println("Exception: " + ex.getMessage());
	  } 
	  
	  return nodes;
      
   }


   /**
    * Get all the userIds for users which have a specific permission in a set of
    * hierarchy nodes, this can be used to check one node or many nodes as needed,
    * <br/>The actual permissions this should handle are shown at the top of this class
    * 
    * @param nodeIds an array of unique ids for hierarchy nodes
    * @param hierarchyPermConstant a HIERARCHY_PERM constant from {@link EvalConstants}
    * @return a set of userIds (not username/eid)
    */
   public Set<String> getUserIdsForNodesPerm(String[] nodeIds, String hierarchyPermConstant) {
       log.debug("getUserIdsForNodesPerm()");
       Set<String> s = null;
       s = hierarchyService.getUserIdsForNodesPerm(nodeIds, hierarchyPermConstant);
       return s;
   }

   /**
    * Get the hierarchy nodes which a user has a specific permission in,
    * this is used to find a set of nodes which a user should be able to see and to build
    * the list of hierarchy nodes for selecting eval groups to assign evaluations to,
    * <br/>The actual permissions this should handle are shown at the top of this class
    * 
    * @param userId the internal user id (not username)
    * @param hierarchyPermConstant a HIERARCHY_PERM constant from {@link EvalConstants}
    * @return a Set of {@link EvalHierarchyNode} objects
    */
   public Set<EvalHierarchyNode> getNodesForUserPerm(String userId, String hierarchyPermConstant) {
       log.debug("getNodesForUserPerm");
      Set<EvalHierarchyNode> evalNodes = new HashSet<EvalHierarchyNode>();
      List<EvalHierarchyNode> nodes = projectLogic.getNodesForUserPerm(userId, hierarchyPermConstant);
      if (nodes != null && nodes.size() > 0) {
          for (EvalHierarchyNode node : nodes) {
              if (node != null) {
                  EvalHierarchyNode evalNode = getNodeById(node.id);
                  if (evalNode == null) {
                      log.debug("evalNode is null!");
                  } else {
                      evalNodes.add( evalNode );
                  }
              }
          } 
          log.debug("found some nodes!");
      } else {
          log.debug("user has no node perms");
      }
      log.debug("Returning this many nodes: "+evalNodes.size());
      return evalNodes;
   }
   
   /**
    * Assign a user has a specific hierarchy permission at a specific hierarchy node
    *
    * @param userId the internal user id (not username)
    * @param nodeId a unique id for the hierarchy node
    * @param hierarchyPermConstant a HIERARCHY_PERM constant from {@link EvalConstants}
    * @param whether this should cascade
    */
   public void assignUserNodePerm(String userId, String nodeId, String hierarchyPermConstant, boolean cascade) {
       log.debug("Called assignUserNodePerm("+userId+", "+nodeId+", "+hierarchyPermConstant);
       
       if (userId == null || "".equals(userId)
               || nodeId == null || "".equals(nodeId)
               || hierarchyPermConstant == null || "".equals(hierarchyPermConstant)) {
           throw new IllegalArgumentException("Invalid arguments to assignUserNodePerm, no arguments can be null or blank: userId="+userId+", nodeId="+nodeId+", hierarchyPermConstant="+hierarchyPermConstant);
       }
       
       Boolean nodePerm = checkUserNodePerm(userId, nodeId, hierarchyPermConstant);
       EvalHierarchyNode node = getNodeById(nodeId);
       
       if (nodePerm == null || nodePerm == false) {
           // check it exists, this is where bombs
           if (node == null) {
               throw new IllegalArgumentException("Node id ("+nodeId+") provided is invalid, node does not exist");
           }
           // create the perm
           projectLogic.assignPerm(userId, Long.parseLong(nodeId), hierarchyPermConstant);
           log.debug("Called assignPerm("+userId+", "+nodeId+", "+hierarchyPermConstant);
           
           
       } else {
           // permission already set, do nothing
       }
       if (cascade) {
           // cascade the permission creation
           if (node != null 
                   && node.childNodeIds != null 
                   && node.childNodeIds.size() > 0) {
               
               // get all the permissions which are related to the nodes under this one
                       
               for (String childNodeId : node.childNodeIds) {
                   
                   List<HierarchyNodePermission> nodePerms = projectLogic.getPerms(userId, Long.parseLong(childNodeId), hierarchyPermConstant);
                   
                   if (nodePerms.size() == 0) {
                       projectLogic.assignPerm(userId, Long.parseLong(childNodeId), hierarchyPermConstant);
                   }
                   
               }
           }
       }
   }
   
   /**
    * Remove a user's specific hierarchy permission at a specific hierarchy node
    *
    * @param userId the internal user id (not username)
    * @param nodeId a unique id for the hierarchy node
    * @param hierarchyPermConstant a HIERARCHY_PERM constant from {@link EvalConstants}
    * @param whether this should cascade
    */
   public void removeUserNodePerm(String userId, String nodeId, String hierarchyPermConstant, boolean cascade) {
       log.debug("Called removeUserNodePerm("+userId+", "+nodeId+", "+hierarchyPermConstant);

       if (userId == null || "".equals(userId)
                  || nodeId == null || "".equals(nodeId)
                  || hierarchyPermConstant == null || "".equals(hierarchyPermConstant)) {
              throw new IllegalArgumentException("Invalid arguments to removeUserNodePerm, no arguments can be null or blank: userId="+userId+", nodeId="+nodeId+", hierarchyPermConstant="+hierarchyPermConstant);
       }

       Boolean nodePerm = checkUserNodePerm(userId, nodeId, hierarchyPermConstant);
       EvalHierarchyNode node = getNodeById(nodeId);
       
       if (node == null) {
             throw new IllegalArgumentException("Node id ("+nodeId+") provided is invalid, node does not exist");
       }

       if (nodePerm != null && nodePerm) {
              // remove the perm
              projectLogic.removePerm(userId, Long.parseLong(nodeId), hierarchyPermConstant);
              log.debug("Called removePerm("+userId+", "+nodeId+", "+hierarchyPermConstant);
       }
   }

   /**
    * Determine if a user has a specific hierarchy permission at a specific hierarchy node,
    * <br/>The actual permissions this should handle are shown at the top of this class
    * 
    * @param userId the internal user id (not username)
    * @param nodeId a unique id for a hierarchy node
    * @param hierarchyPermConstant a HIERARCHY_PERM constant from {@link EvalConstants}
    * @return true if the user has this permission, false otherwise
    */
   public boolean checkUserNodePerm(String userId, String nodeId, String hierarchyPermConstant) {
       boolean allowed = false;
       allowed = hierarchyService.checkUserNodePerm(userId, nodeId, hierarchyPermConstant);
       return allowed;
   }


   /**
    * Gets the list of nodes in the path from an eval group to the root node,
    * should be in order with the first node being the root node and the last node being
    * the parent node for the given eval group
    *  
    * @param evalGroupId the unique ID of an eval group
    * @return a List of {@link EvalHierarchyNode} objects (ordered from root to evalgroup)
    */
   public List<EvalHierarchyNode> getNodesAboveEvalGroup(String evalGroupId) {
      List<EvalHierarchyNode> nodes = new ArrayList<EvalHierarchyNode>();
      List<EvalHierarchyNode> nodes2 = new ArrayList<EvalHierarchyNode>();
      String parentId = "";
      
      try {
      
         SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
         Site site = siteService.getSite(evalGroupId.substring(6));
      
         String evalParentNodeId = "";
         
         Map<Long, String> rules = projectLogic.getRules();
         Iterator it = rules.entrySet().iterator(); 
         while (it.hasNext()) {
             Map.Entry pairs = (Map.Entry)it.next();
             if (site.getTitle().contains(pairs.getValue().toString())) {
                 evalParentNodeId = pairs.getKey().toString();
             }
         
         }
         
         if (evalParentNodeId != "") {
             
             EvalHierarchyNode node = projectLogic.getNode(Long.parseLong(evalParentNodeId));
             System.out.println("getNodesAboveEvalGroup: getNode("+Long.parseLong(evalParentNodeId)+")");

             //parentId = result1.getString("parent_id"); //TODO: node.directParentNodeIds
             Iterator parents = node.directParentNodeIds.iterator();
             if (parents.hasNext()) {
                 parentId = parents.next().toString();
             } else {
                 parentId = "";
             }
             
         
             Set<String> children = new TreeSet<String>();
	         for (EvalHierarchyNode childNode1 : getChildNodes(node.id, false)) {
	  	         children.add(childNode1.id);
	         }
	  
	         node.childNodeIds = children;
	  
	         Set<String> directChildren = new TreeSet<String>();
	         for (EvalHierarchyNode childNode2 : getChildNodes(node.id, true)) {
	  	         directChildren.add(childNode2.id);
	         }
	  
	         node.directChildNodeIds = directChildren;
         
             nodes.add(node);
         
             while (parentId != "" || parentId != null) {
                 EvalHierarchyNode node2 = projectLogic.getNode(Long.parseLong(parentId));
                 System.out.println("getNodesAboveEvalGroup: getNode("+Long.parseLong(parentId)+")");
             
                 Set<String> children2 = new TreeSet<String>();
	             for (EvalHierarchyNode childNode1 : getChildNodes(node2.id, false)) {
	  	             children2.add(childNode1.id);
	             }
	  
	             node2.childNodeIds = children2;
	  
	             Set<String> directChildren2 = new TreeSet<String>();
	             for (EvalHierarchyNode childNode2 : getChildNodes(node2.id, true)) {
	  	             directChildren2.add(childNode2.id);
	             }
	  
	             node2.directChildNodeIds = directChildren2;
             
                 // want to add the parent to the begining of the list
                 nodes2.clear();
                 nodes2.add(node2);
                 nodes.addAll(0, nodes2);
             
                 //parentId = result2.getString("parent_id"); //TODO: node.directParentNodeIds
                 Iterator parents2 = node2.directParentNodeIds.iterator();
                 if (parents2.hasNext()) {
                      parentId = parents2.next().toString();
                 } else {
                      parentId = "";
                 }
             } 
          }
          
          
      } catch (IdUnusedException e) {
         System.err.println("IdUnusedException: " + e.getMessage());
      } catch (NumberFormatException e) {
  	      log.error("NumberFormatException on either evalParentNodeId or parentId("+parentId+")");
      } catch (Exception ex) {
         log.error("Exception: getNodesAboveEvalGroup: " + ex.getMessage());
		 System.err.println("Exception: " + ex.getMessage());
	  } 
	  
	  return nodes;
      
   }

   /**
    * Get the set of eval group ids beneath a specific hierarchy node, note that this should only
    * include the eval groups directly beneath this node and not any groups that are under
    * child nodes of this node<br/>
    * Note: this will not fail if the nodeId is invalid, it will just return no results<br/>
    * Convenience method for {@link #getEvalGroupsForNodes(String[])}
    * 
    * @param nodeId a unique id for a hierarchy node
    * @return a Set of eval group ids representing the eval groups beneath this hierarchy node
    */
   public Set<String> getEvalGroupsForNode(String nodeId) {
       Set<String> results = new TreeSet<String>();
       
       if(nodeId.equals("1")) {
           // root node will not have have any rules.
           return results;
       }
       
       try {
           
           // instead of just getting the eval groups from the db like below....
           //result1 = statement.executeQuery("SELECT * from HIER_PROVIDER_evalgroups where node_id = "+nodeId+";");
           
           // ...we want to get the rule from the db for this node and then query for all the sites that match that rule
           //result = statement.executeQuery("SELECT rule from HIER_PROVIDER_evalgrouprules where node_id = "+nodeId+";");
           
           List<String> rules = projectLogic.getRule(Long.parseLong(nodeId));

           Iterator it = rules.iterator();
           while (it.hasNext()) {
        	   String rule = it.next().toString();
        	   SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
		  
        	   List<Site> sites = siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null);
        	   log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
		   
        	   for (Site site : sites) {
        		   log.warn("found site: "+site.getTitle()+" "+site.getId());
        		   results.add("/site/"+site.getId());
        	   }
           }
           
       } catch (NumberFormatException e) {
           log.error("NumberFormatException where nodeId: "+nodeId);
       } catch (Exception ex) {
           log.error("Exception: getEvalGroupsForNode: " + ex.getMessage());
	       System.err.println("Exception: " + ex.getMessage());
	   } 
       
       
       return results;
   }

   /**
    * Get the set of eval group ids beneath a set of hierarchy nodes, note that this should only
    * include the eval groups directly beneath these nodes and not any groups that are under
    * child nodes of this node<br/>
    * Note: this will not fail if the nodeId is invalid, it will just return no results,
    * an empty array of nodeids will return an empty map
    * 
    * @param nodeIds a set of unique ids for hierarchy nodes
    * @return a Map of nodeId -> a set of eval group ids representing the eval groups beneath that node
    */
   public Map<String, Set<String>> getEvalGroupsForNodes(String[] nodeIds) {
       Map map = new LinkedHashMap();
       
       for (String nodeId : nodeIds) {
           if(nodeId.equals("1")) {
               continue;
           }
		   Set<String> results = new TreeSet<String>();
		   try {
			   
			   List<String> rules = projectLogic.getRule(Long.parseLong(nodeId));
			   
			   Iterator it = rules.iterator();
			   while (it.hasNext()) {
               	   String rule = it.next().toString();
               
                   SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
		  
		           List<Site> sites = siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null);
		           log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
		   
		           for (Site site : sites) {
		               log.warn("found site: "+site.getTitle()+" "+site.getId());
		               results.add("/site/"+site.getId());
                   }
               }
			 
		   } catch (NumberFormatException e) {
		       log.error("NumberFormatException for nodeId: "+nodeId);
		   } catch (Exception ex) {
		       log.error("Exception: getEvalGroupsForNodes: " + ex.getMessage());
			   System.err.println("Exception: " + ex.getMessage());
		   } 
		   
		   // add results to map
		   map.put(nodeId, results);
       }
       return map;
   }

   /**
    * Get the count of the number of eval groups assigned to each node in a group of nodes
    * @param nodeIds an array of unique ids for hierarchy nodes
    * @return a map of nodeId -> number of eval groups
    */
   public Map<String, Integer> countEvalGroupsForNodes(String[] nodeIds) {
       Map map = new LinkedHashMap();
       
       for (String nodeId : nodeIds) {
           if(nodeId.equals("1")) {
               map.put(nodeId,0);
               continue;
           }
           try {
			   List<String> rules = projectLogic.getRule(Long.parseLong(nodeId));
			   List<Site> sites = new ArrayList<Site>();

   			   Iterator it = rules.iterator();
   			   while (it.hasNext()) {
   			       String rule = it.next().toString();
           
   			       SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
   			       for (Site site : siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null)) {
                  	       sites.add(site);
   			       }
			       log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
               }

               map.put(nodeId, sites.size());
             
           } catch (NumberFormatException e) {
               log.error("NumberFormatException for nodeId: "+nodeId);
           } catch (Exception ex) {
               log.error("Exception: countEvalGroupsForNodes: " + ex.getMessage());
			   System.err.println("Exception: " + ex.getMessage());
		   } 
           
       }
   
       return map;
   }
   
   private EvalHierarchyNode makeEvalNode(HierarchyNode node) {
       EvalHierarchyNode eNode = new EvalHierarchyNode();
       eNode.id = node.id;
       eNode.title = node.title;
       eNode.description = node.description;
       eNode.directChildNodeIds = node.directChildNodeIds;
       eNode.childNodeIds = node.childNodeIds;
       eNode.directParentNodeIds = node.directParentNodeIds;
       eNode.parentNodeIds = node.parentNodeIds;
       return eNode;
   }


}
