package org.sakaiproject.login.tool;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.SessionManager;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;

/**
 * This servlet is useful when you want to have another HTTP request made for logout as your container based
 * authentication needs to do some cleanup and you can't do that on a normal logout. We redirect to this servlet
 * so that we can be sure that we get the additional HTTP request on a known URL.
 */
public class GooglePlusLogout extends HttpServlet {

	/** Our log (commons). */
	private static final Log M_log = LogFactory.getLog(GooglePlusLogin.class);

	private ServerConfigurationService serverConfigurationService;
	private UsageSessionService usageSessionService;
	private SessionManager sessionManager;
	
	private String googlePlusClientId;
	private String googlePlusClientSecret;
	
    /*
     * Default HTTP transport to use to make HTTP requests.
     */
    private static final HttpTransport TRANSPORT = new NetHttpTransport();

    /*
     * Default JSON factory to use to deserialize JSON.
     */
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    /*
     * Gson object to serialize JSON responses to requests to this servlet.
     */
    private static final Gson GSON = new Gson();

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai GooglePlus Logout";
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

		M_log.debug("init()");
		serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
		usageSessionService = (UsageSessionService) ComponentManager.get(UsageSessionService.class);
		sessionManager = (SessionManager) ComponentManager.get(SessionManager.class);
		
		googlePlusClientId = serverConfigurationService.getString("google.plus.client.id", null);
		googlePlusClientSecret = serverConfigurationService.getString("google.plus.client.secret", null);
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		M_log.debug("destroy()");

		super.destroy();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// get the session
		Session session = sessionManager.getCurrentSession();
		
		// Fetch the token from the seession
		String tokenData = (String) session.getAttribute("googlePlusToken");
		
		if (tokenData != null && tokenData != "") {
			M_log.debug("GooglePlus token for : " + session.getUserEid() + " : " + tokenData);

			// Build the credential from stored token data.
			GoogleCredential credential = new GoogleCredential.Builder()
			    .setJsonFactory(JSON_FACTORY)
			    .setTransport(TRANSPORT)
			    .setClientSecrets(googlePlusClientId, googlePlusClientSecret).build()
			    .setFromTokenResponse(JSON_FACTORY.fromString(
			        tokenData, GoogleTokenResponse.class));
	
			// Execute HTTP GET request to revoke current token.
			HttpResponse revokeResponse = TRANSPORT.createRequestFactory()
			    .buildGetRequest(new GenericUrl(
			        String.format(
			            "https://accounts.google.com/o/oauth2/revoke?token=%s",
			            credential.getAccessToken()))).execute();
		}
		else {
			M_log.debug("No GooglePlus token for : " + session.getUserEid());
		}
		
		String returnUrl = serverConfigurationService.getString("login.googleplus.logout.url", null);
		
		// if we end up with nowhere to go, go to the portal
		if (returnUrl == null)
		{
			// M_log.warn("login.googleplus.logout.url isn't set, to use container logout it should be.");
			returnUrl = (String)session.getAttribute(Tool.HELPER_DONE_URL);
			if (returnUrl == null || "".equals(returnUrl))
			{
				M_log.debug("complete: nowhere set to go, going to portal");
				returnUrl = serverConfigurationService.getPortalUrl();
			}
		}

		usageSessionService.logout();

		// redirect to the done URL
		res.sendRedirect(res.encodeRedirectURL(returnUrl));

	}
}
