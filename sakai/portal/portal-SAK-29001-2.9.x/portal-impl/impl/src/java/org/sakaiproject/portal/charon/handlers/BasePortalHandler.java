/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.portal.charon.handlers;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.portal.api.Portal;
import org.sakaiproject.portal.api.PortalHandler;
import org.sakaiproject.portal.api.PortalHandlerException;
import org.sakaiproject.portal.api.PortalRenderContext;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.util.ResourceLoader;

/**
 * Abstract class to hold common base methods for portal handlers.
 * 
 * @author ieb
 * @since Sakai 2.4
 * @version $Rev$
 * 
 */
public abstract class BasePortalHandler implements PortalHandler
{
	private static final Log log = LogFactory.getLog(BasePortalHandler.class);

	public BasePortalHandler()
	{
		urlFragment = "none";
	}

	protected PortalService portalService;

	protected Portal portal;

	private String urlFragment;

	protected ServletContext servletContext;

	public abstract int doGet(String[] parts, HttpServletRequest req,
			HttpServletResponse res, Session session) throws PortalHandlerException;

	// TODO: Go through and make sure to remove and test the mistaken code that
	// simply
	// calls doGet in doPost()
	public int doPost(String[] parts, HttpServletRequest req, HttpServletResponse res,
			Session session) throws PortalHandlerException
	{
		return NEXT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.charon.PortalHandler#deregister(org.sakaiproject.portal.charon.Portal)
	 */
	public void deregister(Portal portal)
	{
		this.portal = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.portal.charon.PortalHandler#register(org.sakaiproject.portal.charon.Portal)
	 */
	public void register(Portal portal, PortalService portalService,
			ServletContext servletContext)
	{
		this.portal = portal;
		this.portalService = portalService;
		this.servletContext = servletContext;

	}

	/**
	 * @return the servletContext
	 */
	public ServletContext getServletContext()
	{
		return servletContext;
	}

	/**
	 * @param servletContext
	 *        the servletContext to set
	 */
	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	/**
	 * @return the urlFragment
	 */
	public String getUrlFragment()
	{
		return urlFragment;
	}

	/**
	 * @param urlFragment the urlFragment to set
	 */
	public void setUrlFragment(String urlFragment)
	{
		this.urlFragment = urlFragment;
	}
	
	/**
	 * *
	 * 
	 * @return Locale based on its string representation (language_region)
	 */
	private Locale getLocaleFromString(String localeString)
	{
		String[] locValues = localeString.trim().split("_");
		if (locValues.length >= 3)
			return new Locale(locValues[0], locValues[1], locValues[2]); // language, country, variant
		else if (locValues.length == 2)
			return new Locale(locValues[0], locValues[1]); // language, country
		else if (locValues.length == 1)
			return new Locale(locValues[0]); // language
		else
			return Locale.getDefault();
	}
	
	private Locale setSiteLanguage(Site site)
	{
		ResourceLoader rl = new ResourceLoader();
				
		ResourcePropertiesEdit props = site.getPropertiesEdit();
				
		String locale_string = props.getProperty("locale_string");
			
		if(log.isDebugEnabled()){
			log.debug("setSiteLanguage - locale_string property: " + locale_string);
		}
		
		Locale loc;
				
		// if no language was specified when creating the site, set default language to session
		if(locale_string == null || locale_string == "")
		{					
			if(log.isDebugEnabled()){
				log.debug("setSiteLanguage - no locale, setting null.");
			}
			loc = rl.setContextLocale(null);
		}
		
		// if you have indicated a language when creating the site, set selected language to session
		else
		{				
			Locale locale = getLocaleFromString(locale_string);	
			
			if(log.isDebugEnabled()){
				log.debug("setSiteLanguage - locale: " + locale.toString());
			}
			loc = rl.setContextLocale(locale);			
		}

        return loc;
	}
	
	protected void addLocale(PortalRenderContext rcontext, Site site) {
		Locale locale = setSiteLanguage(site);	
		if(log.isDebugEnabled()) {
			log.debug("Locale for site " + site.getId() + " = " + locale.toString());
		}
        rcontext.put("locale", locale.toString());
	}
}
