<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 ***********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/melete/tags/2.8m1/melete-app/src/webapp/melete/confirm_addmodulesection.jsp $
 * $Id: confirm_addmodulesection.jsp 68182 2010-06-15 20:18:18Z mallika@etudes.org $  
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
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:view>
<sakai:view title="Modules: Add Section Confirmation" toolCssHref="rtbc004.css">
<%@include file="accesscheck.jsp" %>
   <h:form id="AddSectionConfirmForm">
    <f:subview id="top">
		<jsp:include page="topnavbar.jsp"/> 
	</f:subview>	
	<div class="meletePortletToolBarMessage"><img src="images/check.gif" alt="" width="16" height="16" align="absbottom" border="0"><h:outputText value="#{msgs.confirm_addmodulesection_confirming_section}" /></div>
	  <!-- This Begins the Main Text Area -->
      <table class="maintableCollapseWithBorder">
           
        <tr>
          <td class="maintabledata3">
		
		  <table class="maintableCollapseWithNoBorder">
		  	<tr><td height="20" class="maintabledata5">&nbsp;</td></tr>
            <tr>
                <td valign="top">
                   <table class="deleteConfirmTable" border="1">
                    <tr class="maintabledata3">
                      <td valign="top"><img src="images/right_check.gif" width="24" height="24" align="absbottom" alt="Confirmation Signal" border="0"></td>
                      <td align="left"><h:outputText value="#{msgs.confirm_addmodulesection_you_have_succes}" /><br>
                        <br> <h:outputText value="#{addSectionPage.section.title}" styleClass="bold" />                 
					  <p align="left"><h:outputText value="#{msgs.confirm_addmodulesection_you_may_continue}"/> <BR>
                      </p></td>
                    </tr>
                  </table></td>
              </tr>
			  <tr>
                <td>         
                 <div class="actionBar" align="left">				
					<h:commandButton id="preview"  action="#{addSectionPage.previewFromAdd}" value="#{msgs.im_preview_section}" accesskey="#{msgs.preview_access}" title="#{msgs.im_preview_section_text}" styleClass="BottomImgPreview"/>
					<h:commandButton id="saveAddAnotherbutton"  action="#{addSectionPage.saveAndAddAnotherSection}" value="#{msgs.im_add_another_section}"  accesskey="#{msgs.add_access}" title="#{msgs.im_add_another_section_text}" styleClass="BottomImgAdd"/>
					<h:commandButton id="FinishButton" action="list_auth_modules" value="#{msgs.im_finish}" accesskey="#{msgs.finish_access}" title="#{msgs.im_finish_text}" styleClass="BottomImgFinish"/>
				</div></td>
             </tr>			 
            </table>
          </td>
        </tr>
      </table>   
   </h:form>      
  <!-- This Ends the Main Text Area --> 
</sakai:view>
</f:view>
