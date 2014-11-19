package org.sakaiproject.login.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

import com.lastpass.saml.*;


public class SAMLLogin extends HttpServlet
{
	private static final long serialVersionUID = -3589514330633190919L;

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SAMLLogin.class);
	
	private String defaultReturnUrl;
	private SAMLClient client;

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai SAML Login";
	}

	/**
	 * Initialize the servlet.
	 * 
	 * @param config
	 *        The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		M_log.info("init()");
		defaultReturnUrl = ServerConfigurationService.getString("portalPath", "/portal");
		
		try {
			SAMLInit.initialize();
			IdPConfig idpConfig = new IdPConfig(new File("idp-metadata.xml"));
			SPConfig spConfig = new SPConfig(new File("sp-metadata.xml"));
			client = new SAMLClient(spConfig, idpConfig);
		} catch (SAMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		M_log.info("destroy()");

		super.destroy();
	}

	/**
	 * Respond to requests.
	 * 
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// get the session
		Session session = SessionManager.getCurrentSession();

		String authresponse = req.getParameter("SAMLResponse");
		String username = "test";
		AttributeSet aset;
			try {
			    aset = client.validateResponse(authresponse);
			    username = aset.getNameId();
			    
			    
			    M_log.info("SAML username=" + username);
			    M_log.info(java.util.Arrays.toString(aset.getAttributes().get("Name").toArray()));
			    M_log.info(java.util.Arrays.toString(aset.getAttributes().entrySet().toArray()));
			    // do something with now-authenticated user...
			} catch (SAMLException e) {
			    // response invalid, return to login page...
				e.printStackTrace();
			}

            User u = null;
            try {
            	u = UserDirectoryService.getUserByEid(username);
            }
            catch (UserNotDefinedException ue) {
						//UserDirectoryService.addUser(googleId, email, firstName, lastName, email, hiddenPW, userType, null);
						//u = UserDirectoryService.getUserByEid(username);
            	ue.printStackTrace();
			}

            
			// login the user
			if (u != null && UsageSessionService.login(u.getId(), u.getEid(), req.getRemoteAddr(), req.getHeader("user-agent"), UsageSessionService.EVENT_LOGIN_CONTAINER))
			{
				// get the return URL
				String url = getUrl(session, Tool.HELPER_DONE_URL);

				// cleanup session
				session.removeAttribute(Tool.HELPER_MESSAGE);
				session.removeAttribute(Tool.HELPER_DONE_URL);
				
				// redirect to the done URL
				res.sendRedirect(res.encodeRedirectURL(url));
				return;
			}
		
		session.setAttribute(SkinnableLogin.ATTR_CONTAINER_CHECKED, SkinnableLogin.ATTR_CONTAINER_CHECKED);
		res.sendRedirect(res.encodeRedirectURL(getUrl(session, SkinnableLogin.ATTR_RETURN_URL)));
	}

	/**
	 * Gets a URL from the session, if not found returns the portal URL.
	 * @param session The users HTTP session.
	 * @param sessionAttribute The attribute the URL is stored under.
	 * @return The URL.
	 */
	private String getUrl(Session session, String sessionAttribute) {
		String url = (String) session.getAttribute(sessionAttribute);
		if (url == null || url.length() == 0)
		{
			M_log.debug("No "+ sessionAttribute + " URL, redirecting to portal URL.");
			url = defaultReturnUrl;
		}
		return url;
	}
}
