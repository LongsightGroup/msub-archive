package org.sakaiproject.content.impl.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.impl.BaseContentService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.test.SakaiKernelTestBase;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Test for deleting files.
 */
public class ContentHostingServiceRecycleTest  extends SakaiKernelTestBase {

    public static Test suite()
    {
        TestSetup setup = new TestSetup(new TestSuite(ContentHostingServiceRecycleTest.class))
        {

            protected void setUp() throws Exception
            {
                // These properties need to be dynamic so they work across linux/mac/windows.
                Properties properties = new Properties();
                properties.put("org.sakaiproject.content.api.ContentHostingService@bodyPath",
                        Files.createTempDirectory(FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir")), "files").toString());
                properties.put("org.sakaiproject.content.api.ContentHostingService@bodyPathDeleted",
                        Files.createTempDirectory(FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir")), "deleted").toString());
                oneTimeSetup(null, null, properties);
            }
            protected void tearDown() throws Exception
            {
                oneTimeTearDown();
            }
        };
        return setup;
    }

    public void testDeleteResource() throws Exception {
        ContentHostingService ch = getService(ContentHostingService.class);
        SessionManager sm = getService(SessionManager.class);
        Session session = sm.getCurrentSession();
        session.setUserId("admin");
        session.setUserEid("admin");

        // Create a file
        String filename = "/"+ UUID.randomUUID().toString();
        ContentResourceEdit resource = ch.addResource(filename);
        resource.setContent("Hello World".getBytes());
        ch.commitResource(resource);

        // Delete the file (into the recycle bin)
        ch.removeResource(filename);
        ch.restoreResource(filename);
    }

    /**
     * This is to test that you can only store one copy of a deleted file.
     * The database schema and UI appear to support having multiple copies of a file presented to a user,
     * however when you restore a file you only pass in the ID and not the UUID so there's no way to
     * determine which copy of a file the user requested be restored. In the future we may wish to change
     * this and support storing multiple old copies of a file.
     *
     * @throws Exception
     */
    public void testDeleteResourceTwice() throws Exception {
        ContentHostingService ch = getService(ContentHostingService.class);
        SessionManager sm = getService(SessionManager.class);
        ThreadLocalManager tl = getService(ThreadLocalManager.class);
        reset(tl, sm);

        // Create a file
        String filename = "/"+ UUID.randomUUID().toString();
        ContentResourceEdit resource = ch.addResource(filename);
        resource.setContent("First".getBytes());
        ch.commitResource(resource);

        // Delete the file (into the recycle bin)
        ch.removeResource(filename);

        ContentResourceEdit resource2 = ch.addResource(filename);
        resource2.setContent("Second".getBytes());
        ch.commitResource(resource2);

        ch.removeResource(filename);

        try {
            ch.getResource(filename);
            fail("We shouldn't be able to find: "+ filename);
        } catch (IdUnusedException e) {
            // Expected
        }

        List<ContentResource> allDeleted = ch.getAllDeletedResources("/");
        int found = 0;
        for (ContentResource deleted : allDeleted) {
            if (deleted.getId().equals(filename)) {
                found++;
            }
        }
        assertEquals("There should only be one copy of the file in the recycle bin.", 1, found);
    }

    /**
     * Clear out any threadlocals and reset the session to be admin.
     * @param tl ThreadLocalManager service.
     * @param sm SessionManager service.
     */
    private void reset(ThreadLocalManager tl, SessionManager sm) {
        tl.clear();
        Session session = sm.getCurrentSession();
        session.setUserId("admin");
        session.setUserEid("admin");
    }
}
