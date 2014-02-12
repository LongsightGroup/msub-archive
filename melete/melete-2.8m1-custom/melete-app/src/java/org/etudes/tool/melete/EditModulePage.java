/**********************************************************************************
 *
 * $URL$
 * $Id$
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.component.app.melete.*;

import java.util.*;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.sakaiproject.util.ResourceLoader;

import org.etudes.component.app.melete.ModuleDateBean;
import org.etudes.api.app.melete.AccessGroupService;
import org.etudes.api.app.melete.SectionObjService;
import org.etudes.api.app.melete.exception.MeleteException;
//import org.sakaiproject.jsf.ToolBean;

import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.exception.IdUnusedException;

/**
 * @author Mallika
 *
 * Edit Module Page is the backing bean for the page edit_module.jsp.
 * It also connects to other jsp pages like cclicenseform.jsp and
 * publicdomainform.jsp and license_results.jsp
 * 8/1/06 - Mallika - Removing IAgree code
 * 28/12/06 - Rashmi - remove license validations and clean up license code
 * 2/6/07 - Rashmi - set section breadcrumbs to text only for add section page
 * 3/6/07- Rashmi - remove section breadcrumbs
 *   */

public class EditModulePage extends ModulePage implements Serializable/*, ToolBean*/ {

   /** Dependency:  The logging service. */
	protected Log logger = LogFactory.getLog(EditModulePage.class);
   private boolean callFromAddContent = false;
   private boolean showLicenseFlag = true;
   private boolean hasSections = false;
   private SectionObjService firstSection = null;
   private boolean siteHasGroups = false;
   private List<SelectItem> availableGroups = new ArrayList<SelectItem>();
   private List<AccessGroupService> accessGroups;
   private static final String DEFAULT_AVAILABLE_GROUP_ID = "-1";
   private String DEFAULT_AVAILABLE_GROUP_TITLE = "availableGroups_selectGroup";
   private String selectedAvailableGroup = DEFAULT_AVAILABLE_GROUP_ID;
   private static final String PARAM_GROUP_ID = "groupId";

   private static ResourceLoader bundle = new ResourceLoader("org.etudes.tool.melete.bundle.Messages");


    public EditModulePage(){
      setFormName("EditModuleForm");
      setSuccess(false);

    }


	/*
	 * Rashmi 12/21
	 * modification details
	 *
	 */
	public void setModification()
	{
		  FacesContext context = FacesContext.getCurrentInstance();
	      Map sessionMap = context.getExternalContext().getSessionMap();
	      module.setModifiedByFname((String)sessionMap.get("firstName"));
	      module.setModifiedByLname((String)sessionMap.get("lastName"));
	      module.setModificationDate(new Date());
	      module.setAccessGroups(new HashSet(getAccessGroups()));
	}
	 public boolean  getShowLicenseFlag() {
	  	return showLicenseFlag;
	  }

	  public void setShowLicenseFlag(boolean  showLicenseFlag) {
	  	this.showLicenseFlag = showLicenseFlag;
	  }
	/*
	 * Rashmi -- 12/21 revised to put correct validations
	 *
	 */
	public String savehere()
	{
    	String errMsg = "";
        setSuccess(false);
        if(moduleService == null)
        	moduleService = getModuleService();

	     FacesContext context = FacesContext.getCurrentInstance();     	 
     	 Map sessionMap = context.getExternalContext().getSessionMap();

	    // rashmi added validations start
//     	validation
      	module.setTitle(module.getTitle().trim());

      	// validation no 3
      	Date  d = new Date();
      	Date st = getModuleShdates().getStartDate();
      	Date end = getModuleShdates().getEndDate();
      	
//       validation no 4 b
      	 boolean dateResult = validateDates(context, bundle, st, end);
         if (dateResult == false) return "failure";

    	/*if ((end != null) && (st != null))
		{
			if (end.compareTo(st) <= 0)
			{
				errMsg = "";
				errMsg = bundle.getString("end_date_before_start");
				FacesMessage msg = new FacesMessage(errMsg);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(null, msg);
				return "failure";
			}
		}*/

 	    // rashmi added validations end

	     // actual update
		try{
			setModification();
			if(module.getKeywords() != null)
			{
				module.setKeywords(module.getKeywords().trim());
			}
			if(module.getKeywords() == null || (module.getKeywords().length() == 0) )
				 	{
						module.setKeywords(module.getTitle());
					}
			ModuleDateBean mdbean = new ModuleDateBean();
			mdbean.setModuleId(getModule().getModuleId().intValue());
			mdbean.setModule((Module)getModule());
			mdbean.setModuleShdate((ModuleShdates)getModuleShdates());
			mdbean.setDateFlag(false);
	      	
			ArrayList mdbeanList = new ArrayList();
			mdbeanList.add(mdbean);
			moduleService.updateProperties(mdbeanList, (String)sessionMap.get("courseId"));

			// add module to session
			sessionMap.put("currModule",module);

			//Track the event
			EventTrackingService.post(EventTrackingService.newEvent("melete.module.edit", ToolManager.getCurrentPlacement().getContext(), true));


		}
		catch(MeleteException me)
		{
			logger.debug("show error message for"+me.toString()+me.getMessage()+",");
			errMsg = bundle.getString(me.getMessage());
			addMessage(context, "Error Message", errMsg, FacesMessage.SEVERITY_ERROR);
			return "failure";
		}
		catch(Exception ex)
		{
			errMsg = bundle.getString("edit_module_fail");
			addMessage(context, "Error Message", errMsg, FacesMessage.SEVERITY_ERROR);
			return "failure";
		}
		/*if (callFromAddContent == false)
		{
		  String msg="";
		  msg = bundle.getString("edit_module_success");
		  addMessage(context, "Info Message", msg, FacesMessage.SEVERITY_INFO);
		}*/
		setSuccess(true);
		return "success";
	 }

	/*
	 *
	 */
	public String save()
	{
		if(!savehere().equals("failure"))
		{
    		callFromAddContent = false;
		    setSuccess(true);
		}
    	else
    	{
    		callFromAddContent = false;

    	}
		return "edit_module";
	}

    /*
     * Revision by rashmi on 12/20
     * in sbcPage settings ..change from getModule() to module
     * add section page instance to reset values
     */
    public String addContentSections()
    {
    	String errMsg = "";

        if(!getSuccess())
        {
        	callFromAddContent = true;
        	if(!savehere().equals("failure"))
    		{
        		callFromAddContent = false;
    		    setSuccess(true);
    		}
        	else
        	{
        		callFromAddContent = false;
        		return "edit_module";
        	}
        }
        //Revision -- 12/20 - to remove retaintion of values
        FacesContext context = FacesContext.getCurrentInstance();
        ValueBinding binding =Util.getBinding("#{addSectionPage}");
        AddSectionPage addPage = (AddSectionPage) binding.getValue(context);
        addPage.setSection(null);
        addPage.resetSectionValues();
        addPage.setModule(module);

       return "addmodulesections";
    }

    public String gotoTOC()
	{
    	String errMsg = "";

        if(!getSuccess())
        {
        	callFromAddContent = true;
        	if(!savehere().equals("failure"))
    		{
        		callFromAddContent = false;
    		    setSuccess(true);
    		}
        	else
        	{
        		callFromAddContent = false;
        		return "edit_module";
        	}
        }
		FacesContext context = FacesContext.getCurrentInstance();
        ValueBinding binding =Util.getBinding("#{listAuthModulesPage}");
        ListAuthModulesPage listPage = (ListAuthModulesPage) binding.getValue(context);
        listPage.resetValues();
        listPage.setModuleDateBeans(null);
		return "list_auth_modules";
	}

    /*
     * Revised by Rashmi -- 12/21 to fix bug#189
     * reset values
     */
    public void setEditInfo(ModuleDateBean mdbean){
    	resetModuleValues();
    	setModuleDateBean(mdbean);
    	setModule(mdbean.getModule());
    	setModuleShdates(mdbean.getModuleShdate());
    	if((mdbean.getSectionBeans() == null)||(mdbean.getSectionBeans().isEmpty()))
    	{
    		setFirstSection(null);
    		setHasSections(false);
    	}
    	else{
    		setHasSections(true);
    		setFirstSection(((SectionBean)mdbean.getSectionBeans().get(0)).getSection());
    	}

   	}

    public boolean isHasSections()
    {
    	return hasSections;
    }
	/**
	 * @param hasSections the hasSections to set
	 */
	public void setHasSections(boolean hasSections)
	{
		this.hasSections = hasSections;
	}

	public String editSection()
	{
		callFromAddContent = false;
		 if(!getSuccess())
	        {
	        	if(!savehere().equals("failure"))
	    	 		    setSuccess(true);
	    	   	else return "edit_module";
	        }

	        FacesContext context = FacesContext.getCurrentInstance();
	        ValueBinding binding =Util.getBinding("#{editSectionPage}");
	        EditSectionPage editPage = (EditSectionPage) binding.getValue(context);
	        Map sessionMap = context.getExternalContext().getSessionMap();
			sessionMap.put("currModule", module);
			editPage.setEditInfo(firstSection);

		return "editmodulesections";
	}
	/**
	 * @param firstSection the firstSection to set
	 */
	public void setFirstSection(SectionObjService firstSection)
	{
		this.firstSection = firstSection;
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
