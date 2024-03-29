/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation, The MIT Corporation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.tool.gradebook.ui;

import java.util.*;

/**
 * Singleton bean to set up URL filtering by current user's role.
 */
public class AuthorizationFilterConfigurationBean {
	private List userAbleToEditPages;
	private List userAbleToGradePages;
	private List userGradablePages;

	public List getUserAbleToEditPages() {
		return userAbleToEditPages;
	}
	public void setUserAbleToEditPages(List userAbleToEditPages) {
		this.userAbleToEditPages = userAbleToEditPages;
	}
	public List getUserAbleToGradePages() {
		return userAbleToGradePages;
	}
	public void setUserAbleToGradePages(List userAbleToGradePages) {
		this.userAbleToGradePages = userAbleToGradePages;
	}
	public List getUserAbleToViewOwnGradesPages() {
		return userGradablePages;
	}
	public void setUserAbleToViewOwnGradesPages(List userGradablePages) {
		this.userGradablePages = userGradablePages;
	}
}
