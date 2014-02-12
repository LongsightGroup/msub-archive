<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<script src="/osp-common-tool/js/jquery-1.2.1.js" >
</script>
<script type="text/javascript">
	function checkAll(cb, i) {
	
		if (cb.checked) {
			jQuery("tr:gt(0)").each(function(){
				jQuery("input:eq(" + i + ")", this).get(0).checked = true;
			});
		} else {
			jQuery("tr:gt(0)").each(function(){
				jQuery("input:eq(" + i + ")", this).get(0).checked = false;
			});
		} 
	}
</script>


<div class ="portletBody">

<h3><fmt:message key="perm.page.title"/></h3>
<div class="instruction">
   <c:out value="${message}" escapeXml="false" />
</div>

<form method="post">
<osp:form/>

<%--
<spring:bind path="permissionsEdit.siteId">
<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
</spring:bind>
--%>
<spring:bind path="permissionsEdit.permissions">

<input type="hidden" name="<c:out value="${status.expression}"/>" value="" />

   <table class="listHier lines" cellspacing="0" summary ="<fmt:message key="perm.list.summary"/>" border="0">
      <tr class="nolines">
         <td id="role" style="text-align: right; font-weight: bold;">
            <fmt:message key="perm.hdr.roles"/>
         </td>
         <c:forEach var="role" items="${roles}">
         <td headers="role"><c:out value="${role.id}" /></td>
         </c:forEach>
      </tr>
      <tr class="headCheck">
         <th id="role" style="font-weight: bold;">
            <fmt:message key="perm.hdr.general"/>
         </th>
         <c:forEach var="role" items="${roles}" varStatus="checkIndex">
         <th id="<spring:message code="${role.id}" text="${role.id}" />"><input type="checkbox" name="role-${role.id}" value="checkAll" onclick="checkAll(this, ${checkIndex.index});" /></th>
         </c:forEach>
      </tr>

      <c:forEach var="function" items="${toolFunctions}">
         <tr>
            <td headers="function"><fmt:message key="${function}" /></td>

            <c:forEach var="role" items="${roles}">
            <c:set var="checkValue"><c:out value="${role.id}" />~<c:out value="${function}" /></c:set>
            <td headers="revise" style="text-align: left;">
            <c:set var="checked" value=""/>

            <c:forEach var="current" items="${permissionsEdit.permissions}">
               <c:set var="currentString" value="${current}" />
               <c:if test="${currentString eq checkValue}">
                  <c:if test="${current.readOnly}">
                     <c:set var="checked" value="checkedPerm"/>
                  </c:if>
                  <c:if test="${!current.readOnly}">
                     <c:set var="checked" value="checked"/>
                  </c:if>
               </c:if>
            </c:forEach>

            <c:if test="${checked == 'checkedPerm'}">
               <img alt="This permission is read only"  src="<osp:url value="/img/checkon.gif"/>" border="0"/>
            </c:if>
            <c:if test="${checked != 'checkedPerm'}">
                  <input type="checkbox" name="<c:out value="${status.expression}"/>"
                     value="<c:out value="${checkValue}" />"
                     <c:out value="${checked}" />
                     />
            </c:if>
            </td>
            </c:forEach>
         </tr>
      </c:forEach>
   </table>
</spring:bind>


   <div class="act">
   	  <input type="hidden" id="toolPermissionsSaved" name="toolPermissionsSaved" value="false"/>
      <input type="submit" value="<fmt:message key="button_save"/>" accesskey="s" class="active" onclick="javascript:document.getElementById('toolPermissionsSaved').value=true"/>
      <input name="_cancel" type="submit" value="<fmt:message key="button_cancel"/>" accesskey="x"/>
   </div>

</form>
</div>
