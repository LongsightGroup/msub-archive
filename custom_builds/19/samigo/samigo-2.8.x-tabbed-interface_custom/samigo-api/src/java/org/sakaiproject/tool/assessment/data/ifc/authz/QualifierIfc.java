/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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



package org.sakaiproject.tool.assessment.data.ifc.authz;

public  interface QualifierIfc
    extends java.io.Serializable
{
//  String getQualifierId();
  long getQualifierId();

  //void setQualifierId(String id);
  void setQualifierId(long id);

  String getReferenceName();

  void setReferenceName(String referenceName);

  String getDisplayName();

  void setDisplayName(String displayName);

  String getDescription();

  void setDescription(String description);

  String getQualifierTypeId();

  void setQualifierTypeId(String qualifierTypeId);

}
