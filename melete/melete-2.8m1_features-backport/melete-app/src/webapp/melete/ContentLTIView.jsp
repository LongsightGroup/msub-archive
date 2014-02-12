<!--
 ***********************************************************************************
 *
 * Copyright (c) 2008,2009,2010 Etudes, Inc.
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

<h:panelGrid id="LinkPanel2" columns="2" width="100%" columnClasses="col1,col2" border="0" rendered="#{addSectionPage.shouldRenderLTI}">
  <h:column><h:outputText id="linkText1" value="#{msgs.contentlinkviewlti_link}"/>    
  </h:column>
  <h:column>
      <h:commandLink id="serverViewButton"  action="#{addSectionPage.gotoServerLTIView}" >
        <h:graphicImage id="replaceImg2" value="images/replace2.gif" styleClass="AuthImgClass"/>
        <h:outputText value="#{msgs.contentuploadview_select}" />
      </h:commandLink>
  </h:column>
  <h:column/>
  <h:column>
    <h:outputLink id="showResourceLTI" value="#{addSectionPage.currLTIUrl}" target="_blank" title="Section Resource" styleClass="a1" rendered="#{addSectionPage.displayCurrLTI != null}">
       <h:outputText id="editlinkText3" value="#{addSectionPage.displayCurrLTI}" />
    </h:outputLink>
    <h:outputText id="linkText2" value="#{msgs.contentlinkviewlti_nofile}" rendered="#{addSectionPage.displayCurrLTI == null}" styleClass="bold"/>
  </h:column>
  <h:column/>
  <h:column>
       <h:selectBooleanCheckbox id="windowopen" title="openWindow" value="#{addSectionPage.section.openWindow}" >
       </h:selectBooleanCheckbox>
        <h:outputText id="editlinkText_8" value="#{msgs.editcontentlinkserverview_openwindow}" />
  </h:column>
</h:panelGrid>

