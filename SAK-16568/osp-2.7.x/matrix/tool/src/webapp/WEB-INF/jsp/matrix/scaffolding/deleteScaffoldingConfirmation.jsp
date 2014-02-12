<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>
   <fmt:message key="title_delete_matrix"/>
</h3>
   
<div class="alertMessage">
   <fmt:message key="text_AreYouSureDeleteMatrix"/>
	 <c:if test="${scaffolding_published}">
      <fmt:message key="text_CautionDeleteMatrix"/>
	</c:if>
</div>
<c:if test="${totalLinksNum > 0}">
	<div class="alertMessage">
		<c:if test="${totalLinksNum > 1}">
			<fmt:message key="confirmDeleteLinkWarningPart1Plural">
				<fmt:param value="${totalLinksNum}"/> 
			</fmt:message>
		</c:if>
		<c:if test="${totalLinksNum == 1}">
			<fmt:message key="confirmDeleteLinkWarningPart1Singlular">
				<fmt:param value="${totalLinksNum}"/> 
			</fmt:message>
		</c:if>
		<c:if test="${linkedSitesNum > 1}">
			<fmt:message key="confirmDeleteLinkWarningPart2Plural">
				<fmt:param value="${linkedSitesNum}"/> 
			</fmt:message>
		</c:if>
		<c:if test="${linkedSitesNum == 1}">
			<fmt:message key="confirmDeleteLinkWarningPart2Singlular">
				<fmt:param value="${linkedSitesNum}"/> 
			</fmt:message>
		</c:if>
		
	</div>
</c:if>
<form method="post">
   <div class="act">
      <input name="continue" type="submit" value="<fmt:message key="button_continue" />" class="active" accesskey="s" />
      <input name="cancel" type="submit" value="<fmt:message key="button_cancel" />"  accesskey="x" />
   </div>
</form>
