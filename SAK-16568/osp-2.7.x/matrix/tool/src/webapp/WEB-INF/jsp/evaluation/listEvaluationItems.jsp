<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<osp-c:authZMap prefix="osp.matrix." var="can" />
<osp-c:authZMap prefix="osp.portfolio.evaluation." var="canPort" />

<div class="navIntraTool">
    <c:if test="${isMaintainer && !isUserSite}">
      <c:set var="hasFirstAction" value="true" />
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
        <osp:param name="message"><fmt:message key="action_message_setPermission">
         <fmt:param><c:out value="${tool.title}"/></fmt:param>
               <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
         </osp:param>

             <osp:param name="name" value="review"/>
             <osp:param name="qualifier" value="${worksite.id}"/>
             <osp:param name="returnView" value="listEvaluationItemsRedirect"/>
             </osp:url>"
            title="<fmt:message key="action_permissions_title"/>" >
            <fmt:message key="action_permissions"/>
        </a>
    </c:if>
    <c:if test="${!isUserSite}" >
       <c:if test="${hasFirstAction}" > | </c:if>
       <c:if test="${currentSiteEvalsKey == evalType}">
          <a href="<osp:url value="listEvaluationItems.osp"/>&evalTypeKey=<c:out value="${allEvalsKey}" />"><fmt:message key="show_all_evals"/></a>
      </c:if>
      <c:if test="${allEvalsKey == evalType}">
         <a href="<osp:url value="listEvaluationItems.osp"/>&evalTypeKey=<c:out value="${currentSiteEvalsKey}" />"><fmt:message key="show_site_evals"/></a>
       </c:if>
    </c:if>
</div>

<c:if test="${!canPort.use}">
	<div class="alertMessage"><fmt:message key="eval_message_notAllowed"/></div>
</c:if>

<c:if test="${not empty errorMessage}">
   <div class="alertMessage"><c:out value="${errorMessage}" /></div>
</c:if>

<div class="navPanel">
	<div class="viewNav">
	    <h3>
			<fmt:message key="title_evaluationManager"/>
			<c:if test="${allEvalsKey == evalType}">
				<span class="highlight"> <fmt:message key="eval_all_evals_suffix"/></span>
			</c:if>
			<c:if test="${currentSiteEvalsKey == evalType}">
				<span class="highlight"> <fmt:message key="eval_site_evals_suffix"/></span> 
			</c:if>
		</h3>
	</div>

	<c:if test="${canPort.use}">
		<form method="post" id="reviewList" name="reviewList">
			<osp:form />
			<osp:url  var="listUrl" value="listEvaluationItems.osp" />
			<osp:listScroll  listUrl="${listUrl}" className="listNav" />
		</form>
	</c:if>
</div>	

<c:if test="${canPort.use}">
	<div class="navPanel">
			<c:choose>
				<c:when test="${hasGroups && empty userGroups}">
					<p class="instruction"><fmt:message key="matrix_groups_unavailable"></fmt:message></p>
				</c:when>
				<c:otherwise>
					<form method="get" action="<osp:url value="listEvaluationItems.osp"/>">
						<osp:form/>
						<div class="viewNav">
							<c:if test="${not empty userGroups && userGroupsCount > 0}">
								<label for="group_filter-id"><fmt:message key="matrix_viewing_select_group" /></label>
								<select name="group_filter" id="group_filter-id">
									<option value="" <c:if test="${empty filteredGroup}">selected="selected"</c:if>>
									<fmt:message key="matrix_groups_showall"></fmt:message>
									</option>
									<c:forEach var="group" items="${userGroups}">
										<option value="<c:out value="${group.id}"/>" <c:if test="${filteredGroup == group.id}">selected="selected"</c:if>>
											<c:out value="${group.title}"></c:out>
										</option>
									</c:forEach>
								</select>
								<input type="submit" name="filter" value="<fmt:message key="button_filter"></fmt:message>"/>
							</c:if>					
						</div>
					</form>
				</c:otherwise>
			</c:choose>
	</div>	
</c:if>



<c:if test="${canPort.use}">
    <form method="POST" id="evalList" name="evalList">
        <osp:form />
    <input type="hidden" id="action" name="action" value="" />
    <input type="hidden" id="eval_id" name="id" value="" />
    <c:set var="sortDir" value="asc" />
    <c:set var="sortDirectionText" value="descending" />
    
    <c:choose>
      <c:when test="${empty reviewerItems}">
      <p class="instruction"><fmt:message key="eval_list_empty_message"/></p>
   </c:when>
   <c:otherwise>
    
    <table class="listHier lines nolines" cellspacing="0" summary="<fmt:message key="eval_list_summary"/>">
        <thead>
            <tr>
                <th title='<fmt:message key="eval_sortbytitle"/>' scope="col">
                   <c:if test="${sortByColumn == 'title'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listEvaluationItems.osp"/>&sortByColumn=title&direction=<c:out value="${sortDir}" />">
                     <fmt:message key="eval_title"/>
                     <c:if test="${sortByColumn == 'title'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbytitleasc"/>'
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbytitledesc"/>'
                             </c:if>
                             />
                     </c:if>
                  </a>
               </th>
               <th title='<fmt:message key="eval_sortbyowner"/>' scope="col">	                  
                    <c:if test="${sortByColumn == 'owner'}">
                       <c:if test="${direction == 'asc'}">
                          <c:set var="sortDir" value="desc" />
                          <c:set var="sortDirectionText" value="ascending" />
                       </c:if>
                    </c:if>
                    <a href="<osp:url value="listEvaluationItems.osp"/>&sortByColumn=owner&direction=<c:out value="${sortDir}" />">
                       <fmt:message key="eval_owner"/>
                       <c:if test="${sortByColumn == 'owner'}">
                       <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                           <c:if test="${sortDirectionText == 'ascending'}">
                               alt ='<fmt:message key="eval_sortbyownerasc"/>'
                            </c:if>
                            <c:if test="${sortDirectionText == 'descending'}">
                               alt ='<fmt:message key="eval_sortbyownerdesc"/>'
                            </c:if>
                          />
                       </c:if>
                    </a>
                </th>
                <th title='<fmt:message key="eval_sortbydateReceived"/>' scope="col">
                  <c:if test="${sortByColumn == 'date'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listEvaluationItems.osp"/>&sortByColumn=date&direction=<c:out value="${sortDir}" />">
                     <fmt:message key="eval_dateReceived"/>
                     <c:if test="${sortByColumn == 'date'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbydateReceivedasc"/>'
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbydateReceiveddesc"/>'
                             </c:if>
                         />
                     </c:if>
                  </a>
                </th>
            <c:if test="${allEvalsKey == evalType}">
               <th title='<fmt:message key="eval_sortbysite"/>' scope="col">
                 <c:if test="${sortByColumn == 'site'}">
                   <c:if test="${direction == 'asc'}">
                     <c:set var="sortDir" value="desc" />
                     <c:set var="sortDirectionText" value="ascending" />
                   </c:if>
                 </c:if>
                 <a href="<osp:url value="listEvaluationItems.osp"/>&sortByColumn=site&direction=<c:out value="${sortDir}" />">
                   <fmt:message key="eval_site"/>
                   <c:if test="${sortByColumn == 'site'}">
                   <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                         <c:if test="${sortDirectionText == 'ascending'}">
                           alt ='<fmt:message key="eval_sortbysiteasc"/>'
                         </c:if>
                         <c:if test="${sortDirectionText == 'descending'}">
                           alt ='<fmt:message key="eval_sortbysitedesc"/>'
                         </c:if>
                       />
                   </c:if>
                 </a>
               </th>
            </c:if>  
                <th title='<fmt:message key="eval_sortbytype"/>' scope="col"> 
                  <c:if test="${sortByColumn == 'type'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listEvaluationItems.osp"/>&sortByColumn=type&direction=<c:out value="${sortDir}" />">
                     <fmt:message key="eval_type"/>
                     <c:if test="${sortByColumn == 'type'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbytypeasc"/>'
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbytypedesc"/>'
                             </c:if>
                          />
                     </c:if>
                  </a>
                </th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${reviewerItems}" varStatus="loopCount">
					<c:if test="${item.url != null}">
					<c:choose>
						<c:when test="${item.evalType == 'matrix_cell_type'}">
							<c:set var="permCheck" value="true" />
						</c:when>
						<c:when test="${item.evalType == 'wizard_type'}">
							<osp-c:authZMap prefix="osp.wizard." var="canEvalWizardTool" qualifier="${item.id}"/>
							<c:set var="permCheck" value="${canEvalWizardTool.evaluateSpecificWizard}" />
						</c:when>
						<c:when test="${item.evalType == 'wizard_page_type'}">
							<osp-c:authZMap prefix="osp.wizard." var="canEvalPage" qualifier="${item.id}"/>
							<c:set var="permCheck" value="${canEvalPage.evaluateSpecificWizardPage}" />
						</c:when>
					</c:choose>

                <tr>
                  <td  class="specialLink">
                    <h4>
                     <c:choose>
                       <c:when test="${permCheck}">
                          <a href="#" onClick="document.getElementById('action').value='open';document.getElementById('eval_id').value='<c:out value="${item.id.value}" />_<c:out value="${item.owner.id}" />';document.getElementById('evalList').submit();"
                          title="<fmt:message key="eval_link_title"/>">
                             <c:out value="${item.title}" />
                          </a>
                       </c:when>
                       <c:otherwise>
                          <c:out value="${item.title}" />
                       </c:otherwise>
                     </c:choose>
                    </h4>  
                  </td>
				  <td>  
                     <c:choose>
                       <c:when test="${item.owner != null && !item.hideOwnerDisplay}">
                           <c:out value="${item.owner.sortName}" />
                       </c:when>
                       <c:otherwise>
                           <span title="<fmt:message key="blind_evaluation_tooltip"/>">
                           <fmt:message key="blind_evaluation_username"></fmt:message>
                           </span>
                       </c:otherwise>
                     </c:choose>
                  </td>	
                  <td>
                        <c:if test="${item.submittedDate==null}"> &nbsp; </c:if>
                        <c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${item.submittedDate}" pattern="${dateFormat}"/>
                  </td>
               <c:if test="${allEvalsKey == evalType}">
                  <td>
                     <c:out value="${item.siteTitle}" />
                  </td>
               </c:if>  
                    <td>
               <c:choose>
                  <c:when test="${item.evalType == 'matrix_cell_type'}">
                     <fmt:message key='eval_type_nice_matrixcell'/>
                  </c:when>
                  <c:when test="${item.evalType == 'wizard_type'}">
                     <fmt:message key='eval_type_nice_wizard'/>
                  </c:when>
                  <c:when test="${item.evalType == 'wizard_page_type'}">
                     <fmt:message key='eval_type_nice_wizard_page'/>
                  </c:when>
               </c:choose>
                    </td>
                </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    </c:otherwise>
    </c:choose>
    </form>
</c:if>
