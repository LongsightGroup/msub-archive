/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.0.x/cm-service/cm-impl/hibernate-impl/impl/src/test/org/sakaiproject/coursemanagement/test/ClassPathCMSyncJobTest.java $
 * $Id: ClassPathCMSyncJobTest.java 59674 2009-04-03 23:05:58Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.coursemanagement.test;

import java.util.List;

import junit.framework.Assert;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.coursemanagement.impl.job.ClassPathCMSyncJob;

public class ClassPathCMSyncJobTest extends CourseManagementTestBase {
	private ClassPathCMSyncJob job;
	private CourseManagementService cmService;
	private CourseManagementAdministration cmAdmin;
	
	protected void onSetUpInTransaction() throws Exception {
		job = (ClassPathCMSyncJob)applicationContext.getBean(ClassPathCMSyncJob.class.getName());
		cmService = (CourseManagementService)applicationContext.getBean(CourseManagementService.class.getName());
		cmAdmin = (CourseManagementAdministration)applicationContext.getBean(CourseManagementAdministration.class.getName());
		job.syncAllCmObjects();
	}
	
	public void testAcademicSessionsLoaded() throws Exception {
		// Ensure that the academic sessions were loaded
		List asList = cmService.getAcademicSessions();
		Assert.assertEquals(2, asList.size());
	}
	
	public void testAcademicSessionsReconciled() throws Exception {
		// Update an AS manually
		AcademicSession academicSession = cmService.getAcademicSession("fall_2006");
		
		String oldTitle = academicSession.getTitle();
		
		academicSession.setTitle("new title");
		cmAdmin.updateAcademicSession(academicSession);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getAcademicSession("fall_2006").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getAcademicSession("fall_2006").getTitle());		
	}
	
	public void testCanonicalCoursesLoaded() throws Exception {
		// Ensure that the canonical courses were loaded
		try {
			cmService.getCanonicalCourse("biology_101");
			cmService.getCanonicalCourse("chemistry_101");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testCanonicalCoursesReconciled() throws Exception {
		// Update a cc manually
		CanonicalCourse cc = cmService.getCanonicalCourse("biology_101");
		String oldTitle = cc.getTitle();
		cc.setTitle("new title");
		cmAdmin.updateCanonicalCourse(cc);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getCanonicalCourse("biology_101").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getCanonicalCourse("biology_101").getTitle());
	}

	public void testCourseOfferingsLoaded() throws Exception {
		// Ensure that the course offerings were loaded
		try {
			cmService.getCourseOffering("biology_101_01");
			cmService.getCourseOffering("chemistry_101_01");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testCourseOfferingsReconciled() throws Exception {
		// Update a co manually
		CourseOffering co = cmService.getCourseOffering("biology_101_01");
		String oldTitle = co.getTitle();
		co.setTitle("new title");
		cmAdmin.updateCourseOffering(co);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getCourseOffering("biology_101_01").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getCourseOffering("biology_101_01").getTitle());
	}

	public void testCourseOfferingMembersReconciled() throws Exception {
		// Ensure that the memberships were loaded
		Membership member = (Membership)cmService.getCourseOfferingMemberships("biology_101_01").iterator().next();
		Assert.assertEquals("assistant", member.getRole());
		
		// Add a new membership
		Membership newMember = cmAdmin.addOrUpdateCourseOfferingMembership("foo", "bar", "biology_101_01","active");
		
		// Ensure it was added
		Assert.assertTrue(cmService.getCourseOfferingMemberships("biology_101_01").contains(newMember));
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the new member was removed
		Assert.assertFalse(cmService.getCourseOfferingMemberships("biology_101_01").contains(newMember));
	}

	public void testSectionsLoaded() throws Exception {
		// Ensure that the sections were loaded
		try {
			cmService.getSection("biology_101_01_lec01");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testSectionsReconciled() throws Exception {
		// Update a sec manually
		Section sec = cmService.getSection("biology_101_01_lec01");
		String oldTitle = sec.getTitle();
		sec.setTitle("new title");
		cmAdmin.updateSection(sec);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getSection("biology_101_01_lec01").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getSection("biology_101_01_lec01").getTitle());
	}

	public void testSectionMembersReconciled() throws Exception {
		// Ensure that the memberships were loaded
		Membership member = (Membership)cmService.getSectionMemberships("biology_101_01_lec01").iterator().next();
		Assert.assertEquals("assistant", member.getRole());
		
		// Add a new membership
		Membership newMember = cmAdmin.addOrUpdateSectionMembership("foo", "bar", "biology_101_01_lec01","active");
		
		// Ensure it was added
		Assert.assertTrue(cmService.getSectionMemberships("biology_101_01_lec01").contains(newMember));
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the new member was removed
		Assert.assertFalse(cmService.getSectionMemberships("biology_101_01_lec01").contains(newMember));
	}

	public void testEnrollmentSetsLoaded() throws Exception {
		// Ensure that the enrollmentSets were loaded
		try {
			cmService.getEnrollmentSet("biology_101_01_lec01_es");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testEnrollmentSetsReconciled() throws Exception {
		// Update a enrollment set manually
		EnrollmentSet es = cmService.getEnrollmentSet("biology_101_01_lec01_es");
		String oldTitle = es.getTitle();
		es.setTitle("new title");
		cmAdmin.updateEnrollmentSet(es);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getEnrollmentSet("biology_101_01_lec01_es").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getEnrollmentSet("biology_101_01_lec01_es").getTitle());
	}

	public void testCourseSetsLoaded() throws Exception {
		// Ensure that the courseSets were loaded
		try {
			cmService.getCourseSet("ucb");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testCourseSetsReconciled() throws Exception {
		// Update a course set manually
		CourseSet es = cmService.getCourseSet("ucb");
		String oldTitle = es.getTitle();
		es.setTitle("new title");
		cmAdmin.updateCourseSet(es);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getCourseSet("ucb").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getCourseSet("ucb").getTitle());
	}
	
	public void testCourseSetMembersReconciled() throws Exception {
		// Ensure that the memberships were loaded
		Membership member = (Membership)cmService.getCourseSetMemberships("ucb").iterator().next();
		Assert.assertEquals("birgeneau", member.getUserId());
		Assert.assertEquals("president", member.getRole());
		
		// Add a new membership
		Membership newMember = cmAdmin.addOrUpdateCourseSetMembership("foo", "bar", "ucb", "active");
		
		// Ensure it was added
		Assert.assertTrue(cmService.getCourseSetMemberships("ucb").contains(newMember));
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the new member was removed
		Assert.assertFalse(cmService.getCourseSetMemberships("ucb").contains(newMember));
	}
	
	public void testEnrollmentsLoaded() throws Exception {
		Assert.assertNotNull(cmService.findEnrollment("student1", "biology_101_01_lec01_es"));
		Assert.assertNotNull(cmService.findEnrollment("student2", "biology_101_01_lec01_es"));
	}

	public void testEnrollmentsReconciled() throws Exception {
		// Remove an enrollment
		cmAdmin.removeEnrollment("student1", "biology_101_01_lec01_es");
		
		// Ensure that it's been dropped
		Assert.assertTrue(cmService.findEnrollment("student1",  "biology_101_01_lec01_es").isDropped());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation added the enrollment
		Assert.assertFalse(cmService.findEnrollment("student1",  "biology_101_01_lec01_es").isDropped());
	}

	public void testInstructorsLoaded() throws Exception {
		Assert.assertTrue(cmService.getInstructorsOfRecordIds("biology_101_01_lec01_es").contains("instructor1"));
		Assert.assertTrue(cmService.getInstructorsOfRecordIds("biology_101_01_lec01_es").contains("instructor2"));
	}

	public void testInstructorsReconciled() throws Exception {
		// Remove an instructor
		EnrollmentSet es = cmService.getEnrollmentSet("biology_101_01_lec01_es");
		es.getOfficialInstructors().remove("instructor1");
		
		// Ensure that the instructor is gone
		Assert.assertFalse(cmService.getInstructorsOfRecordIds("biology_101_01_lec01_es").contains("instructor1"));
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation added the instructor
		Assert.assertTrue(cmService.getInstructorsOfRecordIds("biology_101_01_lec01_es").contains("instructor1"));
	}
	
	public void testCurrentAcademicSessions() throws Exception {
		List<AcademicSession> currentAcademicSessions = cmService.getCurrentAcademicSessions();
		Assert.assertEquals(1, currentAcademicSessions.size());
		Assert.assertEquals("winter_2007", currentAcademicSessions.get(0).getEid());
	}
}
