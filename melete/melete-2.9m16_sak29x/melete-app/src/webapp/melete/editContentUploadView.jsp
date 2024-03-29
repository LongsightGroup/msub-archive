<!--
 ***********************************************************************************
 * $URL$
 * $Id$  
 ***********************************************************************************
 *
 * Copyright (c) 2008,2009,2010,2011 Etudes, Inc.
 *
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007, 2008 Foothill College, ETUDES Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************
-->
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%@include file="accesscheck.jsp" %>

<h:panelGrid id="uploadView" columns="2" columnClasses="col1,col2" width="100%" border="0" rendered="#{editSectionPage.shouldRenderUpload}">
	<h:column>
		<h:outputText id="edituploadText1" value="#{msgs.editcontentuploadview_file_uploaded}" />
	</h:column>
	<h:column>			
		<h:commandLink id="serverViewButton" actionListener="#{editSectionPage.setServerFileListener}" >
			<f:param name="sectionId" value="#{editSectionPage.editId}" />
			<h:graphicImage id="replaceImg2" value="/images/replace2.gif" styleClass="AuthImgClass"/>
			<h:outputText value="#{msgs.editcontentuploadview_replace}"/>
	    </h:commandLink>		
	</h:column>
	<h:column/>		
	<h:column> 
	 	<h:outputText id="edituploadText2" value="#{editSectionPage.secResourceName}" rendered="#{editSectionPage.secResourceName != null}" styleClass="bold"/>	
		<h:outputText id="edituploadText3" value="#{msgs.editcontentuploadview_nofile}" rendered="#{editSectionPage.secResourceName == null}" styleClass="bold"/>
	</h:column>
	<h:column/>		
	 <h:column>     	
        <h:selectBooleanCheckbox id="windowopen" title="openWindow" value="#{editSectionPage.section.openWindow}" rendered="#{editSectionPage.shouldRenderUpload}">
		  </h:selectBooleanCheckbox>
		  <h:outputText id="editlinkText_8" value="#{msgs.editcontentlinkserverview_openwindow}" rendered="#{editSectionPage.shouldRenderUpload}"/>
        </h:column>     
  </h:panelGrid>
	
