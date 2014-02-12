/**********************************************************************************
 *
 * $URL: https://source.sakaiproject.org/contrib/etudes/melete/tags/2.8.2/melete-app/src/java/org/etudes/tool/melete/AddModulePage.java $
 * $Id: AddModulePage.java 69815 2010-08-17 21:59:53Z rashmi@etudes.org $  
 ***********************************************************************************
 *
 * Copyright (c) 2008 Etudes, Inc.
 *
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007, 2008 Foothill College, ETUDES Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/
package org.etudes.tool.melete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.GregorianCalendar;
import java.util.Map;
import org.sakaiproject.util.ResourceLoader;

import org.etudes.api.app.melete.AccessGroupService;
import org.etudes.component.app.melete.AccessGroup;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.context.ExternalContext;

import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.tool.cover.ToolManager;

import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;
/**
 * @author Rashmi
 *
 * Rashmi - 10/24/06 - clean up comments and change logger.info to debug
 * Mallika - 2/9/07 - adding code to setmodule
 * Rashmi - 3/6/07 - remove section breadcrumbs
 *  */

public class AddModulePage extends ModulePage implements Serializable{
	
	private boolean siteHasGroups = false;
	private List<SelectItem> availableGroups = new ArrayList<SelectItem>();
	private List<AccessGroupService> accessGroups;
	private static final String DEFAULT_AVAILABLE_GROUP_ID = "-1";
	private String DEFAULT_AVAILABLE_GROUP_TITLE = "availableGroups_selectGroup";
	private String selectedAvailableGroup = DEFAULT_AVAILABLE_GROUP_ID;
	private static final String PARAM_GROUP_ID = "groupId";

	private static ResourceLoader bundle = new ResourceLoader("org.etudes.tool.melete.bundle.Messages");

    public AddModulePage(){
       	this.module = null;
    	setModuleShdates(null);
    	setModuleDateBean(null);
    	setFormName("AddModuleForm");
    }


   	/*
	 * set module to null to fix #19 and #20
	 * Rashmi -12/15
	 * revised Rashmi -12/20 to remove start and end dates
	 */
	public void setModuleNull()
	{
		this.module = null;
		setModuleShdates(null);
		resetModuleValues();
	}

	 /*
     * saves the module into database.
     * Valiation 1- validates user inputs for learning objectives and description.
     * Validation 2- user has agreed to the license.
     *
     * Revision on 11/15: - add code to initiate breadcrumps in add section page
     * 11/22 Rashmi -- get course id from session
     * 12/1  Rashmi -- license agre error message for copyright
     * validation 3 -- start date check Rashmi --12/6
     * validation 1 removed as now there is juxt one field description  Rashmi --12/8
     * revised to add license 4 to check if it has ben agreed upon or not Rashmi - 5/18
     */

    public String save()
	{
    	Date  d = new Date();
     	Date st = getModuleShdates().getStartDate();

        setSuccess(false);
        if(moduleService == null)
        	moduleService = getModuleService();

	     FacesContext context = FacesContext.getCurrentInstance();
	     ResourceLoader bundle = new ResourceLoader("org.etudes.tool.melete.bundle.Messages");

     	 //validation
     	module.setTitle(module.getTitle().trim());
     	module.setAccessGroups(new HashSet(getAccessGroups()));
     
     	// validation no 3
       	Date end = getModuleShdates().getEndDate();

 //  validation to limit year to 4 digits
        boolean dateResult = validateDates(context, bundle, st, end);
        if (dateResult == false) return "add_module";

	   	// get course info from sessionmap
	      Map sessionMap = context.getExternalContext().getSessionMap();
	      String courseId = (String)sessionMap.get("courseId");
	      String userId = (String)sessionMap.get("userId");

	     // actual insert
		try{
			if(module.getKeywords() != null)
			{
				module.setKeywords(module.getKeywords().trim());
			}
			if(module.getKeywords() == null || (module.getKeywords().length() == 0) )
				 	{
						module.setKeywords(module.getTitle());
					}

			moduleService.insertProperties(getModule(),getModuleShdates(),userId,courseId);
			// add module to session
			sessionMap.put("currModule",module);
		  //Track the event
		  EventTrackingService.post(EventTrackingService.newEvent("melete.module.new", ToolManager.getCurrentPlacement().getContext(), true));


		}catch(Exception ex)
		{
			//logger.error("mbusiness insert module failed:" + ex.toString());
			String errMsg = bundle.getString("add_module_fail");
			addMessage(context, "Error Message", errMsg, FacesMessage.SEVERITY_ERROR);
			return "add_module";
		}
		setSuccess(true);
		return "confirm_addmodule";
	 }

    /*
     * Called by the jsp page to redirect to add module sections page.
     * Revision -- Rashmi 12/21 resetting section value and setting SecBcPage values
     */
    public String addContentSections()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        ValueBinding binding =Util.getBinding("#{editSectionPage}");
        EditSectionPage editPage = (EditSectionPage) binding.getValue(context);
        editPage.resetSectionValues();
        editPage.setModule(module);
        editPage.addBlankSection();
        
       return "editmodulesections";
    }
    
    public boolean isSiteHasGroups(){
		Site currentSite = getCurrentSite();
		if(currentSite != null){
			return currentSite.hasGroups();
		}else{
			return false;
		}
	}

	public Site getCurrentSite(){
		try{
			return SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		} catch (IdUnusedException e) {
			logger.error(e);
		}
		return null;
	}		

	public void setSiteHasGroups(boolean siteHasGroups) {
		this.siteHasGroups = siteHasGroups;
	}
	
	public List<SelectItem> getAvailableGroups(){
		availableGroups = new ArrayList<SelectItem>();
		availableGroups.add(new SelectItem(DEFAULT_AVAILABLE_GROUP_ID, getResourceBundleString(DEFAULT_AVAILABLE_GROUP_TITLE)));

		Site currentSite = getCurrentSite();   
		if(currentSite.hasGroups()){

			Collection groups = currentSite.getGroups();

			groups = sortGroups(groups);

			for (Iterator groupIterator = groups.iterator(); groupIterator.hasNext();)
			{
				Group currentGroup = (Group) groupIterator.next();
				if(!isGroupSelected(currentGroup.getId())){
					availableGroups.add(new SelectItem(currentGroup.getId(), currentGroup.getTitle()));
				}				
			}		
		}

		return availableGroups;		
	}
	
	public static String getResourceBundleString(String key) 
    {
        return bundle.getString(key);
    }
	
	/**
	 * Takes groups defined and sorts them alphabetically by title
	 * so will be in some order when displayed on permission widget.
	 * 
	 * @param groups
	 * 			Collection of groups to be sorted
	 * 
	 * @return
	 * 		Collection of groups in sorted order
	 */
	private Collection sortGroups(Collection groups) {
		List sortGroupsList = new ArrayList();

		sortGroupsList.addAll(groups);

		final GroupComparator groupComparator = new GroupComparator("title", true);

		Collections.sort(sortGroupsList, groupComparator);

		groups.clear();

		groups.addAll(sortGroupsList);

		return groups;
	}


	private boolean isGroupSelected(String groupId){
		for (AccessGroupService accessGroup : getAccessGroups()) {
			if(accessGroup.getGroupId().equals(groupId)){
				return true;
			}
		}
		return false;
	}
	
	public String getSelectedAvailableGroup(){
		return selectedAvailableGroup;
	}

	public void setSelectedAvailableGroup(String selectedAvailableGroup){
		this.selectedAvailableGroup = selectedAvailableGroup;
	}

	public void processActionAddGroup(ValueChangeEvent event){
		String selectedGroup = (String) event.getNewValue();
		if(!DEFAULT_AVAILABLE_GROUP_ID.equals(selectedGroup) && !isGroupSelected(selectedGroup)){
			AccessGroupService aGS = new AccessGroup();
			aGS.setGroupId(selectedGroup);
			Site currentSite = getCurrentSite();   
			if(currentSite.hasGroups()){
				Collection groups = currentSite.getGroups();
				aGS.setGroupTitle(getGroupTitle(groups, selectedGroup));
			}			
			aGS.setModule(getModule());
			getAccessGroups().add(aGS);
			selectedAvailableGroup = DEFAULT_AVAILABLE_GROUP_ID;
		}
	}

	public String processActionRemoveGroup(){
		String groupId = getExternalParameterByKey(PARAM_GROUP_ID);
		if(groupId != null && !"".equals(PARAM_GROUP_ID)){
			for (AccessGroupService accessGroup : getAccessGroups()) {
				if(accessGroup.getGroupId().equals(groupId)){
					getAccessGroups().remove(accessGroup);
					break;
				}
			}
		}

		return null;
	}
	
	private String getExternalParameterByKey(String parameterId)
	  {    
	    ExternalContext context = FacesContext.getCurrentInstance()
	        .getExternalContext();
	    Map paramMap = context.getRequestParameterMap();
	    
	    return (String) paramMap.get(parameterId);    
	  }


	public List<AccessGroupService> getAccessGroups() {
		if(accessGroups == null){	
			accessGroups = new ArrayList<AccessGroupService>();
			Site currentSite = getCurrentSite();   
			if(currentSite.hasGroups()){

				Collection groups = currentSite.getGroups();
			
				if(module.getAccessGroups() != null){
					for (AccessGroupService accessGroup : module.getAccessGroups()) {
						//look for group, if it doesn't exist, groupTitle will be empty
						String groupTitle = getGroupTitle(groups, accessGroup.getGroupId());
						if(groupTitle != null && !"".equals(groupTitle)){
							accessGroup.setGroupTitle(groupTitle);
							accessGroups.add(accessGroup);
						}
					}
				}
			}
			
		}
		return accessGroups;
	}
	/**
	 * look for group title, if it doesn't exist, groupTitle will be empty
	 * 
	 * @param userGroups
	 * @param authorizedGroupId
	 * @return
	 */
	
	private String getGroupTitle(Collection userGroups, String authorizedGroupId){
		  if (userGroups==null || userGroups.isEmpty() 
				  || authorizedGroupId==null || authorizedGroupId.equals("")) {
			  return "";
		  }
		  Iterator userGroupsIter = userGroups.iterator();
		  while (userGroupsIter.hasNext()) {
			Group group = (Group) userGroupsIter.next();
			if (group.getId().equals(authorizedGroupId)) {
				return group.getTitle();
			}
		  }
		  return "";
	}


	public void setAccessGroups(List<AccessGroupService> accessGroups) {
		this.accessGroups = accessGroups;
	}
	
	public String getWinEncodeName(){
		String element = null;
		String pid = ToolManager.getCurrentPlacement().getId();
		if (pid != null){
			element = escapeJavascript("Main" + pid);
		}

		return element;
	}

	/**
	* Return a string based on value that is safe to place into a javascript / html identifier:
	* anything not alphanumeric change to 'x'. If the first character is not alphabetic, a
	* letter 'i' is prepended.
	* @param value The string to escape.
	* @return value fully escaped using javascript / html identifier rules.
	*/
	protected String escapeJavascript(String value){
		if (value == null || value == "") return "";
		try
		{
			StringBuffer buf = new StringBuffer();

			// prepend 'i' if first character is not a letter
			if(! java.lang.Character.isLetter(value.charAt(0)))
			{
				buf.append("i");
			}

			// change non-alphanumeric characters to 'x'
			for (int i = 0; i < value.length(); i++)
			{
				char c = value.charAt(i);
				if (! java.lang.Character.isLetterOrDigit(c))
				{
					buf.append("x");
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			return value;
		}

	}	// escapeJavascript	
 }
