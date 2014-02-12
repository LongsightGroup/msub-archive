<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<div class="navPanel">
	<div class="viewNav">
		<h3><fmt:message key="assign.title.manage"/></h3>
		<p class="instruction"><fmt:message key="edit.addAssign.instructions"/></p>
	</div>	
	<osp:url var="listUrl" value="listAssignment.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>


<form method="POST" action="<osp:url value="listAssignment.osp"/>">

<table class="listHier lines nolines" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col" class="attach"></th>
         <th scope="col"><fmt:message key="assign.title"/></th>
         <th scope="col"><fmt:message key="assign.status"/></th>
         <th scope="col"><fmt:message key="assign.open"/></th>
         <th scope="col"><fmt:message key="assign.due"/></th>
      </tr>
   </thead>
   <tbody>
     <c:forEach var="bean" items="${assignments}">
        <tr>
         <td class="attach">
            <input type="checkbox" 
                   id="<c:out value='${bean.assignment.id}'/>" 
                   name="<c:out value='${bean.assignment.id}'/>" 
                   <c:if test="${bean.selected}">checked='checked'</c:if> 
                   />
         </td> <!-- checked -->
          <td><label for="<c:out value='${bean.assignment.id}'/>"><c:out value="${bean.assignment.title}" /></label></td>
          <td><c:out value="${bean.assignment.status}" /></td>
          <td><c:out value="${bean.assignment.openTimeString}" /></td>
          <td><c:out value="${bean.assignment.dueTimeString}" /></td> 
        </tr>
     </c:forEach>
    </tbody>
</table>

<div class="act">
      <input type="submit" name="_save" value="<fmt:message key="button_save"/>" accesskey="s" class="active"/>
      <input type="submit" name="_cancel" value="<fmt:message key="button_cancel"/>" accesskey="x" />

</div>

</form>