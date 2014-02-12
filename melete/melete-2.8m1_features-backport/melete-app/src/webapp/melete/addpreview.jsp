<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 ***********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/melete/tags/2.8m1/melete-app/src/webapp/melete/addpreview.jsp $
 * $Id: addpreview.jsp 68210 2010-06-16 17:41:18Z rashmi@etudes.org $  
 ***********************************************************************************
 *
 * Copyright (c) 2008,2010 Etudes, Inc.
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
<sakai:view title="Modules: Preview Section" toolCssHref="rtbc004.css">

<%@include file="accesscheck.jsp" %>
<h:form id="previewAddForm" >
	<f:subview id="top">
		<jsp:include page="topnavbar.jsp"/> 
	</f:subview>		
	<div class="meletePortletToolBarMessage"><img src="images/preview.png" alt="" width="16" height="16" align="absmiddle"><h:outputText value="#{msgs.add_preview_previewing_section}" /></div>
    
     <table class="maintableCollapseWithBorder">
		<tr><td colspan="2" height="20"> <div  class="maintabledata5">&nbsp;</div></td></tr>	
		  <tr><td>
				<h:outputText value="#{msgs.add_preview_previewing_section_of}" styleClass="bold" /> <h:outputText value="#{addSectionPage.module.title}" styleClass="bold"/>
					</td></tr>	
				
				    <tr><td>
							<h:outputText value="Section " /> <h:outputText value=" : " /> 	<h:outputText id="sec1" value="#{addSectionPage.section.title}"></h:outputText>
				</td></tr>
				    <tr><td>
						<h:outputText id="sec3" value="#{msgs.add_preview_instructions}" rendered="#{addSectionPage.renderInstr}" styleClass="bold"></h:outputText> 
				</td></tr>
				<tr><td>
						<h:outputText id="sec2" value="#{addSectionPage.section.instr}" rendered="#{addSectionPage.renderInstr}"></h:outputText>
				</td></tr>
				<tr><td>
                <h:inputHidden id="contentType" value="#{addSectionPage.section.contentType}"/>
			   
			    <h:outputLink id="viewSectionLink"  value="#{addSectionPage.previewContentData}" target="_blank" rendered="#{(addSectionPage.shouldRenderLink || addSectionPage.shouldRenderUpload || addSectionPage.shouldRenderLTI) && addSectionPage.section.openWindow == true}">
                <h:outputText id="sectitleLink" 
                           value="#{addSectionPage.secResourceName}">
                </h:outputText>
                </h:outputLink>		
                
                 <h:outputText id="contentFrame" value="<iframe id=\"iframe1\" src=\"#{addSectionPage.previewContentData}\" style=\"visibility:visible\" scrolling= \"auto\" width=\"100%\" height=\"700\"
                 border=\"0\" frameborder= \"0\"></iframe>" rendered="#{(addSectionPage.shouldRenderUpload || addSectionPage.shouldRenderLink || addSectionPage.shouldRenderLTI) && addSectionPage.section.openWindow == false}" escape="false" />
	             
	          	<h:outputText id="contentTextFrame" rendered="#{addSectionPage.shouldRenderEditor && addSectionPage.contentWithHtml == true}" >
					<f:verbatim>
					<iframe id="iframe3" name="iframe3" src="${addSectionPage.previewContentData}" width="100%" height="700px" style="visibility:visible" scrolling= "auto" border="0" frameborder= "0">
					</iframe>
					</f:verbatim>
				</h:outputText>
				
				<!-- render typeEditor content without form tags -->
				<h:outputText value="#{addSectionPage.previewContentData}" escape="false" rendered="#{addSectionPage.shouldRenderEditor && addSectionPage.contentWithHtml == false}"/>
				</td></tr>
			
			<tr><td> 
				 <div class="actionBar" align="left">
					<h:commandButton id="FinishButton" action="list_auth_modules" value="#{msgs.im_finish}" accesskey="#{msgs.finish_access}" title="#{msgs.im_finish_text}" styleClass="BottomImgFinish"/>							
				</div></td>
              </tr>
			 <tr>
			 <td>
			 <table width="100%" border="0" cellpadding="3" cellspacing="0" >
   	         <tr>
	         <td align="center" class="meleteLicenseMsg center"><B>
	         <h:outputText id="lic1_val0" value="    " 
rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 0}"/> 
             <!--License code Copyright-->
   <h:outputText id="lic1_val1" value="#{msgs.add_preview_copyright}" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 1}"/> 

   <h:outputText id="lic1_val4" value="#{addSectionPage.section.sectionResource.resource.copyrightYear}" rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 1)&&(addSectionPage.section.sectionResource.resource.copyrightYear != addSectionPage.nullString))}"/> 
      <h:outputText id="lic1_val2" escape="false" value="<BR>#{addSectionPage.section.sectionResource.resource.copyrightOwner} " 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 1}"/> 

      <!--End license code Copyright-->
 <!--License code Public domain-->
   <h:outputText id="lic2_val1" value="#{msgs.add_preview_dedicated_to}" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 2}"/> 
   <h:outputLink value="#{addSectionPage.section.sectionResource.resource.ccLicenseUrl}" target="_blank" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 2}">
   <h:outputText id="lic2_val2" value="#{msgs.add_preview_public_domain}" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 2}"/> 
   </h:outputLink>
  <h:outputText id="lic2_val5" value="#{addSectionPage.section.sectionResource.resource.copyrightYear}" rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 2)&&(addSectionPage.section.sectionResource.resource.copyrightYear != addSectionPage.nullString))}"/> 
   <h:outputText id="lic2_val3" escape="false" value="<BR>#{addSectionPage.section.sectionResource.resource.copyrightOwner} "  

rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 2)&&(addSectionPage.section.sectionResource.resource.copyrightOwner != addSectionPage.nullString))}"/> 

 
      <!--End license code Public domain-->   
      <!--License code CC license-->
   <h:outputText id="lic3_val1" value="#{msgs.add_preview_licensed_under}" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 3}"/> 
   <h:outputLink value="#{addSectionPage.section.sectionResource.resource.ccLicenseUrl}" target="_blank" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 3}">
   <h:outputText id="lic3_val2" value="#{msgs.add_preview_creative_commons}" 

rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 3}"/>
   </h:outputLink> 
   
   <h:outputText id="lic3_val5" value="#{addSectionPage.section.sectionResource.resource.copyrightYear}" rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 3)&&(addSectionPage.section.sectionResource.resource.copyrightYear != addSectionPage.nullString))}"/> 
<h:outputText id="lic3_val3" escape="false" value="<BR>#{addSectionPage.section.sectionResource.resource.copyrightOwner} "  

rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 3)&&(addSectionPage.section.sectionResource.resource.copyrightOwner != addSectionPage.nullString))}"/> 

      <!--End license code CC license-->     	
	 
        <!--License code fairuse license-->
      <h:outputText id="lic4_val2" value="#{msgs.add_preview_fairuse}"  rendered="#{addSectionPage.section.sectionResource.resource.licenseCode == 4}"/>
    
   <h:outputText id="lic4_val5" value="#{addSectionPage.section.sectionResource.resource.copyrightYear}" rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 4)&&(addSectionPage.section.sectionResource.resource.copyrightYear != addSectionPage.nullString))}"/> 
	   <h:outputText id="lic4_val3" escape="false" value="<BR>#{addSectionPage.section.sectionResource.resource.copyrightOwner} "  

rendered="#{((addSectionPage.section.sectionResource.resource.licenseCode == 4)&&(addSectionPage.section.sectionResource.resource.copyrightOwner != addSectionPage.nullString))}"/> 
	
         <!--End license code fairuse license-->            
              </B></TD></TR>
	         </TABLE>
			 
			 </td></tr>
			</table>


<script type="text/javascript">
      window.onload=function(){
		var oIframe = document.getElementById("iframe3");
		 if(oIframe)
			 {
		        var oDoc = oIframe.contentWindow || oIframe.contentDocument;
			    if (oDoc.document) {
				oDoc = oDoc.document;	
				oIframe.style.height = oDoc.body.scrollHeight + 100 +"px";				       
			    } else oIframe.height = oDoc.body.offsetHeight + 100 ; 
			   	for (i=0; i < document.styleSheets.length; i++)
				{
				  var link = document.createElement("link");
				  //finish constructing the links
			       link.setAttribute("rel", "stylesheet");
			       link.setAttribute("type", "text/css");
				   //assign this new link the href of the parent one
			       link.setAttribute("href", document.styleSheets[i].href);
				   oDoc.body.appendChild(link); 
			    }
			    						
			  }
		setMainFrameHeight('<h:outputText value="#{meleteSiteAndUserInfo.winEncodeName}"/>');  
	}
 </script>
  </h:form>      
</sakai:view>
</f:view>
