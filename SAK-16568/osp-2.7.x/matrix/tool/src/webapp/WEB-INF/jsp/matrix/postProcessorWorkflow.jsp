<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages" var="msgs" />

<form method="POST" action="<osp:url value="reviewPostProcessor.osp"/>">
   <osp:form/>
   
   <h3><osp:message key="title_pogression" bundle="${msgs}" /></h3>
   <fieldset>
      <legend class="radio"><osp:message key="legend_workflowOptions" bundle="${msgs}" /></legend>
      <c:forEach var="option" items="${workflows}" varStatus="loopStatus">
         <div class="checkbox indnt1">
            <input type="radio" id="workflow_option_<c:out value="${loopStatus.index}" />" 
                  name="workflowId" 
                  <c:if test="${option.title == 'Complete Workflow'}">checked</c:if>
                  value="<c:out value="${option.id}" />" />
            <label for="workflow_option_<c:out value="${loopStatus.index}" />">
               <c:if test="${option.title == 'Complete Workflow'}">
                  <osp:message key="workflow_complete" bundle="${msgs}" />
               </c:if>
               <c:if test="${option.title == 'No Workflow'}">
                  <osp:message key="workflow_none" bundle="${msgs}" />
               </c:if>
               <c:if test="${option.title == 'Return Workflow'}">
                  <osp:message key="workflow_return" bundle="${msgs}" />
               </c:if>
               <c:if test="${option.title == 'Returned Workflow'}">
                  <osp:message key="workflow_returned" bundle="${msgs}" />
               </c:if>
            </label>
         </div>
      </c:forEach>
   </fieldset>
   
   <p class="act">      
      <input type="submit" name="submit" class="active" value="<osp:message key="submit" bundle="${msgs}" />" accesskey="s" />
      <input type="hidden" name="objId" value="<c:out value="${obj_id}"/>"/>
      <input type="hidden" name="manager" value="<c:out value="${manager}"/>"/>
   </p>
</form>