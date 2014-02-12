/**********************************************************************************
 *
 * $URL: https://source.sakaiproject.org/contrib/etudes/melete/trunk/melete-impl/src/java/org/etudes/component/app/melete/BookmarkDB.java $
 * $Id: BookmarkDB.java 60573 2009-05-19 20:17:20Z mallika@etudes.org $
 ***********************************************************************************
 *
 * Copyright (c) 2010 Etudes, Inc.
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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;


public class BookmarkDB {
	private HibernateUtil hibernateUtil;
	private Log logger = LogFactory.getLog(BookmarkDB.class);

	/**
	 * default constructor
	 */
	public BookmarkDB() {

	}

	public List getBookmarks(String userId, String siteId)
	{
		List mbList = new ArrayList();
		try{
		     Session session = getHibernateUtil().currentSession();
		     Query q=session.createQuery("from Bookmark mb where mb.userId =:userId and mb.siteId=:siteId and mb.section.module.coursemodule.archvFlag = 0 order by mb.lastVisited desc,mb.bookmarkId asc");
			  q.setParameter("userId",userId);
			  q.setParameter("siteId", siteId);
			  mbList = q.list();

		}
	    catch (HibernateException he)
	    {
		  logger.error(he.toString());
	    }
	    finally
		{
	    	try
			  {
		      	hibernateUtil.closeSession();
			  }
		      catch (HibernateException he)
			  {
				  logger.error(he.toString());
			  }
		}
		return mbList;

	}

	public Bookmark getBookmark(String userId, String siteId, int sectionId)
	{
		Bookmark mb = null;
		try{
		    Session session = getHibernateUtil().currentSession();
		     Query q=session.createQuery("from Bookmark mb where mb.userId =:userId and mb.siteId=:siteId and mb.section.sectionId=:sectionId and mb.section.module.coursemodule.archvFlag = 0");
			  q.setParameter("userId",userId);
			  q.setParameter("siteId", siteId);
			  q.setParameter("sectionId", sectionId);
			  mb = (Bookmark)q.uniqueResult();

		}
	    catch (HibernateException he)
	    {
		  logger.error(he.toString());
	    }
	    finally
		{
	    	try
			  {
		      	hibernateUtil.closeSession();
			  }
		      catch (HibernateException he)
			  {
				  logger.error(he.toString());
			  }
		}
		return mb;
	}

	public int getLastVisitSectionId(String userId, String siteId)
	{
		int sectionId = 0;
		try{
		    Session session = getHibernateUtil().currentSession();
		     Query q=session.createQuery("select mb.section from Bookmark mb where mb.userId =:userId and mb.siteId=:siteId and mb.lastVisited=1 and mb.section.module.coursemodule.archvFlag = 0");
			  q.setParameter("userId",userId);
			  q.setParameter("siteId", siteId);
			  Section section = (Section)q.uniqueResult();
			  if (section.getModule().getModuleshdate().isVisibleFlag())
			  {
				  sectionId = section.getSectionId().intValue();
			  }
			  else
			  {
				  sectionId = 0;
			  }
		}
	    catch (HibernateException he)
	    {
		  logger.error(he.toString());
	    }
	    finally
		{
	    	try
			  {
		      	hibernateUtil.closeSession();
			  }
		      catch (HibernateException he)
			  {
				  logger.error(he.toString());
			  }
		}
		return sectionId;
	}

	private void adjustLastVisited(List mbList, int sectionId) throws Exception
	{
		Transaction tx = null;
	 	try
		{
	 		 Session session = hibernateUtil.currentSession();
		      tx = session.beginTransaction();
		      for (ListIterator i = mbList.listIterator(); i.hasNext(); )
		    {
			 Bookmark bm = (Bookmark)i.next();
			 if ((bm.getSectionId() != sectionId)&&(bm.getLastVisited().booleanValue() == true))
			 {
			   bm.setLastVisited(new Boolean(false));
			   session.saveOrUpdate(bm);
			 }
		     }
		   tx.commit();

	    }
	 	catch(StaleObjectStateException sose)
	     {
			if(tx !=null) tx.rollback();
			logger.error("stale object exception" + sose.toString());
			throw sose;
	     }
	    catch (HibernateException he)
	    {
		  logger.error(he.toString());
		  he.printStackTrace();
		  throw he;
	    }
	    catch (Exception e) {
	      if (tx!=null) tx.rollback();
	      logger.error(e.toString());
	      throw e;
	    }
	    finally
		{
	    	try
			  {
		      	hibernateUtil.closeSession();
			  }
		      catch (HibernateException he)
			  {
				  logger.error(he.toString());
				  throw he;
			  }
		}
	}
	public void setBookmark(Bookmark mb) throws Exception
	{
		Transaction tx = null;
	 	try
		{
	 		 Session session = hibernateUtil.currentSession();
		      tx = session.beginTransaction();

		      Query q=session.createQuery("select mb1 from Bookmark as mb1 where mb1.userId =:userId and mb1.siteId = :siteId and mb1.section.sectionId = :sectionId");
			  q.setParameter("userId",mb.getUserId());
			  q.setParameter("siteId",mb.getSiteId());
			  q.setParameter("sectionId",mb.getSectionId());
			  Bookmark find_mb = (Bookmark)q.uniqueResult();

		      if(find_mb == null)
		     	  session.save(mb);
		      else
		      {
		    	 find_mb.setTitle(mb.getTitle());
		    	 find_mb.setNotes(mb.getNotes());
		    	 find_mb.setLastVisited(mb.getLastVisited());
		    	 //TODO: Code in loop to set all other last visiteds to false
		    	 session.update(find_mb);
		      }


	      tx.commit();

	    }
	 	catch(StaleObjectStateException sose)
	     {
			if(tx !=null) tx.rollback();
			logger.error("stale object exception" + sose.toString());
			throw sose;
	     }
	    catch (HibernateException he)
	    {
		  logger.error(he.toString());
		  he.printStackTrace();
		  throw he;
	    }
	    catch (Exception e) {
	      if (tx!=null) tx.rollback();
	      logger.error(e.toString());
	      throw e;
	    }
	    finally
		{
	    	try
			  {
		      	hibernateUtil.closeSession();
			  }
		      catch (HibernateException he)
			  {
				  logger.error(he.toString());
				  throw he;
			  }
		}
	    //This code sets all other last visited flags to false
	    //for this user's site
	    if (mb.getLastVisited().booleanValue() == true)
	    {
	    	List mbList = getBookmarks(mb.getUserId(), mb.getSiteId());
	    	if (mbList != null)
	    	{
	    		if (mbList.size() > 0)
	    		{
	    			adjustLastVisited(mbList, mb.getSectionId());
	    		}
	    	}
	    }
	}

	  public void deleteBookmark(int bookmarkId) throws Exception
      {
        Transaction tx = null;
        Bookmark mb = null;
        String delBookmarkStr = "delete Bookmark bm where bm.bookmarkId=:bookmarkId";
        try{
               Session session = getHibernateUtil().currentSession();
               tx = session.beginTransaction();
               int deletedEntities = session.createQuery(delBookmarkStr).setInteger("bookmarkId",bookmarkId).executeUpdate();
               tx.commit();
           }
           catch (HibernateException he)
           {
               if (tx!=null) tx.rollback();
               logger.error(he.toString());
               throw he;
           }
           catch (Exception e) {
              if (tx!=null) tx.rollback();
               logger.error(e.toString());
               throw e;
            }
            finally
            {
            	try
    			{
    				hibernateUtil.closeSession();
    			}
    			catch (HibernateException he)
    			{
    				logger.error(he.toString());
    				throw he;
    			}
            }
      }


	/**
	 * @return Returns the hibernateUtil.
	 */
	public HibernateUtil getHibernateUtil() {
		return hibernateUtil;
	}
	/**
	 * @param hibernateUtil The hibernateUtil to set.
	 */
	public void setHibernateUtil(HibernateUtil hibernateUtil) {
		this.hibernateUtil = hibernateUtil;
	}




}
