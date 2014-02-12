package org.etudes.api.app.melete;

public interface AccessGroupService {
	
	public Long getAccessId();
	
	public void setAccessId(Long accessId);
	
	public org.etudes.api.app.melete.ModuleObjService getModule();
	
	public void setModule(org.etudes.api.app.melete.ModuleObjService module);
	
	public String getGroupId();
	
	public void setGroupId(String groupId);
	
	public String getGroupTitle();
	
	public void setGroupTitle(String groupTitle);
}
