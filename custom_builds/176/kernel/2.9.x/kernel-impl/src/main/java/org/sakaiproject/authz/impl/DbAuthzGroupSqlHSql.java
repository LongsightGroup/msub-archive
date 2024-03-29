/**********************************************************************************
 * $URL: https://svn.rsmart.com/svn/vendor/branches/sakai-kernel/rsmart-cle/kernel-impl/src/main/java/org/sakaiproject/authz/impl/DbAuthzGroupSqlHSql.java $
 * $Id: DbAuthzGroupSqlHSql.java 26198 2011-04-08 19:28:14Z jcrodriguez $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
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

package org.sakaiproject.authz.impl;

/**
 * methods for accessing authz data in a hypersonic sql database.
 */
public class DbAuthzGroupSqlHSql extends DbAuthzGroupSqlDefault
{
	// HSQL does not support SELECT FOR UPDATE, so we need to do an ordinary select here.
	// This means that maximum size limits on groups may not be reliably enforced using HSQL.
	
	@Override
	public String getSelectRealmUpdate()
	{
		return "select REALM_KEY from SAKAI_REALM where REALM_ID = ?";
	}
}
