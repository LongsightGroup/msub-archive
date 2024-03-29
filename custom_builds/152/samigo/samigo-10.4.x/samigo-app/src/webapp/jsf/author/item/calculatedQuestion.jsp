<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
* $Id: matching.jsp 59563 2009-04-02 15:18:05Z arwhyte@umich.edu $
<%--
***********************************************************************************
*
* Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.osedu.org/licenses/ECL-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License. 
*
**********************************************************************************/
--%>
-->
<%-- "checked in wysiwyg code but disabled, added in lydia's changes between 1.9 and 1.10" --%>
<%-- 
saveButton style is used only on save buttons to attach a click handler, so that javascript
can throw a confirm dialog box if nothing has been changed.  saveButton style doesn't do any
styling.

changeWatch style is used only on fields that should be watched for changes, with the saveButton 
above.  If a changeWatch field is changed, the saveButton buttons will not trigger a 
confirmation dialog
--%>
<f:view>
<html xmlns="http://www.w3.org/1999/xhtml">
<head><%= request.getAttribute("html.head") %>
	<title><h:outputText value="#{authorMessages.item_display_author}"/></title>
	<!-- HTMLAREA -->
	<samigo:stylesheet path="/htmlarea/htmlarea.css"/>
	<samigo:script path="/js/jquery-1.3.2.min.js"/>
	<samigo:script path="/htmlarea/htmlarea.js"/>
	<samigo:script path="/htmlarea/lang/en.js"/>
	<samigo:script path="/htmlarea/dialog.js"/>
	<samigo:script path="/htmlarea/popupwin.js"/>
	<samigo:script path="/htmlarea/popups/popup.js"/>
	<samigo:script path="/htmlarea/navigo_js/navigo_editor.js"/>
	<samigo:script path="/jsf/widget/wysiwyg/samigo/wysiwyg.js"/>
	<!-- AUTHORING -->
	<samigo:script path="/js/authoring.js"/>
	<script type="text/javascript">
	$(document).ready(function() {
		initCalcQuestion();
	});
	</script>
</head>
<%-- unfortunately have to use a scriptlet here --%>
<body onload="<%= request.getAttribute("html.body.onload") %>">

<div class="portletBody">
<!-- content... -->
<!-- FORM -->

<!-- HEADING -->
<%@ include file="/jsf/author/item/itemHeadings.jsp" %>
<h:form id="itemForm">
	<p class="act">
		<h:commandButton 
				rendered="#{itemauthor.target=='assessment'}" 
				value="#{commonMessages.action_save}" 
				action="#{itemauthor.currentItem.getOutcome}" 
				styleClass="active saveButton">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
	  	</h:commandButton>
	  	<h:commandButton 
	  			rendered="#{itemauthor.target=='questionpool'}" 
	  			value="#{commonMessages.action_save}" 
	  			action="#{itemauthor.currentItem.getPoolOutcome}" 
	  			styleClass="active saveButton">
	    	<f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
	  	</h:commandButton>
	
	  	<h:commandButton 
	  			rendered="#{itemauthor.target=='assessment'}" 
	  			value="#{commonMessages.cancel_action}" 
	  			action="editAssessment" 
	  			immediate="true">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ResetItemAttachmentListener" />
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditAssessmentListener" />
	  	</h:commandButton>
	 	<h:commandButton 
                rendered="#{itemauthor.target=='questionpool'}" 
	 			value="#{commonMessages.cancel_action}" 
	 			action="editPool" 
	 			immediate="true">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ResetItemAttachmentListener" />
	 	</h:commandButton>
	</p>

  	<!-- QUESTION PROPERTIES -->
  	<!-- this is for creating multiple choice questions -->
	<%-- kludge: we add in 1 useless textarea, the 1st does not seem to work --%>
	<div style="display:none">
		<h:inputTextarea id="ed0" cols="10" rows="10" value="            " />
	</div>

	<!-- 1 POINTS -->
	<div class="tier2">
		<div class="shorttext"> <h:outputLabel value="#{authorMessages.answer_point_value}" />
	    	<h:inputText id="answerptr" value="#{itemauthor.currentItem.itemScore}" required="true">
				<f:validateDoubleRange/>
			</h:inputText>
			<br/>
			<h:message for="answerptr" styleClass="validate"/>
	  	</div>
		<br/>
	</div>

    <%-- 2 QUESTION TEXT --%>
    <div class="longtext"> <h:outputLabel value="#{authorMessages.q_text}" />
    <br/></div>
	<div class="tier2">
	
	  	<h:outputText value="#{authorMessages.calc_question_define_vars}" /><br/>
	  	<h:outputText value="#{authorMessages.calc_question_answer_expression}" /><br/>
	  	<h:outputText value="#{authorMessages.calc_question_answer_variance}" /><br/><br/>
	
		<h:outputLink onclick="$('#calcQInstructions').toggle();" value="#">
			<h:outputText value="#{authorMessages.calc_question_hideshow}"/> 
		</h:outputLink>
		<div id="calcQInstructions" style='display:none;'>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_general_instructsion_label}" /></div>
			<div class="tier2">
				<h:outputText value="#{authorMessages.calc_question_general_instructions1 }" /><br/>
				<h:outputText value="#{authorMessages.calc_question_general_instructions2 }" /><br/>
				<h:outputText value="#{authorMessages.calc_question_general_instructions3 }" />
			</div>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_walkthrough_label}" /></div>
			<div class="tier2">
				<ol>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough1 }" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough2 }" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough3 }" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough4 }" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough5 }" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_walkthrough6 }" /></li>
				</ol>
			</div>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_var_label}" /></div>
			<div class="tier2">
				<h:outputText value="#{authorMessages.calc_question_define_vars}" />
			</div>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_example_label}" /></div>
			<div class="tier2">
				<h:outputText value="#{authorMessages.calc_question_example1}" />
			</div>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_formula_label}" /></div>
			<div class="tier2">
				<h:outputText value="#{authorMessages.calc_question_answer_expression}" />
			</div>
			<div class="longtext"><h:outputLabel value="#{authorMessages.calc_question_example_label}" /></div>
			<div class="tier2">
				<h:outputText value="#{authorMessages.calc_question_example2}" />
			</div>
			<div class="longtext">Additional Information</div>
			<div class="tier2">
				<ul>
					<li><h:outputText value="#{authorMessages.calc_question_answer_variance}" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_answer_decimal}" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_operators}" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_functions}" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_constants}" /></li>
					<li><h:outputText value="#{authorMessages.calc_question_unique_names}"/></li>
				</ul>
			</div>
		</div>
	  
	  	<br/>
	  
	  <!-- WYSIWYG -->
	  	<h:panelGrid>
	   		<samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.instruction}" hasToggle="yes">
	     		<f:validateLength maximum="60000"/>
	   		</samigo:wysiwyg>
	
	  	</h:panelGrid>
	  
	  	<h:commandButton rendered="#{itemauthor.target=='assessment' || itemauthor.target == 'questionpool'}"
	  			value="#{authorMessages.calc_question_extract_button}" 
	  			action="calculatedQuestion" 
	  			styleClass="active">
	  		<f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.CalculatedQuestionExtractListener" />
		</h:commandButton>
	</div>

  	<!-- 2a ATTACHMENTS -->
  	<%@ include file="/jsf/author/item/attachment.jsp" %>

  	<!-- 3 ANSWER -->
	<!-- display variables -->
	<div class="longtext"> <h:outputLabel value="#{authorMessages.calc_question_var_label} " /></div>
	<div class="tier2">
		<h:dataTable cellpadding="0" cellspacing="0" styleClass="listHier" id="pairs" 
				value="#{itemauthor.currentItem.calculatedQuestion.variablesList}" var="variable">
	      
	    	<h:column>
	        	<f:facet name="header">          
	          		<h:outputText value="" />
	        	</f:facet>
	
	          	<h:outputText value="" />
	      	</h:column>
	
	      	<h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_varname_col}"  />
	        	</f:facet>
	          	<h:outputText escape="false" value="#{variable.name}" rendered="#{variable.active }" />
	          	<h:outputText escape="false" value="#{variable.name}" rendered="#{!variable.active }" 
	          			styleClass="disabledField" />
	      	</h:column>
	
	      	<h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_min}"  />
	        	</f:facet>
	          	<h:inputText value="#{variable.min}" disabled="#{!variable.active }" 
	          			styleClass="#{(!variable.validMin ? 'validationError' : '') } changeWatch"/>
	      	</h:column>
	
	      	<h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_max}"  />
	        	</f:facet>
	          	<h:inputText value="#{variable.max}" disabled="#{!variable.active }" 
	          			styleClass="#{(!variable.validMax ? 'validationError' : '') } changeWatch"/>
	      	</h:column>
	
	      	<h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_dec}"  />
	        	</f:facet>
			  	<h:selectOneMenu value="#{variable.decimalPlaces}" disabled="#{!variable.active }" styleClass="changeWatch">
		     		<f:selectItems value="#{itemauthor.decimalPlaceList}" />
	  			</h:selectOneMenu>
	      	</h:column>
	
		</h:dataTable>
		<h:outputLabel value="<p>#{authorMessages.no_variables_defined}</p>" 
				rendered="#{itemauthor.currentItem.calculatedQuestion.variablesList eq '[]'}"/>
	</div>

	<!-- display formulas -->
	<div class="longtext"> <h:outputLabel value="#{authorMessages.calc_question_formula_label} " /></div>
	<div class="tier2">
		<h:dataTable cellpadding="0" cellspacing="0" styleClass="listHier" id="formulas" 
				value="#{itemauthor.currentItem.calculatedQuestion.formulasList}" var="formula">
	    	<h:column>
	        	<f:facet name="header">          
		    		<h:outputText value=""  />
	    	    </f:facet>
	          <h:outputText value="" />
	      </h:column>
	
	      <h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_formulaname_col}"  />
	        	</f:facet>
	          	<h:outputText escape="false" value="#{formula.name}" rendered="#{formula.active }" />
	          	<h:outputText escape="false" value="#{formula.name}" rendered="#{!formula.active }" styleClass="disabledField" />
	      </h:column>
	
	      <h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_formula_col}"  />
	        	</f:facet>
	        	<h:inputTextarea value="#{formula.text }"
	        			cols="40" rows="3" 
	        			disabled="#{!formula.active }" 
	        			styleClass="#{(!formula.validFormula ? 'validationError' : '')} changeWatch"/>        	
	      </h:column>
	      
	      <h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_tolerance}"  />
	        	</f:facet>
	          	<h:inputText value="#{formula.tolerance}"  
	          			disabled="#{!formula.active }" 
	          			styleClass="#{(!formula.validTolerance ? 'validationError' : '')} changeWatch"/>
	      </h:column>
	      
	      <h:column>
	        	<f:facet name="header">
	          		<h:outputText value="#{authorMessages.calc_question_dec}" />
	        	</f:facet>
			  	<h:selectOneMenu id="assignToPart" value="#{formula.decimalPlaces}" disabled="#{!formula.active }" styleClass="changeWatch">
	     			<f:selectItems  value="#{itemauthor.decimalPlaceList}" />
	  			</h:selectOneMenu>          
	      </h:column>
		</h:dataTable>
		<h:outputLabel value="<p>#{authorMessages.no_formulas_defined}</p>" 
				rendered="#{itemauthor.currentItem.calculatedQuestion.formulasList eq '[]'}"/>
	</div>

    <!-- display calculations -->
    <div class="longtext"> <h:outputLabel value="#{authorMessages.calc_question_calculation_label} " /></div>
    <div class="tier2">
        <h:dataTable cellpadding="0" cellspacing="0" styleClass="listHier" id="calculations" 
                value="#{itemauthor.currentItem.calculatedQuestion.calculationsList}" var="calculation"
                rendered="#{itemauthor.currentItem.calculatedQuestion.hasCalculations}">
          <h:column>
            <f:facet name="header">
              <h:outputText value="" />
            </f:facet>
            <h:outputText value="" />
          </h:column>
          <h:column>
            <f:facet name="header">
              <h:outputText value="#{authorMessages.calc_question_calculation_col}" />
            </f:facet>
            <h:outputText escape="false" value="#{calculation.text}" />
          </h:column>
          <h:column>
            <f:facet name="header">
              <h:outputText value="#{authorMessages.calc_question_calculation_sample_col}" />
            </f:facet>
            <h:outputText escape="false" value="#{calculation.formula}" />
          </h:column>
          <h:column>
            <f:facet name="header">
              <h:outputText value="" />
            </f:facet>
            <h:outputText value="#{calculation.value}" />
          </h:column>
          <h:column>
            <f:facet name="header">
              <h:outputText value="#{authorMessages.calc_question_calculation_status_col}" />
            </f:facet>
            <h:outputText value="#{calculation.status}" />
          </h:column>
        </h:dataTable>
        <h:outputLabel value="<p>#{authorMessages.calc_question_no_calculations}</p>" 
                rendered="#{! itemauthor.currentItem.calculatedQuestion.hasCalculations}"/>
    </div>

	<br/>
	<br/>
    <!-- 6 PART -->

	<h:panelGrid columns="3" columnClasses="shorttext" rendered="#{itemauthor.target == 'assessment'}">
		<f:verbatim>&nbsp;</f:verbatim>
		<h:outputLabel value="#{authorMessages.assign_to_p}" />
	  	<h:selectOneMenu id="assignToPart" value="#{itemauthor.currentItem.selectedSection}">
	    	<f:selectItems  value="#{itemauthor.sectionSelectList}" />
	  	</h:selectOneMenu>
	</h:panelGrid>

    <!-- 7 POOL -->
	<h:panelGrid columns="3" columnClasses="shorttext" 
			rendered="#{itemauthor.target == 'assessment' && author.isEditPendingAssessmentFlow}">
		<f:verbatim>&nbsp;</f:verbatim>  <h:outputLabel value="#{authorMessages.assign_to_question_p}" />
	  	<h:selectOneMenu id="assignToPool" value="#{itemauthor.currentItem.selectedPool}">
	    	<f:selectItem itemValue="" itemLabel="#{authorMessages.select_a_pool_name}" />
	     	<f:selectItems value="#{itemauthor.poolSelectList}" />
	  	</h:selectOneMenu>
	</h:panelGrid><br/>

	<!-- 8 FEEDBACK -->
	<div class="longtext">
		<h:outputLabel value="#{authorMessages.correct_incorrect_an}" 
				rendered="#{itemauthor.target == 'questionpool' || (itemauthor.target != 'questionpool' && (author.isEditPendingAssessmentFlow && assessmentSettings.feedbackAuthoring ne '2') || (!author.isEditPendingAssessmentFlow && publishedSettings.feedbackAuthoring ne '2'))}"/>
		<br></br>
	</div>
	
	<div class="tier2">
	<h:panelGrid rendered="#{itemauthor.target == 'questionpool' || (itemauthor.target != 'questionpool' && (author.isEditPendingAssessmentFlow && assessmentSettings.feedbackAuthoring ne '2') || (!author.isEditPendingAssessmentFlow && publishedSettings.feedbackAuthoring ne '2'))}">
		<h:outputText value="#{authorMessages.correct_answer_opti}" />
	  	<f:verbatim><br/></f:verbatim>
	  	<samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.corrFeedback}" hasToggle="yes" >
	    	<f:validateLength maximum="4000"/>
	   	</samigo:wysiwyg>
	</h:panelGrid>	
	<br/>	
	<h:panelGrid rendered="#{itemauthor.target == 'questionpool' || (itemauthor.target != 'questionpool' && (author.isEditPendingAssessmentFlow && assessmentSettings.feedbackAuthoring ne '2') || (!author.isEditPendingAssessmentFlow && publishedSettings.feedbackAuthoring ne '2'))}">
		<h:outputText value="#{authorMessages.incorrect_answer_op}"/>
	  	<f:verbatim><br/></f:verbatim>
	   	<samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.incorrFeedback}" hasToggle="yes" >
	    	<f:validateLength maximum="4000"/>
	   	</samigo:wysiwyg>
	 </h:panelGrid>	
	</div>

	<!-- METADATA -->
	<h:panelGroup rendered="#{itemauthor.showMetadata == 'true'}" styleClass="longtext">
	<h:outputLabel value="Metadata"/><br/>
	<div class="tier2">
	
	<h:panelGrid columns="2" columnClasses="shorttext">
		<h:outputText value="#{authorMessages.objective}" />
	  	<h:inputText size="30" id="obj" value="#{itemauthor.currentItem.objective}" />
		<h:outputText value="#{authorMessages.keyword}" />
	  	<h:inputText size="30" id="keyword" value="#{itemauthor.currentItem.keyword}" />
		<h:outputText value="#{authorMessages.rubric_colon}" />
	  	<h:inputText size="30" id="rubric" value="#{itemauthor.currentItem.rubric}" />
	</h:panelGrid>
	</div>
	</h:panelGroup>

	<p class="act">
		<h:commandButton 
				rendered="#{itemauthor.target=='assessment'}" 
				value="#{commonMessages.action_save}" 
				action="#{itemauthor.currentItem.getOutcome}" 
				styleClass="active saveButton">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
	  	</h:commandButton>
	  	<h:commandButton 
	  			rendered="#{itemauthor.target=='questionpool'}" 
	  			value="#{commonMessages.action_save}" 
	  			action="#{itemauthor.currentItem.getPoolOutcome}" 
	  			styleClass="active saveButton">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
	  	</h:commandButton>
	  	<h:commandButton  
	  			rendered="#{itemauthor.target=='assessment'}" 
	  			value="#{commonMessages.cancel_action}" 
	  			action="editAssessment" 
	  			immediate="true">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ResetItemAttachmentListener" />
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditAssessmentListener" />
	  	</h:commandButton>
	 	<h:commandButton 
                rendered="#{itemauthor.target=='questionpool'}" 
	 			value="#{commonMessages.cancel_action}" 
	 			action="editPool" 
	 			immediate="true">
	        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ResetItemAttachmentListener" />
	 	</h:commandButton>
	</p>

</h:form>
<!-- end content -->
</div>
</body>
</html>
</f:view>

