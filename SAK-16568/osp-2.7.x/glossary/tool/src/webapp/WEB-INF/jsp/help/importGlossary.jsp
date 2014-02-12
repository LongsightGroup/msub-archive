<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.glossary.bundle.Messages" var="msgs" />

<form  method="post">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><osp:message key="osp.help.glossary.importTitle" bundle="${msgs}" /></h3>
    
    <p class="instruction"><osp:message key="osp.help.glossary.importInstructions" bundle="${msgs}" /></p>
    <spring:bind path="uploadGlossary.uploadedGlossary">
       <c:if test="${status.error}">
	   		<p class="shorttext validFail">
        </c:if>
		<c:if test="${!status.error}">
	           <p class="shorttext">
        </c:if>
         <span class="reqStar">*</span>
            <label for="name"><osp:message key="osp.help.glossary.importTheseFiles" bundle="${msgs}" /></label>
            <input type="text" id="name" disabled="disabled"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedGlossary" id="uploadedGlossary"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <osp:message key="osp.help.glossary.PickFilesToImport" bundle="${msgs}" /></a>
   			<c:if test="${status.error}">
				<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
			</c:if>	
        </p>
    </spring:bind>
        
    <spring:bind path="uploadGlossary.replaceExistingTerms">
        <p class="instruction"><osp:message key="osp.help.glossary.whenTermExists" bundle="${msgs}" /></p>
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="replaceTerm" value="true" 
                <c:if test="${status.value}">checked="checked"</c:if> />
            <label for="replaceTerm">
                    <osp:message key="osp.help.glossary.replaceExistingTerm" bundle="${msgs}" />
            </label>
        </p>
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="ignoreTerm" value="false" 
                <c:if test="${status.value == false}">checked</c:if> />
            <label for="ignoreTerm">
                    <osp:message key="osp.help.glossary.ignoreExistingTerm" bundle="${msgs}" />
            </label>
        </p>
        
    </spring:bind>
    
    <div class="act">
      <input type="submit" value="<osp:message key="osp.help.glossary.importButton" bundle="${msgs}" />" class="active" accesskey="s" /> 
      <input type="button" value="<osp:message key="osp.help.glossary.cancelButton" bundle="${msgs}" />" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'" accesskey="x" />
    </div>

</form>