package org.sakaiproject.login.tool;

import java.io.BufferedReader;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;


public class GooglePlusLogin extends HttpServlet
{
	private static final long serialVersionUID = -3589514330633190919L;

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(GooglePlusLogin.class);
	
	private String defaultReturnUrl;

	private MessageDigest md;
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
		return "Sakai GooglePlus Login";
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
		googlePlusClientId = ServerConfigurationService.getString("google.plus.client.id", null);
		googlePlusClientSecret = ServerConfigurationService.getString("google.plus.client.secret", null);
		
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) { 
			M_log.warn("No SHA-256 support: " + e.getMessage());
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
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// get the session
		Session session = SessionManager.getCurrentSession();

		try
		{
			String state = req.getParameter("state");
			String code = req.getParameter("code");
			
	        byte[] digest = md.digest(session.getId().getBytes("UTF-8"));
	        String stateFromSession = byteArray2Hex(digest);
			M_log.debug("GooglePlus code for : " + session.getUserEid() + ":" + code);
			
			// Google CSRF Check
			if (!stateFromSession.equals(state)) {
				M_log.warn("GooglePlus CSRF check failed for " + session.getUserEid() + ":" + state + " : " + stateFromSession);
				res.setStatus(400);
				return;
			}
			
			String redirectUrl = ServerConfigurationService.getPortalUrl().replace("/portal", "/sakai-login-tool/googleplus/login");
			GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,
			    googlePlusClientId, googlePlusClientSecret, code, redirectUrl).execute();
			 
            // Create a credential representation of the token data.
            GoogleCredential credential = new GoogleCredential.Builder()
                .setJsonFactory(JSON_FACTORY)
                .setTransport(TRANSPORT)
                .setClientSecrets(googlePlusClientId, googlePlusClientSecret).build()
                .setFromTokenResponse(tokenResponse);
            
            // You can read the Google user ID in the ID token.
            GoogleIdToken idToken = tokenResponse.parseIdToken();
            String email = idToken.getPayload().getEmail();
            M_log.debug("GooglePlus email: " + email + ";token=" + tokenResponse.toString());
            
            User u = null;
            try {
            	u = UserDirectoryService.getUserByEid(email);
            }
            catch (UserNotDefinedException ue) {
				if (ServerConfigurationService.getBoolean("login.googleplus.user.create", false)) {
						Plus service = new Plus.Builder(TRANSPORT, JSON_FACTORY, credential).setApplicationName("CST Sakai").build();
						// Get a list of people that this user has shared with this app.
						//PeopleFeed people = service.people().list("me", "visible").execute();
						Person googlePerson = service.people().get("me").execute();
						String displayName = googlePerson.getDisplayName();
						String[] names = displayName.split(" ");
						String firstName = names[0];
						String lastName  = names[names.length-1];
						if (names.length > 3) {
								firstName += " " + names[1] + " " + names[2] ;
						}
						else if (names.length > 2) {
								firstName += " " + names[1];
						}

						// Set password to something unguessable - they can set a new PW once they are logged in
						byte[] pwDigest = md.digest(idToken.toString().getBytes("UTF-8"));
						String hiddenPW = byteArray2Hex(pwDigest);
						String userType = ServerConfigurationService.getString("login.googleplus.user.type", "google");
						String googleId = idToken.getPayload().getUserId();
						UserDirectoryService.addUser(googleId, email, firstName, lastName, email, hiddenPW, userType, null);
						u = UserDirectoryService.getUserByEid(email);
						M_log.info("GooglePlus created new user: " + email + ":" + u.getEid() + ":" + u.getId());
				}
				else {
            		M_log.info("GooglePlus user does not exist in Sakai: " + email);
				}
            }
            
			// login the user
			if (u != null && UsageSessionService.login(u.getId(), u.getEid(), req.getRemoteAddr(), req.getHeader("user-agent"), UsageSessionService.EVENT_LOGIN_CONTAINER))
			{
				// get the return URL
				String url = getUrl(session, Tool.HELPER_DONE_URL);

				// cleanup session
				session.removeAttribute(Tool.HELPER_MESSAGE);
				session.removeAttribute(Tool.HELPER_DONE_URL);
				
				// We will need this when we logout of Google+
	            session.setAttribute("googlePlusToken", tokenResponse.toString());

				// redirect to the done URL
				res.sendRedirect(res.encodeRedirectURL(url));
				return;
			}
		}
		catch (Exception ex)
		{
			M_log.warn("Authentication Failed: " + ex.getMessage(), ex);
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
	
    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
