<%@ include file="/WEB-INF/jsp/include.jsp" %>
<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.glossary.bundle.Messages"/>
<html>
<bead>
</head>
<body>
<h3><fmt:message key="title_glossary"/></h3>

<c:forEach var="entry" items="${glossary}">
<a name="<c:out value="${entry.term}"/>"/>
<p><b><c:out value="${entry.term}"/></b> - <c:out value="${entry.description}"/></p>
</c:forEach>

</body>
</html>