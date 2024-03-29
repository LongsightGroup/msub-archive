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

package org.sakaiproject.tool.assessment.data.dao.assessment;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentMetaDataIfc;

import java.io.*;

import org.apache.log4j.*;

public class AssessmentMetaData
    implements Serializable, AssessmentMetaDataIfc {
  static Category errorLogger = Category.getInstance("errorLogger");

  private static final long serialVersionUID = 7526471155622776147L;

  public static final String AUTHORS = "ASSESSMENT_AUTHORS";
  public static final String KEYWORDS = "ASSESSMENT_KEYWORDS";
  public static final String OBJECTIVES = "ASSESSMENT_OBJECTIVES";
  public static final String RUBRICS = "ASSESSMENT_RUBRICS";
  public static final String BGCOLOR = "ASSESSMENT_BGCOLOR";
  public static final String BGIMAGE = "ASSESSMENT_BGIMAGE";

  private Long id;
  private AssessmentBaseIfc assessment;
  private String label;
  private String entry;

  public AssessmentMetaData() {}

  public AssessmentMetaData(AssessmentBaseIfc assessment, String label, String entry) {
    this.assessment = assessment;
    this.label = label;
    this.entry = entry;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AssessmentBaseIfc getAssessment() {
    return assessment;
  }

  public void setAssessment(AssessmentBaseIfc assessment) {
    this.assessment = assessment;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getEntry() {
    return entry;
  }

  public void setEntry(String entry) {
    this.entry = entry;
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
  }

}
