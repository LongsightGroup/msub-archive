<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/js/colorPicker/picker.inc" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>
	
	
	<h3><fmt:message key="title_edit_ScaffRow"/></h3>
	
	<div class="instructions">
		<fmt:message key="instructions_scaffRow"/>
		<fmt:message key="instructions_requiredFields"/>
	</div>
	
<form method="post">
	<osp:form/> 
	<input type="hidden" name="params" value="" />
	<input type="hidden" name="dest" value="" />
	<input type="hidden" name="validate" value="false" />
	
	<h4><fmt:message key="title_scaffRow"/></h4>
	
    <spring:bind path="criterion.description">
        <c:if test="${status.error}">
    		<p class="shorttext validFail">
        </c:if>
        <c:if test="${!status.error}">
    		<p class="shorttext">
        </c:if>
            <span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_rowName"/></label>
    		<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id" 
    				value="<c:out value="${status.displayValue}"/>" />
        <c:if test="${status.error}">
           <span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
        </c:if>					
    	</p>
    </spring:bind>
	<p class="shorttext">
		<spring:bind path="criterion.color">
			<label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_bgColor"/></label>   
		   <input type="text" disabled="disabled" value="" size="2" 
                        name="<c:out value="${status.expression}"/>_sample"
                        class="matrixRowDefault"
                        style="background-color: <c:out value="${status.value}"/>" />   
                        
			<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id" 
					value="<c:out value="${status.displayValue}"/>"
               onchange="document.forms[0].elements['<c:out value="${status.expression}"/>_sample'].style.backgroundColor='' + document.forms[0].elements['<c:out value="${status.expression}"/>'].value" />
			<span class="error_message"><c:out value="${status.errorMessage}"/></span>
			<!--
				Put icon by the input control.
				Make it the link calling picker popup.
				Specify input object reference as first parameter to the function and palete selection as second.
			-->
			<a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])" title="<fmt:message key="color_picker_back_linktitle"/>"><img width="15" height="13" border="0" alt="<fmt:message key="color_picker_back_linktitle"/>" src="<osp:url value="/js/colorPicker/img/sel.gif"/>" /></a>
		</spring:bind>
	</p>
              <p class="shorttext">
      <spring:bind path="criterion.textColor">
         <label for="<c:out value="${status.expression}"/>-id"><fmt:message key="label_fontColor"/></label>   
      
         <input type="text" disabled="disabled" value="" size="2" 
                        name="<c:out value="${status.expression}"/>_sample"
                        class="matrixRowFontAsBGDefault" 
                        style="background-color: <c:out value="${status.value}"/>" />
         <input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id"  
               value="<c:out value="${status.displayValue}"/>"
               onchange="document.forms[0].elements['<c:out value="${status.expression}"/>_sample'].style.backgroundColor='' + document.forms[0].elements['<c:out value="${status.expression}"/>'].value" />
         <span class="error_message"><c:out value="${status.errorMessage}"/></span>
         <!--
            Put icon by the input control.
            Make it the link calling picker popup.
            Specify input object reference as first parameter to the function and palete selection as second.
         -->
         <a href="javascript:TCP.popup(document.forms[0].elements['<c:out value="${status.expression}"/>'])" title="<fmt:message key="color_picker_fore_linktitle"/>"><img width="15" height="13" border="0" alt="<fmt:message key="color_picker_fore_linktitle"/>" src="<osp:url value="/js/colorPicker/img/sel.gif"/>" /></a>
      </spring:bind>
   </p>
	
	<div class="act">
		<input type="submit" accesskey="s" name="updateAction" class="active" value="<osp:message key="button_update"/>" />
		<input type="button" accesskey="x" name="action" value="<osp:message key="button_cancel"/>" onclick="javascript:doCancel()" />
	</div>

</form>
<form name="cancelForm" method="get" action="<osp:url value="addScaffolding.osp" />">
	<osp:form/>
	<input type="hidden" name="<c:out value="${isInSession}"/>" value="true" />
</form>
	
