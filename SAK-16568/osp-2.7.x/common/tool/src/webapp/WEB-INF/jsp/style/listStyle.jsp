<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<!-- GUID=<c:out value="${newStyleId}"/> -->

<osp-c:authZMap prefix="osp.style." useSite="true" var="can" />


<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addStyle.osp"/>" title="<fmt:message key="action_new_title"/>" >
          <fmt:message key="action_new"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer && selectableStyle != 'true'}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
                 <fmt:param><c:out value="${tool.title}"/></fmt:param>
                <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
              </osp:param>
                <osp:param name="name" value="style"/>
                <osp:param name="qualifier" value="${worksite.id}"/>
                <osp:param name="returnView" value="listStyleRedirect"/>
                </osp:url>"
                title="<fmt:message key="action_permissions_title"/>" >
          <fmt:message key="action_permissions"/>
          </a>
       </c:if>
    </div>
</c:if>


<div class="navPanel">
	<div class="viewNav">
		<c:if test="${can.create}">
			<h3><fmt:message key="title_styleManager"/></h3>
			<div class="instruction">
				<fmt:message key="info_styleManager"/>
			</div>
		</c:if>	
		<c:if test="${!(can.create)}">
			<h3><fmt:message key="title_styleUser"/></h3>
			<div class="instruction">
				<fmt:message key="info_styleUser"/>
			</div>
		</c:if>
	</div>
	<osp:url var="listUrl" value="listStyle.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>	

<c:if test="${not empty styleError}">
   <div class="validation"><fmt:message key="${styleError}"/></div>
</c:if>





	<table class="listHier lines nolines " cellspacing="0"  summary="<fmt:message key="table_summary"/>">
	   <thead>
		  <tr>
			 <th scope="col" class="attach"></th>
			 <th scope="col"><fmt:message key="table_header_name"/></th>
			 <th scope="col"><fmt:message key="actions"/></th>
			 <th scope="col"><fmt:message key="table_header_owner"/></th>
			<th scope="col"><fmt:message key="table_header_published"/></th>
		  </tr>
	   </thead>
	   <tbody>
	  <c:forEach var="style" items="${styles}">
		<tr>
		  <td class="attach">
			 <c:if test="${selectedStyle == style.id}">
				<img src="<osp:url value="/img/arrowhere.gif"/>" title="<fmt:message key="table_selected_indicator"/>" alt="<fmt:message key="table_selected_indicator"/>"/>
			 </c:if>
		  </td>
		  <td>
			 <c:out value="${style.name}" />
		</td>
		<td>
			 <c:set var="hasFirstAction" value="false" />
			 <div class="itemAction">
				 <c:if test="${can.globalPublish && (style.globalState == 0 || style.globalState == 1) && isGlobal}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="publishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&publishTo=global"><fmt:message key="table_action_publish"/></a>
				 </c:if>
				 
				 <c:if test="${selectableStyle != 'true' && can.suggestGlobalPublish && style.globalState == 0 && !isGlobal}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="publishStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&publishTo=suggestGlobal"><fmt:message key="table_action_suggest_global_publish"/></a>
				 </c:if>
				 
				 <c:if test="${can.edit}">
				   <c:if test="${hasFirstAction}" > | </c:if>
				   <a href="<osp:url value="editStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_edit"/></a>
				   <c:set var="hasFirstAction" value="true" />
				 </c:if>
		
				 <c:if test="${can.delete}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a onclick="return confirmDeletion();"
					   href="<osp:url value="deleteStyle.osp"/>&style_id=<c:out value="${style.id.value}" />"><fmt:message key="table_action_delete"/></a>
				 </c:if>
				 
				 <c:if test="${selectableStyle == 'true' and selectedStyle != style.id.value}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="selectStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&selectAction=on"><fmt:message key="table_action_select"/></a>
				 </c:if>
				 
				 <c:if test="${selectableStyle == 'true' and selectedStyle == style.id.value}">
					 <c:if test="${hasFirstAction}" > | </c:if>
					 <c:set var="hasFirstAction" value="true" />
					 <a href="<osp:url value="selectStyle.osp"/>&style_id=<c:out value="${style.id.value}" />&selectAction=off"><fmt:message key="table_action_unselect"/></a>
				 </c:if>
			 </div>
		  </td>
		  <td><c:out value="${style.owner.displayName}" /></td>
		<td><fmt:message key="style_published_status${style.globalState}"/></td>
		</tr>
		<c:if test="${!(empty style.description)}">
			<tr  class="exclude">
				  <td colspan="5">
					<div class="instruction indnt2 textPanelFooter">
						<c:out value="${style.description}" />		
						</div>
				  </td>
			</tr>	  
		</c:if>
	  </c:forEach>
	  
		</tbody>
	  </table>
	  
	  <c:if test="${ empty styles}">
			<p class="instruction indnt1">
			<fmt:message key="table_no_items_message"/>
			<c:if test="${can.create}">
					<fmt:message key="table_no_items_message_add"/>
				</c:if>
			</p>
		</c:if>
 
   <div class="act">
      <c:if test="${selectableStyle == 'true'}">
         <input type="button" name="goBack" class="active" value="<fmt:message key="button_goback"/>"
            onclick="window.document.location='<osp:url value="selectStyle.osp"/>'" accesskey="x"/>
      </c:if>
   </div>