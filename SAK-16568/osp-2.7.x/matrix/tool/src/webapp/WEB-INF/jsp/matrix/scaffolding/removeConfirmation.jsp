<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<h3>
   <fmt:message key="title_remove">
      <fmt:param value="${label}"/>
   </fmt:message>
</h3>
   
<div class="alertMessage">
   <fmt:message key="text_AreYouSureRemove">
      <fmt:param value="${label}"/>
      <fmt:param value="${displayText}"/>
   </fmt:message>
</div>

<form method="post">
   <div class="act">
      <input name="continue" type="submit" value="<fmt:message key="button_continue" />" class="active" accesskey="s" />
      <input name="cancel" type="submit" value="<fmt:message key="button_cancel" />"  accesskey="x" />
   </div>
</form>