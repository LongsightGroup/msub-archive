/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/api/src/java/org/theospi/portfolio/wizard/model/WizardCategory.java $
* $Id:WizardCategory.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.wizard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 13, 2006
 * Time: 10:18:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class WizardCategory extends IdentifiableObject {

   private String title;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private Wizard wizard;
   private WizardCategory parentCategory;
   private int sequence = 0;

   private List childCategories;
   private List childPages;

   public WizardCategory() {
   }

   public WizardCategory(Wizard wizard) {
      this.wizard = wizard;
      setChildCategories(new ArrayList());
      setChildPages(new ArrayList());
      setCreated(new Date());
      setModified(new Date());
   }
   
   public boolean equals(Object in) {
      return super.equals(in);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getModified() {
      return modified;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   /**
    * @return List of WizardCategory
    */
   public List getChildCategories() {
      return childCategories;
   }

   /**
    * @param childCategories List of WizardCategory
    */
   public void setChildCategories(List childCategories) {
      this.childCategories = childCategories;
   }

   public List getChildPages() {
      return childPages;
   }

   public void setChildPages(List childPages) {
      this.childPages = childPages;
   }

   public Wizard getWizard() {
      return wizard;
   }

   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public WizardCategory getParentCategory() {
      return parentCategory;
   }

   public void setParentCategory(WizardCategory parentCategory) {
      this.parentCategory = parentCategory;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

}
