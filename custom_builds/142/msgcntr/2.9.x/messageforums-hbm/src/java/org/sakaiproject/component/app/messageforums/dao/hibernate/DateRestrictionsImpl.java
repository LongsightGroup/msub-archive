/**********************************************************************************
 * $URL: https://svn.rsmart.com/svn/vendor/branches/sakai/rsmart-cle/msgcntr/messageforums-hbm/src/java/org/sakaiproject/component/app/messageforums/dao/hibernate/DateRestrictionsImpl.java $
 * $Id: DateRestrictionsImpl.java 13692 2009-04-24 16:14:32Z jbush $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.component.app.messageforums.dao.hibernate;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.messageforums.DateRestrictions;

public class DateRestrictionsImpl implements DateRestrictions {

    private static final Log LOG = LogFactory.getLog(DateRestrictionsImpl.class);
 
    private Date visible;
    private Boolean visiblePostOnSchedule;
    private Date postingAllowed;
    private Boolean postingAllowedPostOnSchedule;
    private Date readOnly;
    private Boolean readOnlyPostOnSchedule;
    private Date hidden;
    private Boolean hiddenPostOnSchedule;
    private Long id;
    private Integer version; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public Date getHidden() {
        return hidden;
    }

    public void setHidden(Date hidden) {
        this.hidden = hidden;
    }

    public Boolean getHiddenPostOnSchedule() {
        return hiddenPostOnSchedule;
    }

    public void setHiddenPostOnSchedule(Boolean hiddenPostOnSchedule) {
        this.hiddenPostOnSchedule = hiddenPostOnSchedule;
    }

    public Date getPostingAllowed() {
        return postingAllowed;
    }

    public void setPostingAllowed(Date postingAllowed) {
        this.postingAllowed = postingAllowed;
    }

    public Boolean getPostingAllowedPostOnSchedule() {
        return postingAllowedPostOnSchedule;
    }

    public void setPostingAllowedPostOnSchedule(Boolean postingAllowedPostOnSchedule) {
        this.postingAllowedPostOnSchedule = postingAllowedPostOnSchedule;
    }

    public Date getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Date readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getReadOnlyPostOnSchedule() {
        return readOnlyPostOnSchedule;
    }

    public void setReadOnlyPostOnSchedule(Boolean readOnlyPostOnSchedule) {
        this.readOnlyPostOnSchedule = readOnlyPostOnSchedule;
    }

    public Date getVisible() {
        return visible;
    }

    public void setVisible(Date visible) {
        this.visible = visible;
    }

    public Boolean getVisiblePostOnSchedule() {
        return visiblePostOnSchedule;
    }

    public void setVisiblePostOnSchedule(Boolean visiblePostOnSchedule) {
        this.visiblePostOnSchedule = visiblePostOnSchedule;
    }

}
