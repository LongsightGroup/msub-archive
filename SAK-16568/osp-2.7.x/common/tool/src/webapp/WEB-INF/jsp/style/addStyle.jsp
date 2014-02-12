<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<form method="post" action="addStyle.osp">
    <osp:form />

    <input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
    <input type="hidden" name="validate" value="true" />
    <spring:bind path="style.id">
        <input type="hidden" name="style_id" value="<c:out value="${status.value}"/>" />
    </spring:bind>

    <c:if test="${empty style.id}">
        <h3><fmt:message key="title_addStyle"/></h3>
        <p class="instruction">
        <fmt:message key="instructions_addStyle"/>
    </c:if>
    <c:if test="${not empty style.id}">
        <h3><fmt:message key="title_editStyle"/></h3>
        <p class="instruction">
        <fmt:message key="instructions_editStyle"/>
    </c:if>
        <fmt:message key="instructions_requiredFields"/>
    </p>
    
    
  <spring:bind path="style.name">
  		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>			
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>	
		<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_displayName"/></label>
		<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id" 
				 value="<c:out value="${status.value}"/>" 
			  size="25" maxlength="25" <c:out value="${disabledText}"/> />
		  <c:if test="${status.error}">
               <span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></div>
            </c:if>
	  
	 </p>
        </spring:bind>
      
      <spring:bind path="style.description">
      	<c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        	</c:if>
      	<div class="longtext">
         	<label class="block" for="descriptionTextArea"><fmt:message key="label_description"/></label>
			<textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80" 
			<c:out value="${disabledText}"/>><c:out value="${status.value}"/></textarea>
      	</div>
    </spring:bind>

    <spring:bind path="style.styleFile">
		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>			
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>	
		<span class="reqStar">*</span>
		<label  for="styleFileName"><fmt:message key="label_styleFile"/></label>
		<input type="text" id="styleFileName" disabled="disabled"
			value="<c:out value="${styleFileName}"/>" />
		<input type="hidden" name="styleFile" id="styleFile"
			value="<c:out value="${status.value}"/>" />
		<a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${STYLE_FILE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();" title="<fmt:message key="title_pickFile"/>">
		<fmt:message key="label_pickFile"/> </a>
		<c:if test="${status.error}">
			<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
		</c:if>
		</p>	
    </spring:bind>

    
   <div class="act">

      <input type="submit" name="save" class="active" accesskey="s"
      <c:if test="${empty style.id}">
             value="<fmt:message key="button_submit"/>"
      </c:if>
      <c:if test="${not empty style.id}">
             value="<fmt:message key="button_submitEdit"/>"
      </c:if>
            onclick="javascript:document.forms[0].validate.value='true';" />
      <input type="button" name="cancel" value="<fmt:message key="button_cancel" />"
            onclick="window.document.location='<osp:url value="listStyle.osp"/>'" accesskey="x" />
   </div>

</form>
