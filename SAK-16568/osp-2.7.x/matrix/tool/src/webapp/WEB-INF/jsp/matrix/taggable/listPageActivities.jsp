<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="org.theospi.portfolio.matrix.bundle.Messages" />

<script type="text/javascript" language="JavaScript"
	src="/osp-common-tool/js/thickbox.js"></script>
<link href="/osp-common-tool/css/thickbox.css" type="text/css"
	rel="stylesheet" media="all" />

<h3><fmt:message key="matrix_page_associations">
	<fmt:param value="${pageTitle}" />
</fmt:message></h3>

<table class="listHier lines nolines" cellspacing="0" border="0"
	summary="<fmt:message key="list_activity_summary"/>">
	<thead>
		<tr>
			<th scope="col"><fmt:message key="item_name_title" /></th>
			<th scope="col"><fmt:message key="type_title" /></th>
			<th scope="col"><fmt:message key="site_heading" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="activity" items="${pageActivities}">
			<tr>
				<td style="white-space: nowrap"><a class="thickbox"
					href="<c:out value="${activity.activity.activityDetailUrl}" />">
				<c:out value="${activity.activity.title}" /> </a></td>
				<td style="white-space: nowrap"><c:out
					value="${activity.activity.typeName}" /></td>
				<td style="white-space: nowrap"><c:out
					value="${activity.contextName}" escapeXml="false" /></td>

			</tr>
		</c:forEach>
	</tbody>

</table>
<br/>
<form name="form" method="POST">
	<input type="submit" name="submit" value="<fmt:message key="button_back"/>" accesskey="b" class="active"/>
</form>

