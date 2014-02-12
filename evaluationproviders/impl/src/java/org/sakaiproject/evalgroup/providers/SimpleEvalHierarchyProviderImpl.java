package org.sakaiproject.evalgroup.providers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sakaiproject.evaluation.providers.EvalHierarchyProvider;
import org.sakaiproject.evaluation.constant.EvalConstants;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;
import org.sakaiproject.evaluation.logic.externals.EvalExternalLogic;

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

  protected static  String PERM_ASSIGN_EVALUATION_COPY;

  protected static  String PERM_TA_ROLE_COPY;	
	

	
	/**
   * Initialize this provider
   */
  public void init() {
    log.info("init");
    
  }
  
  private DataSource dataSource;
  
  public void setDataSource(DataSource dataSource) {
	  this.dataSource = dataSource;
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
	  Connection conn;
	  ResultSet result;
	  EvalHierarchyNode node = new EvalHierarchyNode();
	  try {
	  	  conn = dataSource.getConnection();
	  	  Statement statement = conn.createStatement();
	  	  result = statement.executeQuery("SELECT * FROM HIER_PROVIDER_nodes where id = "+nodeId+";");
	  	  if (result != null) {
	  	  	  result.first();
	  	  	  node.title = result.getString("name");
	  	  	  node.id = nodeId;
	  	  	  conn.close();
	  	  } else {
	  	  	  conn.close();
	  	  	  return null;
	  	  }  
	  } catch (SQLException ex) {
		  System.err.println("SQLException: " + ex.getMessage());
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
	  Connection conn;
	  ResultSet result;
	  
	  StringBuffer nodeIdList = new StringBuffer();
	  if (nodeIds.length > 0) {
	     nodeIdList.append(nodeIds[0]);
	     for (int i=1; i<nodeIds.length; i++) {
	        nodeIdList.append(", ");
	        nodeIdList.append(nodeIds[i]);
	     }
	  }
	  
	  try {
	     conn = dataSource.getConnection();
	     Statement statement = conn.createStatement();
	     
	     result = statement.executeQuery("SELECT * FROM HIER_PROVIDER_nodes where id in ("+nodeIdList.toString()+");");
	     
	     while (result.next()) {
	        EvalHierarchyNode newnode = new EvalHierarchyNode();
	        newnode.id = result.getString("id");
	        newnode.title = result.getString("name");
	        
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
	     conn.close();
	  } catch (SQLException ex) {
		 System.err.println("SQLException: " + ex.getMessage());
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
	  Connection conn;
	  ResultSet result;
	  
	  try {
	     conn = dataSource.getConnection();
	     Statement statement = conn.createStatement();
	     
	     result = statement.executeQuery("Select * from HIER_PROVIDER_nodes where parent_id = "+nodeId+";");
	     
	     while (result.next()) {
	        EvalHierarchyNode newnode = new EvalHierarchyNode();
	        newnode.id = result.getString("id");
	        newnode.title = result.getString("name");
	        newnode.directParentNodeIds = new TreeSet<String>();
	        newnode.directParentNodeIds.add(nodeId);
	        
	        Set<String> children = new TreeSet<String>();
			for (EvalHierarchyNode childNode1 : getChildNodes(newnode.id, false)) {
				children.add(childNode1.id);
				
				if(!directOnly) {
				    // add the child node to the result set
				    nodes.add(childNode1);
				
				    // traverse and retrieve grandchild (etc) nodes
				    //for (EvalHierarchyNode grandchildNode : getChildNodes(childNode1.id, false)) {
				    //    nodes.add(grandchildNode);
				    //}
				}
			}
			  
			newnode.childNodeIds = children;
			  
			Set<String> directChildren = new TreeSet<String>();
			for (EvalHierarchyNode childNode2 : getChildNodes(newnode.id, true)) {
				directChildren.add(childNode2.id);
			}
			  
			newnode.directChildNodeIds = directChildren;
	        
	        nodes.add(newnode);
	            
	     }
	     conn.close();
	  
	  } catch (SQLException ex) {
		 System.err.println("SQLException: " + ex.getMessage());
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
       // TODO
       Set<String> ss = new TreeSet<String>();
       ss.add("admin");
       return ss;
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
      // TODO
      Set<EvalHierarchyNode> hs = new TreeSet<EvalHierarchyNode>();
      hs.add(getRootLevelNode());
      return hs;
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
      // TODO
      return true;
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
      Connection conn;
      ResultSet result;
      ResultSet result1;
      ResultSet result2;
      
      try {
      
         SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
         Site site = siteService.getSite(evalGroupId.substring(6));
      
         String evalParentNodeId = "";
      
         conn = dataSource.getConnection();
         Statement statement = conn.createStatement();
         
         result = statement.executeQuery("select * from HIER_PROVIDER_evalgrouprules;");
         while (result.next()) {
             if (site.getTitle().indexOf(result.getString("rule")) != 0) {
                 evalParentNodeId = result.getString("node_id");
             }
         
         }
         
         if (evalParentNodeId != "") {
             result1 = statement.executeQuery("SELECT * FROM HIER_PROVIDER_nodes where id = "+ evalParentNodeId+";");
         
             result1.first();
             EvalHierarchyNode node = new EvalHierarchyNode();
             node.title = result1.getString("name");
             node.id = result1.getString("id");
             parentId = result1.getString("parent_id");
         
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
                 result2 = statement.executeQuery("SELECT * FROM HIER_PROVIDER_nodes where id = "+ parentId+";");
                 result2.first();
                 EvalHierarchyNode node2 = new EvalHierarchyNode();
                 node2.title = result.getString("name");
                 node2.id = result.getString("id");
             
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
             
                 parentId = result2.getString("parent_id");
             } 
          }
          
          conn.close();
          
      } catch (IdUnusedException e) {
         System.err.println("IdUnusedException: " + e.getMessage());
      } catch (SQLException ex) {
		 System.err.println("SQLException: " + ex.getMessage());
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
       Connection conn;
       ResultSet result;
       //ResultSet result1;
       
       try {
           conn = dataSource.getConnection();
           Statement statement = conn.createStatement();
           
           // instead of just getting the eval groups from the db like below....
           //result1 = statement.executeQuery("SELECT * from HIER_PROVIDER_evalgroups where node_id = "+nodeId+";");
           //while (result1.next()) {
           //    results.add(result1.getString("group_id"));
               // log.info(result1.getString("group_id"));
           //}
           
           // ...we want to get the rule from the db for this node and then query for all the sites that match that rule
           
           result = statement.executeQuery("SELECT rule from HIER_PROVIDER_evalgrouprules where node_id = "+nodeId+";");
           //if (!result.first()) {
        	   //log.warn("No eval groups rules for this node.");
           //} else {
           while (result.next()) {
        	   String rule = result.getString("rule");
           
        	   SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
		  
        	   List<Site> sites = siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null);
        	   log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
		   
        	   for (Site site : sites) {
        		   log.warn("found site: "+site.getTitle()+" "+site.getId());
        		   results.add("/site/"+site.getId());
        	   }
           }
           
           conn.close();
           log.warn("Connection to hierarchy db closed.");
       } catch (SQLException ex) {
	       System.err.println("SQLException: " + ex.getMessage());
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
       Connection conn;
       ResultSet result;
       
       for (String nodeId : nodeIds) {
		   Set<String> results = new TreeSet<String>();
		   try {
			   conn = dataSource.getConnection();
			   Statement statement = conn.createStatement();
			   
			   //result = statement.executeQuery("SELECT * from HIER_PROVIDER_evalgroups where node_id = "+nodeId+";");
			   //while (result.next()) {
				//   results.add(result.getString("group_id"));
			   //}
			   
			   result = statement.executeQuery("SELECT rule from HIER_PROVIDER_evalgrouprules where node_id = "+nodeId+";");
               //if (!result.first()) {
            	 //  log.warn("No eval groups rules for this node.");
               //} else {
			   while (result.next()) {
                   String rule = result.getString("rule");
               
                   SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
		  
		           List<Site> sites = siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null);
		           log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
		   
		           for (Site site : sites) {
		               log.warn("found site: "+site.getTitle()+" "+site.getId());
		               results.add("/site/"+site.getId());
                   }
               }
			   
			   conn.close();
		   } catch (SQLException ex) {
			   System.err.println("SQLException: " + ex.getMessage());
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
       Connection conn;
       ResultSet result;
       
       for (String nodeId : nodeIds) {
           try {
               conn = dataSource.getConnection();
	       Statement statement = conn.createStatement();
			   
	       result = statement.executeQuery("SELECT rule from HIER_PROVIDER_evalgrouprules where node_id = "+nodeId+";");
	       List<Site> sites = new ArrayList<Site>();
	       while (result.next()) {
                   String rule = result.getString("rule");
           
                   SiteService siteService = org.sakaiproject.site.cover.SiteService.getInstance();
		   for (Site site : siteService.getSites(SelectionType.ANY, null, rule, null, SortType.TITLE_ASC, null)) {
		       sites.add(site);
                   }
		   log.warn("Rule: "+rule+" Number of sites found: "+sites.size());
			       
               }
	       map.put(nodeId, sites.size());
               
               conn.close();
           } catch (SQLException ex) {
			   System.err.println("SQLException: " + ex.getMessage());
		   } 
           
       }
   
       return map;
   }


}
