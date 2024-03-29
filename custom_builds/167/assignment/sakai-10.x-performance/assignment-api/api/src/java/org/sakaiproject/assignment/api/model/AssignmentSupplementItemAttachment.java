/**********************************************************************************
 * $URL: https://svn.rsmart.com/svn/vendor/branches/sakai/rsmart-cle/assignment/assignment-api/api/src/java/org/sakaiproject/assignment/api/model/AssignmentSupplementItemAttachment.java $
 * $Id: AssignmentSupplementItemAttachment.java 18770 2010-01-21 04:53:30Z jbush $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.assignment.api.model;

/**
 * the attachment for the AssigmentSupplementItem object
 * @author zqian
 *
 */
public class AssignmentSupplementItemAttachment {
	private Long id;
	private String attachmentId;
	private AssignmentSupplementItemWithAttachment assignmentSupplementItemWithAttachment;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public AssignmentSupplementItemWithAttachment getAssignmentSupplementItemWithAttachment()
	{
		return this.assignmentSupplementItemWithAttachment;
	}
	public void setAssignmentSupplementItemWithAttachment(AssignmentSupplementItemWithAttachment assignmentSupplementItemWithAttachment)
	{
		this.assignmentSupplementItemWithAttachment = assignmentSupplementItemWithAttachment;
	}
	

}
