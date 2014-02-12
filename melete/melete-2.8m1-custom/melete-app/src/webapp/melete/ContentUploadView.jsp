<!--
 ***********************************************************************************
 * $URL$
 * $Id$  
 ***********************************************************************************
 *
 * Copyright (c) 2008,2009,2010 Etudes, Inc.
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
<%@include file="accesscheck.jsp" %>

	<h:panelGrid id="uploadView1" columns="2" columnClasses="col1,col2" width="100%">
		<h:column>
			<h:outputText id="uploadText1" value="#{msgs.contentuploadview_upload}" rendered="#{addSectionPage.shouldRenderUpload}"/>				

		</h:column>
		<h:column>	
			<h:commandLink id="serverViewButton"  action="#{addSectionPage.gotoServerView}" >
					<h:graphicImage id="replaceImg2" value="images/replace2.gif" styleClass="AuthImgClass"/>
					<h:outputText value="#{msgs.contentuploadview_select}" />
             </h:commandLink>
        </h:column>
        <h:column/>
        <h:column> 
        	<h:outputText id="uploadText2" value="#{addSectionPage.uploadFileName}" rendered="#{addSectionPage.uploadFileName != null}" styleClass="bold"/>	
			<h:outputText id="uploadText3" value="#{msgs.contentuploadview_nofile}" rendered="#{addSectionPage.uploadFileName == null}" styleClass="bold"/>	
        </h:column>
        <h:column/>
        <h:column>     	
             <h:selectBooleanCheckbox id="windowopen" title="openWindow" value="#{addSectionPage.section.openWindow}" rendered="#{addSectionPage.shouldRenderUpload}">
		  </h:selectBooleanCheckbox>
		  <h:outputText id="editlinkText_8" value="#{msgs.editcontentlinkserverview_openwindow}" rendered="#{addSectionPage.shouldRenderUpload}"/>
        </h:column>      
</h:panelGrid>
		
		
	