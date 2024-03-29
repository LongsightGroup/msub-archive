/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/tags/1.0/api/src/java/org/sakaiproject/assignment2/taggable/api/AssignmentActivityProducer.java $
 * $Id: AssignmentActivityProducer.java 61480 2009-06-29 18:39:09Z swgithen@mtu.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.assignment2.taggable.api;

import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableActivityProducer;
import org.sakaiproject.taggable.api.TaggableItem;

/**
 * A producer of assignments as taggable activities.
 * 
 * @author The Sakai Foundation.
 */
public interface AssignmentActivityProducer extends TaggableActivityProducer {

    /**
     * The type name of this producer.
     */
    public static final String PRODUCER_ID = AssignmentActivityProducer.class
    .getName();

    /**
     * Method to wrap the given assignment as a taggable activity.
     * 
     * @param assignment
     *            The assignment.
     * @return The assignment represented as a taggable activity.
     */
    public TaggableActivity getActivity(Assignment2 assignment);

    /**
     * Method to wrap the given assignment submission as a taggable item.
     * 
     * @param assignmentSubmission
     *            The assignment submission.
     * @param userId
     *            The identifier of the user that this item belongs to.
     * @return The assignment submission represented as a taggable item.
     */
    public TaggableItem getItem(AssignmentSubmission assignmentSubmission,
            String userId);
}
