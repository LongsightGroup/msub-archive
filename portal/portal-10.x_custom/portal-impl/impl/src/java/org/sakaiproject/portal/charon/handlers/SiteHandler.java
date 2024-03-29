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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.portal.api.Portal;
import org.sakaiproject.portal.api.PortalService;
import org.sakaiproject.portal.api.PortalHandlerException;
import org.sakaiproject.portal.api.PortalRenderContext;
import org.sakaiproject.portal.api.SiteView;
import org.sakaiproject.portal.api.StoredState;
import org.sakaiproject.portal.charon.site.AllSitesViewImpl;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.ToolException;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.PreferencesService;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.cover.ActiveToolManager;
import org.sakaiproject.util.Web;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.portal.util.URLUtils;
import org.sakaiproject.portal.util.ToolUtils;
import org.sakaiproject.portal.util.PortalUtils;
import org.sakaiproject.portal.util.ByteArrayServletResponse;
import org.sakaiproject.util.Validator;

import org.sakaiproject.portal.charon.handlers.PDAHandler;

/**
 * @author ieb
 * @since Sakai 2.4
 * @version $Rev$
 */
public class SiteHandler extends WorksiteHandler
{

	private static final String INCLUDE_SITE_NAV = "include-site-nav";

	private static final String INCLUDE_LOGO = "include-logo";

	private static final String INCLUDE_TABS = "include-tabs";

	private static final Log log = LogFactory.getLog(SiteHandler.class);

	private static final String URL_FRAGMENT = "site";

	private int configuredTabsToDisplay = 5;

	private boolean useDHTMLMore = false;
	
	private static ResourceLoader rb = new ResourceLoader("sitenav");
	
	// When these strings appear in the URL they will be replaced by a calculated value based on the context.
	// This can be replaced by the users myworkspace.
	private String mutableSitename ="-";
	// This can be replaced by the page on which a tool appears.
	private String mutablePagename ="-";

	public SiteHandler()
	{
		setUrlFragment(SiteHandler.URL_FRAGMENT);
		configuredTabsToDisplay = ServerConfigurationService.getInt(
				Portal.CONFIG_DEFAULT_TABS, 5);
		useDHTMLMore = Boolean.valueOf(ServerConfigurationService.getBoolean(
				"portal.use.dhtml.more", true));
		mutableSitename =  ServerConfigurationService.getString("portal.mutable.sitename", "-");
		mutablePagename =  ServerConfigurationService.getString("portal.mutable.pagename", "-");
	}

	@Override
	public int doGet(String[] parts, HttpServletRequest req, HttpServletResponse res,
			Session session) throws PortalHandlerException
	{
		if ((parts.length >= 2) && (parts[1].equals(SiteHandler.URL_FRAGMENT)))
		{
			// This is part of the main portal so we simply remove the attribute
			session.setAttribute(PortalService.SAKAI_CONTROLLING_PORTAL, null);
			try
			{
				// site might be specified
				String siteId = null;
				if (parts.length >= 3)
				{
					siteId = parts[2];
				}
				
				// recognize an optional page/pageid
				String pageId = null;
				String toolId = null;

				// may also have the tool part, so check that length is 5 or greater.
				if ((parts.length >= 5) && (parts[3].equals("page")))
				{
					pageId = parts[4];
				}

				// Tool resetting URL - clear state and forward to the real tool
				// URL
				// /portal/site/site-id/tool-reset/toolId
				// 0 1 2 3 4
				if ((siteId != null) && (parts.length == 5) && (parts[3].equals("tool-reset")))
				{
					toolId = parts[4];
					String toolUrl = req.getContextPath() + "/site/" + siteId + "/tool"
						+ Web.makePath(parts, 4, parts.length);
					String queryString = Validator.generateQueryString(req);
					if (queryString != null)
					{
						toolUrl = toolUrl + "?" + queryString;
					}
					portalService.setResetState("true");
					res.sendRedirect(toolUrl);
					return RESET_DONE;
				}

				// may also have the tool part, so check that length is 5 or greater.
				if ((parts.length >= 5) && (parts[3].equals("tool")))
				{
					toolId = parts[4];
				}

				String commonToolId = null;
				
				if(parts.length == 4)
				{
					commonToolId = parts[3];
				}

				doSite(req, res, session, siteId, pageId, toolId, commonToolId, parts,
						req.getContextPath() + req.getServletPath());
				return END;
			}
			catch (Exception ex)
			{
				throw new PortalHandlerException(ex);
			}
		}
		else
		{
			return NEXT;
		}
	}

	public void doSite(HttpServletRequest req, HttpServletResponse res, Session session,
			String siteId, String pageId, String toolId,
			String commonToolId, String [] parts, String toolContextPath) throws ToolException,
			IOException
	{		
				
		// default site if not set
		String userId = session.getUserId();
		if (siteId == null)
		{
			if (userId == null)
			{
				siteId = portal.getSiteHelper().getGatewaySiteId();
				if (siteId == null)
				{
					siteId = ServerConfigurationService.getGatewaySiteId();
				}
			}
			else
			{
				// TODO Should maybe switch to portal.getSiteHelper().getMyWorkspace()
				AllSitesViewImpl allSites = (AllSitesViewImpl)portal.getSiteHelper().getSitesView(SiteView.View.ALL_SITES_VIEW, req, session, siteId);
				List<Map> sites = (List<Map>)allSites.getRenderContextObject();
				if (sites.size() > 0) {
					siteId = (String)sites.get(0).get("siteId");
				}
				else
					siteId = SiteService.getUserSiteId(userId);
			}
		}

		// Can get a URL like /portal/site/-/page/-/tool/sakai.rwiki.  
		// The "mutable" site and page can not be given specific values since the 
		// final resolution depends on looking up the specific placement of the tool 
		// with this common id in the my workspace for this user.
		
		// check for a mutable site to be resolved here
		if (mutableSitename.equalsIgnoreCase(siteId) && (session.getUserId() != null)) {
			siteId = SiteService.getUserSiteId(userId);
		}

		// find the site, for visiting
		boolean siteDenied = false;
		Site site = null;
		try
		{
			Set<SecurityAdvisor> advisors = (Set<SecurityAdvisor>)session.getAttribute("sitevisit.security.advisor");
			if (advisors != null) {
				for (SecurityAdvisor advisor:advisors) {
					SecurityService.pushAdvisor(advisor);
					//session.removeAttribute("sitevisit.security.advisor");
				}
			}
			// This should understand aliases as well as IDs
			site = portal.getSiteHelper().getSiteVisit(siteId);
			
			// SAK-20509 remap the siteId from the Site object we now have, since it may have originally been an alias, but has since been translated.
			siteId = site.getId();
			
		}
		catch (IdUnusedException e)
		{
		}
		catch (PermissionException e)
		{
			if (ServerConfigurationService.getBoolean("portal.redirectJoin", true) &&
					userId != null && portal.getSiteHelper().isJoinable(siteId, userId))
			{
				String redirectUrl = Web.returnUrl(req, "/join/"+siteId);
				res.sendRedirect(redirectUrl);
				return;
			}

			siteDenied = true;
		}

		if (site == null)
		{				
			// if not logged in, give them a chance
			if (userId == null)
			{
				StoredState ss = portalService.newStoredState("directtool", "tool");
				ss.setRequest(req);
				ss.setToolContextPath(toolContextPath);
				portalService.setStoredState(ss);
				portal.doLogin(req, res, session, URLUtils.getSafePathInfo(req), false);
			}
			else
			{
				// Post an event for denied site visits by known users.
				// This can be picked up to check the user state and refresh it if stale,
				// such as showing links to sites that are no longer accessible.
				// It is also helpful for event log analysis for user trouble or bad behavior.
				if (siteDenied)
				{
					Event event = EventTrackingService.newEvent(SiteService.EVENT_SITE_VISIT_DENIED, siteId, false);
					EventTrackingService.post(event);
				}
				portal.doError(req, res, session, Portal.ERROR_SITE);
			}
			return;
		}
		
		// Supports urls like: /portal/site/{SITEID}/sakai.announcements
		if(site != null && commonToolId != null)
		{
			ToolConfiguration tc = null;
			if(!commonToolId.startsWith("sakai."))
			{
				// Try the most likely case first, that of common tool ids starting with 'sakai.'
				tc = site.getToolForCommonId("sakai." + commonToolId);
				if(tc == null)
				{
					// That failed, try the supplied tool id
					tc = site.getToolForCommonId(commonToolId);
				}
			}
			
			if(tc != null)
			{
				pageId = tc.getPageId();
			}
		}

		// Find the pageId looking backwards through the toolId
		if(site != null && pageId == null && toolId != null ) {
			SitePage p = (SitePage) ToolUtils.getPageForTool(site, toolId);
			if ( p != null ) pageId = p.getId();
		}

		// if no page id, see if there was a last page visited for this site
		if (pageId == null)
		{
			pageId = (String) session.getAttribute(Portal.ATTR_SITE_PAGE + siteId);
		}

		// If the page is the mutable page name then look up the 
		// real page id from the tool name.
		if (mutablePagename.equalsIgnoreCase(pageId)) {
			pageId = findPageIdFromToolId(pageId, URLUtils.getSafePathInfo(req), site);
		}
		
		// clear the last page visited
		session.removeAttribute(Portal.ATTR_SITE_PAGE + siteId);

		// form a context sensitive title
		String title = ServerConfigurationService.getString("ui.service","Sakai") + " : "
				+ site.getTitle();

		// Lookup the page in the site - enforcing access control
		// business rules
		SitePage page = portal.getSiteHelper().lookupSitePage(pageId, site);
		if (page != null)
		{
			// store the last page visited
			session.setAttribute(Portal.ATTR_SITE_PAGE + siteId, page.getId());
			title += " : " + page.getTitle();
		}

		// Check for incomplete URLs in the case of inlined tools
		String trinity = ServerConfigurationService.getString(ToolUtils.PORTAL_INLINE_EXPERIMENTAL, "false");
		if ( "true".equals(trinity) && toolId == null) {
			String pagerefUrl = ToolUtils.getPageUrl(req, site, page, getUrlFragment(),
				false, null, null);
			// http://localhost:8080/portal/site/963b28b/tool/0996adf
			String[] pieces = pagerefUrl.split("/");
			if ( pieces.length > 6 && "tool".equals(pieces[6]) ) {
				// SAK-25503 - This probably should be a log.debug later
				String queryString = req.getQueryString();
				if ( queryString != null ) pagerefUrl = pagerefUrl + '?' + queryString;
				log.warn("Redirecting tool inline url: "+pagerefUrl);
				res.sendRedirect(pagerefUrl);
				return;
			}
		}



		// Create and initialize a copy of the PDA Handler
		PDAHandler pdah = new PDAHandler();
		pdah.register(portal,portalService,servletContext);

		// See if we can buffer the content, if not, pass the request through
		String TCP = null;
		String toolPathInfo = null;
		boolean allowBuffer = false;
		Object BC = null;

		ToolConfiguration siteTool = null;
		if ( toolId != null ) {
			siteTool = SiteService.findTool(toolId);
			if ( siteTool != null && parts.length >= 5 ) {
				commonToolId = siteTool.getToolId();

				// Does the tool allow us to buffer?
				allowBuffer = pdah.allowBufferContent(req, siteTool);

				if ( allowBuffer ) {
					TCP = req.getContextPath() + req.getServletPath() + Web.makePath(parts, 1, 5);
					toolPathInfo = Web.makePath(parts, 5, parts.length);

					// Should we bypass buffering based on the request?
					boolean matched = pdah.checkBufferBypass(req, siteTool);

					if ( matched ) {
						ActiveTool tool = ActiveToolManager.getActiveTool(commonToolId);
						portal.forwardTool(tool, req, res, siteTool,
							siteTool.getSkin(), TCP, toolPathInfo);
						return;
					}
				}
			}
		}

		// start the response
		String siteType = portal.calcSiteType(siteId);
		PortalRenderContext rcontext = portal.startPageContext(siteType, title, site
				.getSkin(), req);

		if ( allowBuffer ) {
			BC = pdah.bufferContent(req, res, session, toolId,
					TCP, toolPathInfo, siteTool);

			// If the buffered response was not parseable
			if ( BC instanceof ByteArrayServletResponse ) {
				ByteArrayServletResponse bufferResponse = (ByteArrayServletResponse) BC;
				StringBuffer queryUrl = req.getRequestURL();
				String queryString = req.getQueryString();
				if ( queryString != null ) queryUrl.append('?').append(queryString);
				String msg = "Post buffer bypass CTI="+commonToolId+" URL="+queryUrl;
				String redir = bufferResponse.getRedirect();

				// We are concerned when we neither got output, nor a redirect
				if ( redir != null ) {
					msg = msg + " redirect to="+redir;
					log.debug(msg);
				} else {
					log.warn(msg);
				}
				bufferResponse.forwardResponse();
				return;
			}
		}


		// Include the buffered content if we have it
		if ( BC instanceof Map ) {
			if ( req.getMethod().equals("POST") ) {
				StringBuffer queryUrl = req.getRequestURL();
				String queryString = req.getQueryString();
				if ( queryString != null ) queryUrl.append('?').append(queryString);
				log.warn("It is tacky to return markup on a POST CTI="+commonToolId+" URL="+queryUrl);
			}
			rcontext.put("bufferedResponse", Boolean.TRUE);
			Map<String,String> bufferMap = (Map<String,String>) BC;
			rcontext.put("responseHead", (String) bufferMap.get("responseHead"));
			rcontext.put("responseBody", (String) bufferMap.get("responseBody"));
		}


		// Have we been requested to display minimized and are we logged in?
		if (session.getUserId() != null ) {
			Cookie c = portal.findCookie(req, portal.SAKAI_NAV_MINIMIZED);
                        String reqParm = req.getParameter(portal.SAKAI_NAV_MINIMIZED);
                	String minStr = ServerConfigurationService.getString("portal.allow.auto.minimize","true");
                	if ( c != null && "true".equals(c.getValue()) ) {
				rcontext.put(portal.SAKAI_NAV_MINIMIZED, Boolean.TRUE);
			} else if ( reqParm != null &&  "true".equals(reqParm) && ! "false".equals(minStr) ) {
				rcontext.put(portal.SAKAI_NAV_MINIMIZED, Boolean.TRUE);
			}
		}

		rcontext.put("siteId", siteId);
		boolean showShortDescription = Boolean.valueOf(ServerConfigurationService.getBoolean("portal.title.shortdescription.show", false));

		if (showShortDescription) {
			rcontext.put("shortDescription", Web.escapeHtml(site.getShortDescription()));
		}
		
		if (SiteService.isUserSite(siteId)){
			rcontext.put("siteTitle", rb.getString("sit_mywor"));
		}else{
			rcontext.put("siteTitle", Web.escapeHtml(site.getTitle()));
		}
		
		addLocale(rcontext, site, session.getUserId());
		
		includeSiteNav(rcontext, req, session, siteId);

		includeWorksite(rcontext, res, req, session, site, page, toolContextPath,
					getUrlFragment());

		// Include sub-sites if appropriate
		// TODO: Think through whether we want reset tools or not
		portal.includeSubSites(rcontext, req, session, siteId, req.getContextPath()
				+ req.getServletPath(), getUrlFragment(),
				/* resetTools */false);

		portal.includeBottom(rcontext);

		//Log the visit into SAKAI_EVENT - begin
		try{
			boolean presenceEvents = ServerConfigurationService.getBoolean("presence.events.log", true);
			if (presenceEvents)
				org.sakaiproject.presence.cover.PresenceService.setPresence(siteId + "-presence");
		}catch(Exception e){}
		//End - log the visit into SAKAI_EVENT		

		rcontext.put("currentUrlPath", Web.serverUrl(req) + req.getContextPath()
				+ URLUtils.getSafePathInfo(req));

		doSendResponse(rcontext, res, null);

		StoredState ss = portalService.getStoredState();
		if (ss != null && toolContextPath.equals(ss.getToolContextPath()))
		{
			// This request is the destination of the request
			portalService.setStoredState(null);
		}
		
		
	}

	/*
	 * If the page id is the mutablePageId then see if can resolve it from the
	 * the placement of the tool with a supplied tool id.
	 */
	private String findPageIdFromToolId(String pageId, String toolContextPath,
			Site site) {

		// If still can't find page id see if can determine it from a well known
		// tool id (assumes that such a tool is in the site and the first instance of 
		// the tool found would be the right one).
		String toolSegment = "/tool/";
		String toolId = null;

			try
			{
			// does the URL contain a tool id?
			if (toolContextPath.contains(toolSegment)) {
				toolId = toolContextPath.substring(toolContextPath.lastIndexOf(toolSegment)+toolSegment.length());
				ToolConfiguration toolConfig = site.getToolForCommonId(toolId);
				if (log.isDebugEnabled()) {
					log.debug("trying to resolve page id from toolId: ["+toolId+"]");
				}
				if (toolConfig != null) {
					pageId = toolConfig.getPageId();
				}
			}

			}
			catch (Exception e) {
				log.error("exception resolving page id from toolid :["+toolId+"]",e);
			}

		return pageId;
	}

	/**
	 * Does the final render response, classes that extend this class
	 * may/will want to override this method to use their own template
	 * 
	 * @param rcontext
	 * @param res
	 * @param object
	 * @param b
	 * @throws IOException
	 */
	protected void doSendResponse(PortalRenderContext rcontext, HttpServletResponse res,
			String contentType) throws IOException
	{
		portal.sendResponse(rcontext, res, "site", null);
	}

	protected void includeSiteNav(PortalRenderContext rcontext, HttpServletRequest req,
			Session session, String siteId)
	{
		if (rcontext.uses(INCLUDE_SITE_NAV))
		{

			boolean loggedIn = session.getUserId() != null;
			boolean topLogin = ServerConfigurationService.getBoolean("top.login", true);


			String accessibilityURL = ServerConfigurationService
					.getString("accessibility.url");
			rcontext.put("siteNavHasAccessibilityURL", Boolean
					.valueOf((accessibilityURL != null && !accessibilityURL.equals(""))));
			rcontext.put("siteNavAccessibilityURL", accessibilityURL);
			// rcontext.put("siteNavSitAccessability",
			// Web.escapeHtml(rb.getString("sit_accessibility")));
			// rcontext.put("siteNavSitJumpContent",
			// Web.escapeHtml(rb.getString("sit_jumpcontent")));
			// rcontext.put("siteNavSitJumpTools",
			// Web.escapeHtml(rb.getString("sit_jumptools")));
			// rcontext.put("siteNavSitJumpWorksite",
			// Web.escapeHtml(rb.getString("sit_jumpworksite")));

			rcontext.put("siteNavTopLogin", Boolean.valueOf(topLogin));
			rcontext.put("siteNavLoggedIn", Boolean.valueOf(loggedIn));

			try
			{
				if (loggedIn)
				{
					includeLogo(rcontext, req, session, siteId);
					includeTabs(rcontext, req, session, siteId, getUrlFragment(), false);
				}
				else
				{
					includeLogo(rcontext, req, session, siteId);
					if (portal.getSiteHelper().doGatewaySiteList())
						includeTabs(rcontext, req, session, siteId, getUrlFragment(),
								false);
				}
			}
			catch (Exception any)
			{
			}
		}
	}

	public void includeLogo(PortalRenderContext rcontext, HttpServletRequest req,
			Session session, String siteId) throws IOException
	{
		if (rcontext.uses(INCLUDE_LOGO))
		{

			String skin = getSiteSkin(siteId);
			String skinRepo = ServerConfigurationService.getString("skin.repo");
			rcontext.put("logoSkin", skin);
			rcontext.put("logoSkinRepo", skinRepo);
			String siteType = portal.calcSiteType(siteId);
			String cssClass = (siteType != null) ? siteType : "undeterminedSiteType";
			rcontext.put("logoSiteType", siteType);
			rcontext.put("logoSiteClass", cssClass);
			portal.includeLogin(rcontext, req, session);
		}
	}

	private String getSiteSkin(String siteId)
	{
		// First, try to get the skin the default way
		String skin = SiteService.getSiteSkin(siteId);
		// If this fails, try to get the real site id if the site is a user site
		if (skin == null && SiteService.isUserSite(siteId))
		{
			try
			{
				String userId = SiteService.getSiteUserId(siteId);
				
				// If the passed siteId is the users EID, convert it to the internal ID.
				// Most lookups should be EID, if most URLs contain internal ID, this results in lots of cache misses.
				try
				{
					userId = UserDirectoryService.getUserId(userId);
				}
				catch (UserNotDefinedException unde)
				{
					// Ignore
				}
				String alternateSiteId = SiteService.getUserSiteId(userId);
				skin = SiteService.getSiteSkin(alternateSiteId);
			}
			catch (Exception e)
			{
				// Ignore
			}
		}

		if (skin == null)
		{
			skin = ServerConfigurationService.getString("skin.default");
		}
		String templates = ServerConfigurationService.getString("portal.templates", "neoskin");
		String prefix = portalService.getSkinPrefix();
        // Don't add the prefix twice
        if ( "neoskin".equals(templates) && !StringUtils.startsWith(skin, prefix) ) skin = prefix + skin;
		return skin;
	}

	public void includeTabs(PortalRenderContext rcontext, HttpServletRequest req,
			Session session, String siteId, String prefix, boolean addLogout)
			throws IOException
	{

		if (rcontext.uses(INCLUDE_TABS))
		{

			// for skinning
			String siteType = portal.calcSiteType(siteId);

			// If we have turned on auto-state reset on navigation, we generate
			// the "site-reset" "worksite-reset" and "gallery-reset" urls
            if ("true".equalsIgnoreCase(ServerConfigurationService
					.getString(Portal.CONFIG_AUTO_RESET)))
			{
				prefix = prefix + "-reset";
			}

			boolean loggedIn = session.getUserId() != null;
			
			// Check to see if we display a link in the UI for swapping the view
			boolean roleswapcheck = false; // This variable will tell the UI if we will display any role swapping component; false by default
			String roleswitchvalue = SecurityService.getUserEffectiveRole(SiteService.siteReference(siteId)); // checks the session for a role swap value
			boolean roleswitchstate = false; // This variable determines if the site is in the switched state or not; false by default
			boolean allowroleswap = SiteService.allowRoleSwap(siteId) && !SecurityService.isSuperUser();
			
			// check for the site.roleswap permission
			if (allowroleswap || roleswitchvalue != null)
			{
				Site activeSite = null;
	            try
	            {
	            	activeSite = portal.getSiteHelper().getSiteVisit(siteId); // active site
	            }
            	catch(IdUnusedException ie)
	            {
            		log.error(ie.getMessage(), ie);
            		throw new IllegalStateException("Site doesn't exist!");
	            }
	            catch(PermissionException pe)
	            {
	            	log.error(pe.getMessage(), pe);
	            	throw new IllegalStateException("No permission to view site!");
	            }
	            // this block of code will check to see if the student role exists in the site.  It will be used to determine if we need to display any student view component
	            boolean roleInSite = false;
            	Set<Role> roles = activeSite.getRoles();

            	String externalRoles = ServerConfigurationService.getString("studentview.roles"); // get the roles that can be swapped to from sakai.properties
            	String[] svRoles = externalRoles.split(",");
            	List<String> svRolesFinal = new ArrayList<String>();

            	for (Role role : roles)
            	{
            		for (int i = 0; i < svRoles.length; i++)
            		{
            			if (svRoles[i].trim().equals(role.getId()))
            			{
            				roleInSite = true;
            				svRolesFinal.add(role.getId());
            			}
            		}
            	}
            	if (activeSite.getType() != null && roleInSite) // the type check filters out some of non-standard sites where swapping roles would not apply.  The boolean check makes sure a role is in the site
            	{
		            String switchRoleUrl = "";
		            Role userRole = activeSite.getUserRole(session.getUserId()); // the user's role in the site
		            //if the userRole is null, this means they are more than likely a Delegated Access user.  Since the security check has already allowed
		            //the user to "swaproles" @allowroleswap, we know they have access to this site
		            if (roleswitchvalue != null && (userRole == null || !userRole.getId().equals(roleswitchvalue)))
		            {
		            	switchRoleUrl = ServerConfigurationService.getPortalUrl()
						+ "/role-switch-out/"
						+ siteId
						+ "/?panel=Main";
		            	rcontext.put("roleUrlValue", roleswitchvalue);
		            	roleswitchstate = true; // We're in a switched state, so set to true
		            }
		            else
		            {
		            	if (svRolesFinal.size()>1)
		            	{
		            		rcontext.put("roleswapdropdown", true);
							switchRoleUrl = ServerConfigurationService.getPortalUrl()
							+ "/role-switch/"
							+ siteId
							+ "/";
							rcontext.put("panelString", "/?panel=Main");
		            	}
		            	else
		            	{
		            		rcontext.put("roleswapdropdown", false);
		            		switchRoleUrl = ServerConfigurationService.getPortalUrl()
							+ "/role-switch/"
							+ siteId
							+ "/"
							+ svRolesFinal.get(0)
							+ "/?panel=Main";
		            		rcontext.put("roleUrlValue", svRolesFinal.get(0));
		            	}
		            }
		            roleswapcheck = true; // We made it this far, so set to true to display a component
		            rcontext.put("siteRoles", svRolesFinal);
					rcontext.put("switchRoleUrl", switchRoleUrl);
            	}
			}

			rcontext.put("viewAsStudentLink", Boolean.valueOf(roleswapcheck)); // this will tell our UI if we want the link for swapping roles to display
			rcontext.put("roleSwitchState", roleswitchstate); // this will tell our UI if we are in a role swapped state or not

			int tabsToDisplay = configuredTabsToDisplay;
			int tabDisplayLabel = 1;

			if (!loggedIn)
			{
				tabsToDisplay = ServerConfigurationService.getInt(
						"gatewaySiteListDisplayCount", tabsToDisplay);
			}
			else
			{
				Preferences prefs = PreferencesService
						.getPreferences(session.getUserId());
				ResourceProperties props = prefs.getProperties("sakai:portal:sitenav");
				try
				{
					tabsToDisplay = (int) props.getLongProperty("tabs");					 
				}
				catch (Exception any)
				{
				}
				try
				{
					tabDisplayLabel = (int) props.getLongProperty("tab:label");
				}
				catch (Exception any)
				{
				}
			}

			rcontext.put("tabDisplayLabel", tabDisplayLabel);
			rcontext.put("useDHTMLMore", useDHTMLMore);
			if (useDHTMLMore)
			{
				SiteView siteView = portal.getSiteHelper().getSitesView(
						SiteView.View.DHTML_MORE_VIEW, req, session, siteId);
				siteView.setPrefix(prefix);
				siteView.setToolContextPath(null);
				rcontext.put("tabsSites", siteView.getRenderContextObject());
			}
			else
			{
				SiteView siteView = portal.getSiteHelper().getSitesView(
						SiteView.View.DEFAULT_SITE_VIEW, req, session, siteId);
				siteView.setPrefix(prefix);
				siteView.setToolContextPath(null);
				rcontext.put("tabsSites", siteView.getRenderContextObject());
			}

			String cssClass = (siteType != null) ? "siteNavWrap " + siteType
					: "siteNavWrap";

			rcontext.put("tabsCssClass", cssClass);

			rcontext.put("tabsAddLogout", Boolean.valueOf(addLogout));
			if (addLogout)
			{
				String logoutUrl = Web.serverUrl(req)
						+ ServerConfigurationService.getString("portalPath")
						+ "/logout_gallery";
				rcontext.put("tabsLogoutUrl", logoutUrl);
				// rcontext.put("tabsSitLog",
				// Web.escapeHtml(rb.getString("sit_log")));
			}

			rcontext.put("tabsCssClass", cssClass);

			rcontext.put("tabsAddLogout", Boolean.valueOf(addLogout));
			if (addLogout)
			{
				String logoutUrl = Web.serverUrl(req)
						+ ServerConfigurationService.getString("portalPath")
						+ "/logout_gallery";
				rcontext.put("tabsLogoutUrl", logoutUrl);
				// rcontext.put("tabsSitLog",
				// Web.escapeHtml(rb.getString("sit_log")));
			}

			boolean allowAddSite = false;
			if(SiteService.allowAddCourseSite()) {
				allowAddSite = true;
			} else if (SiteService.allowAddPortfolioSite()) {
				allowAddSite = true;
			} else if (SiteService.allowAddProjectSite()) {
				allowAddSite = true;
			}

			rcontext.put("allowAddSite",allowAddSite);
		}
	}

}
