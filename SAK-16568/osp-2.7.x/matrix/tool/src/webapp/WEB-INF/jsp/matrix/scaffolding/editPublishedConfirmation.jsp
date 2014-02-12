<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>
  <fmt:message key="title_saveMatrix">
    <fmt:param><c:out value="${label}"/></fmt:param>
  </fmt:message>
</h3>
  
<div class="alertMessage" >
<fmt:message key="text_areYouSureEdit">
  <fmt:param><c:out value="${label}"/></fmt:param>
</fmt:message>


<c:if test="${changedCellsSize > 0}">
	<p>
	The following cells have submissions for the form(s) you removed and will no longer use the default forms:
	<ul>
	<c:forEach var="cellName" items="${changedCells}" varStatus="loopCount">
		<li><c:out value="${cellName}"/></li>
	</c:forEach>
	</ul>
</c:if>
</div>

<form method="post">

<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="act">
<input name="continue" type="submit" value="<osp:message key="button_continue"/>" class="active" accesskey="s" />
<input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
</div>
</form>