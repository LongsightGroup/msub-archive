<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--
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
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<f:view>
<sakai:view title="Modules: Add Module Sections" toolCssHref="rtbc004.css">
<%@include file="accesscheck.jsp" %>

<%@ page import="javax.faces.application.FacesMessage, org.sakaiproject.util.ResourceLoader"%>

<% 
	ResourceLoader bundle = new ResourceLoader("org.etudes.tool.melete.bundle.Messages");
	String mensaje=bundle .getString("addmodulesections_uploading");
	String mensaje2=bundle .getString("addmodulesections_done");

%>

<style type="text/css">
<!--
.style1 {color: #FFFFFF}
-->
</style>

<script language="javascript1.2">

function showupload()
{
	var str=document.getElementById("AddSectionForm:contentType").value;
	var sferyxdisplay = document.getElementById("AddSectionForm:contentEditorView:sferyxDisplay");
	
	// for fckeditor display ME-1232
	if(str.match("notype") || !str.match("typeEditor"))
	{		

		if(document.getElementById("othereditor") != undefined && document.getElementById("othereditor") != null)
		  {
		      document.getElementById("othereditor").style.visibility="hidden";
		      document.getElementById("othereditor").style.display="none";
		  }
	}
	if(sferyxdisplay != undefined && str.match("typeEditor"))	
	 {	
		 var k1=document.getElementById("AddSectionForm:contentEditorView:contentTextArea").value;     
		 if(k1 != undefined && k1 != null) document.htmleditor.setContent(k1); //May use initialURLEncodedContent param instead
	 }		  
 }	

function transferEditordata()
{
    var str=document.getElementById("AddSectionForm:contentType").value;
    if (str == "typeEditor")
    {
	if(document.htmleditor!= undefined && document.htmleditor!= null)
	{
	  window.defaultStatus="Adding content....";
	  	var k =document.htmleditor.getContent();
	  		 if(document.getElementById("AddSectionForm:contentEditorView:contentTextArea") !=undefined &&
	  		   document.getElementById("AddSectionForm:contentEditorView:contentTextArea") != null)
			 {
				document.getElementById("AddSectionForm:contentEditorView:contentTextArea").value=k;
			}
		document.htmleditor.uploadMultipartContent(true);     
	}
	}
			
}
  
  function clearmessage()
 {
		   window.defaultStatus="<%=mensaje2%>";
  }


function contentChangeSubmit()
{
	   document.getElementById("AddSectionForm:contentChange").value = "true";
}
</script>

      <!-- This Begins the Main Text Area -->
	<h:form id="AddSectionForm" enctype="multipart/form-data">	
	<!-- top nav bar -->
		<f:subview id="top">
			<jsp:include page="topnavbar.jsp"/> 
		</f:subview>
		<div class="meletePortletToolBarMessage"><img src="images/document_add.gif" alt="" width="16" height="16" align="absmiddle"><h:outputText value="#{msgs.addmodulesections_adding_section}" /> </div>	
		<h:messages id="addsectionerror" layout="table" showDetail="true" showSummary="false" infoClass="BlueClass" errorClass="RedClass"/>

        <table class="maintableCollapseWithBorder">
          <tr>
            <td>
                     <table class="maintableCollapseWithNoBorder">
                                <tr>
						            <td colspan="2" height="20" class="maintabledata5">
											<h:commandLink id="TOCButton"  action="#{addSectionPage.cancel}"  immediate="true">
												<h:outputText id="toc" value="#{msgs.addmodulesections_TOC}" />
											</h:commandLink> &raquo; <h:outputText id="text11" value="#{addSectionPage.module.title}" />  &raquo; <h:outputText id="add_new_section" value="#{msgs.addmodulesections_add_new_section}" />
									</td>
						          </tr>
                                  <tr>
                                    <td class="col1" align="left" valign="top"><h:outputText id="text7" value="#{msgs.addmodulesections_section_title}" /><span class="required">*</span></td>
                                    <td class="col2" align="left" valign="top">
										<h:inputText id="title" value="#{addSectionPage.section.title}" size="45" required="true" styleClass="formtext" title="Title"/>
										
									</td>
                                  </tr>
                                  
                                  <tr> 
                                    <td class="col1" align="left" valign="top"><h:outputText id="text7b" rendered="#{addSectionPage.showSubtitles}" value="#{msgs.addmodulesections_section_subtitle}" /></td>
                                    <td class="col2" align="left" valign="top">
                                      <h:inputText id="subtitle" rendered="#{addSectionPage.showSubtitles}" value="#{addSectionPage.section.subtitle}" size="45" styleClass="formtext" /></td>
                                  </tr>
                                  
                                  <tr>
                                    <td class="col1" align="left" valign="top"><h:outputText id="text6" value="#{msgs.addmodulesections_author}" /></td>
                                    <td class="col2" align="left" valign="top"><h:outputText id="author" value="#{addSectionPage.author}" styleClass="formtext"/></td>
                                   </tr>
                                   <tr>
                                    <td class="col1" align="left" valign="top"><h:outputText id="text5" value="#{msgs.addmodulesections_instructions}" /></td>
                                    <td class="col2" align="left" valign="top">
									  <h:inputTextarea id="instr" cols="45" rows="5" value="#{addSectionPage.section.instr}" styleClass="formtext" >
											<f:validateLength maximum="250" minimum="1"/>
									</h:inputTextarea>													
									</td>
                                  </tr>
                                <tr>
                                    <td class="col1" align="left" valign="top"> <h:outputText id="modality" value="#{msgs.addmodulesections_modality}" /><span class="required">*</span></td>
                                    <td class="col2" align="left" valign="top"><h:outputText id="text4" value="#{msgs.addmodulesections_message1} "/>
									</td>
									  </tr>	
								  <tr>
								  <td class="col1">&nbsp;</td>
                                    <td class="col2" valign="top">
                        			<h:selectBooleanCheckbox id="contentext" title="textualContent" value="#{addSectionPage.section.textualContent}">
									</h:selectBooleanCheckbox>
									<h:outputText  id="text12" value="#{msgs.addmodulesections_textual_content}" />
									</td>
									  </tr>	
								  <tr>
								    <td class="col1">&nbsp;</td>
                                    <td class="col2" valign="top">									
									<h:selectBooleanCheckbox id="contentvideo" title="videoContent" value="#{addSectionPage.section.videoContent}" >
									</h:selectBooleanCheckbox>
									<h:outputText  id="text13" value="#{msgs.addmodulesections_visual_content}" />
									</td>
									  </tr>	
								  <tr>
								    <td class="col1">&nbsp;</td>
                                    <td class="col2" valign="top">
									<h:selectBooleanCheckbox  id="contentaudio" title="audioContent" value="#{addSectionPage.section.audioContent}" >
									</h:selectBooleanCheckbox>
									<h:outputText id="text14" value="#{msgs.addmodulesections_auditory_content}"/>			
										</td>
									  </tr>	
									 
								  <tr>
								  	  <td class="col1" align="left" valign="middle"><h:outputText id="text15" value="#{msgs.addmodulesections_content_type}" /></td>
                                 	  <td class="col2"> 
										   <h:inputHidden id="contentChange" value=""/>								  
												  <h:selectOneMenu id="contentType" value="#{addSectionPage.section.contentType}" valueChangeListener="#{addSectionPage.showHideContent}" onchange="contentChangeSubmit();this.form.submit();"  >
													<f:selectItems value="#{addSectionPage.allContentTypes}" />
											 </h:selectOneMenu>
										</td>
										</tr>
										<tr> 
										 <td colspan="2">
											 <f:subview id="ContentLinkView" rendered="#{addSectionPage.shouldRenderLink}">
												<jsp:include page="ContentLinkView.jsp"/> 
											</f:subview>											
				                           				<f:subview id="ContentUploadView" rendered="#{addSectionPage.shouldRenderUpload}">
												<jsp:include page="ContentUploadView.jsp"/> 
											</f:subview>
											 <f:subview id="ContentLTIView" rendered="#{addSectionPage.shouldRenderLTI}">
												<jsp:include page="ContentLTIView.jsp"/> 
											</f:subview>	
									     </td>
									     </tr>
									     <tr>			
										 <td colspan="2"> 
												
											<f:subview id="contentEditorView" rendered="#{addSectionPage.shouldRenderEditor && authorPreferences.shouldRenderSferyx}">
														<jsp:include page="contentSferyxEditor.jsp?mode=Add"/> 
													<h:inputHidden id="contentTextArea" value="#{addSectionPage.contentEditor}" />
													
													 <h:inputHidden id="sferyxDisplay" value="#{authorPreferences.shouldRenderSferyx}" />
											</f:subview>																																
											<div id="othereditor"><sakai:inputRichText  id="otherMeletecontentEditor" value="#{addSectionPage.contentEditor}"  rows="50" cols="90" width="700" rendered="#{addSectionPage.renderOtherEditor}" collectionBase="#{addSectionPage.FCK_CollId}" /></div>
										</td>
										</tr>
										<tr>
										<td colspan="2">
										<f:subview id="ResourcePropertiesPanel" rendered="#{!addSectionPage.shouldRenderNotype}">
											<jsp:include page="sec_resourcePropertiesPanel.jsp"/>
										</f:subview>
									</td>	
									</tr>
									</table>	 
									
	          <div class="actionBar" align="left">	
				<h:commandButton id="submitsave" action="#{addSectionPage.save}" rendered="#{addSectionPage.shouldRenderEditor}" onclick="transferEditordata()" value="#{msgs.im_add_button}"  accesskey="#{msgs.add_access}" title="#{msgs.im_add_button_text}" styleClass="BottomImgAdd"/>
				<h:commandButton id="submitsave1" action="#{addSectionPage.save}" rendered="#{addSectionPage.shouldRenderUpload}" onclick="clearmessage()" value="#{msgs.im_add_button}"  accesskey="#{msgs.add_access}" title="#{msgs.im_add_button_text}" styleClass="BottomImgAdd"/>
				<h:commandButton id="submitsave2" action="#{addSectionPage.save}" rendered="#{!addSectionPage.shouldRenderEditor && !addSectionPage.shouldRenderUpload}" value="#{msgs.im_add_button}"  accesskey="#{msgs.add_access}" title="#{msgs.im_add_button_text}" styleClass="BottomImgAdd"/>
				<h:commandButton id="cancelButton" immediate="true" action="#{addSectionPage.cancel}" value="#{msgs.im_cancel}"  onclick="clearmessage()" accesskey="#{msgs.cancel_access}" title="#{msgs.im_cancel_text}" styleClass="BottomImgCancel"/>
			 </div>
			</td>
          </tr>
        </table>
		<p ><span class="required">*</span>&nbsp;<h:outputText value="#{msgs.addmodulesections_required}" /></p>
	
</h:form>
</sakai:view>
<script type="text/javascript">
	 showupload();	
</script>
</f:view>

