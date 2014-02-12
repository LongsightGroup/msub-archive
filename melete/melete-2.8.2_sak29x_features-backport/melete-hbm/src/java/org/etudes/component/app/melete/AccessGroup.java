package org.etudes.component.app.melete;

import java.io.Serializable;

import org.etudes.api.app.melete.AccessGroupService;


public class AccessGroup implements Serializable, AccessGroupService{
	private Long accessId;
	private org.etudes.api.app.melete.ModuleObjService module;
	private String groupId;
	//group title isn't stored in the DB, it's just used 
	private String groupTitle;
	
	public Long getAccessId() {
		return accessId;
	}
	public void setAccessId(Long accessId) {
		this.accessId = accessId;
	}
	public org.etudes.api.app.melete.ModuleObjService getModule() {
		return module;
	}
	public void setModule(org.etudes.api.app.melete.ModuleObjService module) {
		this.module = module;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * Group title isn't persistent and needs to be set
	 * @return
	 */
	public String getGroupTitle() {
		return groupTitle;
	}
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

}
