<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<form method="get" action="<osp:url value="scaffoldingCellInfo.osp"/>">
<osp:form/>

<p class="shorttext">
<label><fmt:message key="label_title" /></label> <c:out value="  ${scaffoldingCell.title}" />
</p>
<c:if test="${not empty scaffoldingCell.wizardPageDefinition.description}">
<p class="longtext">
<label class="block"><fmt:message key="label_description" /></label>
<c:out value="  ${scaffoldingCell.wizardPageDefinition.description}" escapeXml="false"/>
</p>
</c:if>

<c:if test="${not empty scaffoldingCell.guidance && not empty scaffoldingCell.guidance.instruction}">
<p class="longtext">
	<label class="block"><fmt:message key="instructions" /></label>
	<c:out value="  ${scaffoldingCell.guidance.instruction.text}" escapeXml="false"/>
</p>
</c:if>
</form>
