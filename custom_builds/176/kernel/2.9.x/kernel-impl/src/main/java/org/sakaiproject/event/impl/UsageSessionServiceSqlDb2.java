/**********************************************************************************
 * $URL: https://svn.rsmart.com/svn/vendor/branches/sakai-kernel/rsmart-cle/kernel-impl/src/main/java/org/sakaiproject/event/impl/UsageSessionServiceSqlDb2.java $
 * $Id: UsageSessionServiceSqlDb2.java 26198 2011-04-08 19:28:14Z jcrodriguez $
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

package org.sakaiproject.event.impl;

/**
 * methods for accessing session usage data in a db2 database.
 */
public class UsageSessionServiceSqlDb2 extends UsageSessionServiceSqlDefault
{
   public String getSakaiSessionSql1()
   {
      // SESSION_USER is a reserved word in mssql and db2
      return "select SESSION_ID,SESSION_SERVER,SAKAI_SESSION.SESSION_USER,SESSION_IP,SESSION_HOSTNAME,SESSION_USER_AGENT,SESSION_START,SESSION_END,SESSION_ACTIVE from SAKAI_SESSION where SESSION_ID = ?";
   }

   public String getInsertSakaiSessionSql()
   {
      // SESSION_USER is a reserved word in mssql and db2
      return "insert into SAKAI_SESSION (SESSION_ID,SESSION_SERVER," +
         "SAKAI_SESSION.SESSION_USER,SESSION_IP,SESSION_HOSTNAME,SESSION_USER_AGENT,SESSION_START,SESSION_END, SESSION_ACTIVE) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
   }

   public String getSakaiSessionSql2()
   {
      return "select SESSION_ID,SESSION_SERVER,SAKAI_SESSION.SESSION_USER,SESSION_IP,SESSION_HOSTNAME,SESSION_USER_AGENT,SESSION_START,SESSION_END,SESSION_ACTIVE from SAKAI_SESSION where SESSION_ACTIVE=1 ORDER BY SESSION_SERVER ASC, SESSION_START ASC";
   }
}
