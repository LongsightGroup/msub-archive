/**********************************************************************************
 *
 * $URL: https://source.sakaiproject.org/contrib/etudes/melete/tags/2.8m1/melete-hbm/src/java/org/etudes/component/app/melete/ModuleStudentPrivs.java $
 * $Id: ModuleStudentPrivs.java 56408 2008-12-19 21:16:52Z rashmi@etudes.org $  
 ***********************************************************************************
 *
 * Copyright (c) 2008 Etudes, Inc.
 *
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007, 2008 Foothill College, ETUDES Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/
package org.etudes.component.app.melete;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.etudes.api.app.melete.*;


/** @author Hibernate CodeGenerator */
public class ModuleStudentPrivs implements Serializable,ModuleStudentPrivsService {

    /** identifier field */
    private String courseId;

    /** identifier field */
    private String studentId;

    /** nullable persistent field */
    private boolean allowUnlimited;

    /** nullable persistent field */
    private boolean deny;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** identifier field */
    private org.etudes.component.app.melete.Module module;

    /** full constructor */
    public ModuleStudentPrivs(String courseId, String studentId, boolean allowUnlimited, boolean deny, Date startDate, Date endDate, org.etudes.component.app.melete.Module module) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.allowUnlimited = allowUnlimited;
        this.deny = deny;
        this.startDate = startDate;
        this.endDate = endDate;
        this.module = module;
    }

    /** default constructor */
    public ModuleStudentPrivs() {
    }

    /** minimal constructor */
    public ModuleStudentPrivs(String courseId, String studentId, org.etudes.component.app.melete.Module module) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.module = module;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isAllowUnlimited() {
        return this.allowUnlimited;
    }

    public void setAllowUnlimited(boolean allowUnlimited) {
        this.allowUnlimited = allowUnlimited;
    }

    public boolean isDeny() {
        return this.deny;
    }

    public void setDeny(boolean deny) {
        this.deny = deny;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }



    public org.etudes.api.app.melete.ModuleObjService getModule() {
        return this.module;
    }

    public void setModule(org.etudes.api.app.melete.ModuleObjService module) {
        this.module = (Module) module;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("courseId", getCourseId())
            .append("studentId", getStudentId())
            .append("module", getModule())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof ModuleStudentPrivs) ) return false;
        ModuleStudentPrivs castOther = (ModuleStudentPrivs) other;
        return new EqualsBuilder()
            .append(this.getCourseId(), castOther.getCourseId())
            .append(this.getStudentId(), castOther.getStudentId())
            .append(this.getModule(), castOther.getModule())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCourseId())
            .append(getStudentId())
            .append(getModule())
            .toHashCode();
    }

}
