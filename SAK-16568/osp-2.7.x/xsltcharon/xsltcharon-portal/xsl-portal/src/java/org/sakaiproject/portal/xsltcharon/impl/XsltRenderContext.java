/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
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

package org.sakaiproject.portal.xsltcharon.impl;

import org.sakaiproject.portal.api.PortalRenderContext;
import org.sakaiproject.portal.api.PortalRenderEngine;
import org.sakaiproject.portal.render.api.RenderResult;
import org.sakaiproject.portal.render.api.ToolRenderException;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.Web;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.site.api.SitePage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 13, 2007
 * Time: 12:07:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class XsltRenderContext implements PortalRenderContext {

   private static final Log log = LogFactory.getLog(XsltRenderEngine.class);
   private static final String ALT_TEMPLATE = "xsltUseTemplate";

   /** messages. */
   private static ResourceLoader rb = new ResourceLoader("org/sakaiproject/portal/xsltcharon/messages");

   private PortalRenderEngine renderEngine;
   private PortalRenderContext baseContext;
   private Map context = new HashMap();
   private UserDirectoryService userDirectoryService;
   private HttpServletRequest request;

   public XsltRenderContext(PortalRenderEngine renderEngine, PortalRenderContext baseContext, HttpServletRequest request) {
      this.renderEngine = renderEngine;
      this.baseContext = baseContext;
      this.request = request;
      this.userDirectoryService = org.sakaiproject.user.cover.UserDirectoryService.getInstance();
   }

   public void put(String string, Object value) {
      context.put(string, value);
      baseContext.put(string, value);
   }
   
   public Object get(String key) {
      return context.get(key);
   }

   public String dump() {
      return baseContext.dump();
   }

   public boolean uses(String includeOption) {
      return baseContext.uses(includeOption);
   }

   /**
    * Get the render engine associated with this context.
    *
    * @return the render engine
    */
   public PortalRenderEngine getRenderEngine() {
      return renderEngine;
   }

   public PortalRenderContext getBaseContext() {
      return baseContext;
   }

   public void setBaseContext(PortalRenderContext baseContext) {
      this.baseContext = baseContext;
   }

   public Document produceDocument() {
      Document doc = Xml.createDocument();

      Element root = doc.createElement("portal");
      doc.appendChild(root);

      User currentUser = userDirectoryService.getCurrentUser();
      boolean loggedIn = false;
      if (currentUser != null && currentUser.getId().length() > 0) {
         root.appendChild(createUserXml(doc, currentUser));
         loggedIn = true;
      }

      Map sitePages = (Map) context.get("sitePages");

      root.appendChild(createLoginXml(doc, request));
      appendTextElementNodeFromProp(doc, "pageTitle", "pageTitle", root);
      root.appendChild(createSkin(doc));
      root.appendChild(createConfixXml(doc, loggedIn, sitePages));

      if (loggedIn) {
         root.appendChild(createSites(doc));
      }

      try {
         root.appendChild(createPageCategories(doc, (List) sitePages.get("pageNavTools")));
      } catch (ToolRenderException e) {
         log.error("", e);
      }

      root.appendChild(createExternalizedXml(doc));

      return doc;
   }

   protected Element createExternalizedXml(Document doc) {
      Element externalized = doc.createElement("externalized");

      for (Iterator i=rb.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         externalized.appendChild(createExternalizedEntryXml(doc, entry.getKey(), entry.getValue()));
      }

      ResourceLoader rbsitenav = (ResourceLoader) context.get("rloader");

      for (Iterator i=rbsitenav.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         externalized.appendChild(createExternalizedEntryXml(doc, entry.getKey(), entry.getValue()));
      }

      return externalized;
   }

   protected Element createExternalizedEntryXml(Document doc, Object key, Object value) {
      Element entry = doc.createElement("entry");
      entry.setAttribute("key", (String) key);

      appendTextElementNode(doc, "value", (String) value, entry);

      return entry;
   }

   protected Element createSites(Document doc) {
      Element sites = doc.createElement("sites");

      Map tabSites = (Map) context.get("tabsSites");
      sites.appendChild(createSitesList(doc, tabSites, "tabsSites", "tabsSites"));
      sites.appendChild(createSitesList(doc, tabSites, "tabsMoreSites", "tabsMoreSites"));

      if (tabSites.get("tabsMoreSortedTermList") != null) {
         sites.appendChild(createTermSites(doc, tabSites));
      }

      return sites;
   }

   protected Element createTermSites(Document doc, Map tabSites) {
      Element siteTypes = doc.createElement("siteTypes");
      List<String> terms = (List<String>) tabSites.get("tabsMoreSortedTermList");
      Map termsMap = (Map) tabSites.get("tabsMoreTerms");

      int index = 0;
      for (Iterator<String> i=terms.iterator();i.hasNext();) {
         Element siteType = doc.createElement("siteType");
         siteType.setAttribute("order", "" + index);
         String title = i.next();
         appendTextElementNode(doc, "title", title, siteType);
         siteType.appendChild(createSitesList(doc, termsMap, title, "sites"));
         siteTypes.appendChild(siteType);
         index++;
      }

      return siteTypes;
   }

   protected Element createSitesList(Document doc, Map currentContext, String prop, String elementName) {
      Element list = doc.createElement(elementName);
      List<Map> sites = (List<Map>) currentContext.get(prop);

      if (sites == null) {
         return list;
      }

      int index = 0;
      for (Iterator<Map> i=sites.iterator();i.hasNext();) {
         list.appendChild(createSite(doc, i.next(), index));
         index++;
      }

      return list;
   }

   protected Element createSite(Document doc, Map siteMap, int index) {
      Element site = doc.createElement("site");

      appendTextElementNode(doc, "url", (String) siteMap.get("siteUrl"), site);
      appendTextElementNode(doc, "title", (String) siteMap.get("siteTitle"), site);
      appendTextElementNode(doc, "description", (String) siteMap.get("siteDescription"), site);
      appendTextElementNode(doc, "shortDescription", (String) siteMap.get("shortDescription"), site);
      appendTextElementNode(doc, "parent", (String) siteMap.get("parentSite"), site);
      appendTextElementNode(doc, "type", (String) siteMap.get("siteType"), site);
      appendTextElementNode(doc, "siteId", (String) siteMap.get("siteId"), site);

      site.setAttribute("selected", siteMap.get("isCurrentSite").toString());
      if (((Boolean)siteMap.get("isCurrentSite")).booleanValue()){
        if (context.get("viewAsStudentLink") != null &&
                ((Boolean)context.get("viewAsStudentLink")).booleanValue() ) {
            Element viewAsStudentLink = doc.createElement("viewAsStudentLink");
            site.appendChild(viewAsStudentLink);
            if (context.get("roleSwitchState") != null) {
                appendTextElementNode(doc, "roleSwitchState", context.get("roleSwitchState").toString(), viewAsStudentLink);
            }
            appendTextElementNode(doc, "roleUrlValue", (String) context.get("roleUrlValue"), viewAsStudentLink);
            if (context.get("roleswapdropdown") != null) {
                appendTextElementNode(doc, "roleswapdropdown", context.get("roleswapdropdown").toString(), viewAsStudentLink);
            }
            appendTextElementNode(doc, "switchRoleUrl", (String) context.get("switchRoleUrl"), viewAsStudentLink);
            appendTextElementNode(doc, "panelString", (String) context.get("panelString"), viewAsStudentLink);
            if (context.get("siteRoles") != null) {
                List roles = (List)context.get("siteRoles");
                appendTextElementNodes(doc, (String[])roles.toArray(new String[roles.size()]), viewAsStudentLink, "siteRoles", "role");
            }
        }
      }
      site.setAttribute("myWorkspace", siteMap.get("isMyWorkspace").toString());
      site.setAttribute("depth", siteMap.get("depth").toString());
      site.setAttribute("order", "" + index);
      
      if ( siteMap.get("isChild") != null )
         site.setAttribute("child", siteMap.get("isChild").toString());
      else if ( siteMap.get("parentSite") != null )
         site.setAttribute("child", Boolean.TRUE.toString());
      else
         site.setAttribute("child", Boolean.FALSE.toString());

      return site;
   }

   protected Element createPageCategories(Document doc, List pageTools) throws ToolRenderException {
      Element categories = doc.createElement("categories");

      int index = 0;
      String lastCategory = null;
      Element lastCategoryElement = null;
      for (Iterator<Map> i = pageTools.iterator();i.hasNext();) {
         Map page = i.next();
         Map pageProps = (Map)page.get("pageProps");
         String currentCategory = (String) pageProps.get("sitePage.pageCategory");  //todo put the static final here
         if (currentCategory == null) {
            lastCategory = null;
            lastCategoryElement = null;
            categories.appendChild(createUncategorizedPage(doc, page, index));
            index++;
         }
         else if (currentCategory.equals(lastCategory)) {
            lastCategoryElement.appendChild(createPageXml(doc, page));
            if ("true".equalsIgnoreCase(page.get("current").toString())) {
               ((Element)lastCategoryElement.getParentNode()).setAttribute("selected", "true");
            }
         }
         else {
            lastCategory = currentCategory;
            lastCategoryElement = createCategory(doc, categories, page, currentCategory, index);
            lastCategoryElement.appendChild(createPageXml(doc, page));
            categories.appendChild(lastCategoryElement.getParentNode());
            index++;
         }
      }

      return categories;
   }

   protected Element createCategory(Document doc, Element categories, Map page, String name, int index) {
      Element categoryElement = doc.createElement("category");
      categoryElement.setAttribute("selected", page.get("current").toString());
      categoryElement.setAttribute("order", Integer.toString(index));
      Element categoryKeyElement = doc.createElement("key");
      safeAppendTextNode(doc, categoryKeyElement, name, true);
      Element categoryEscapedKeyElement = doc.createElement("escapedKey");
      safeAppendTextNode(doc, categoryEscapedKeyElement, Web.escapeJavascript(name), false);

      categoryElement.appendChild(categoryKeyElement);
      categoryElement.appendChild(categoryEscapedKeyElement);

      Element pagesElement = doc.createElement("pages");

      categoryElement.appendChild(pagesElement);
      return pagesElement;
   }

   protected Element createUncategorizedPage(Document doc, Map page, int index) throws ToolRenderException {
      Element categoryElement = doc.createElement("category");
      categoryElement.setAttribute("selected", page.get("current").toString());
      categoryElement.setAttribute("order", Integer.toString(index));
      Element categoryKeyElement = doc.createElement("key");
      safeAppendTextNode(doc, categoryKeyElement,
         "org.theospi.portfolio.portal.model.ToolCategory.uncategorized", false);
      Element categoryEscapedKeyElement = doc.createElement("escapedKey");
      safeAppendTextNode(doc, categoryEscapedKeyElement,
         Web.escapeJavascript("org.theospi.portfolio.portal.model.ToolCategory.uncategorized"), false);

      categoryElement.appendChild(categoryKeyElement);
      categoryElement.appendChild(categoryEscapedKeyElement);

      Element pagesElement = doc.createElement("pages");

      pagesElement.appendChild(createPageXml(doc, page));

      categoryElement.appendChild(pagesElement);
      return categoryElement;
   }

   protected Element createPageXml(Document doc, Map page) throws ToolRenderException {
      Element pageElement = doc.createElement("page");
      pageElement.setAttribute("order", new Integer(0).toString());
      Boolean selected = (Boolean) page.get("current");
      pageElement.setAttribute("selected", selected.toString());

      pageElement.setAttribute("popUp", page.get("ispopup").toString());

      Element pageName = doc.createElement("title");
      safeAppendTextNode(doc, pageName, page.get("pageTitle").toString(), true);

      Element pageUrl = doc.createElement("url");
      safeAppendTextNode(doc, pageUrl, page.get("pageRefUrl").toString(), true);

      Element popPageUrl = doc.createElement("popUrl");
      safeAppendTextNode(doc, popPageUrl, page.get("pagePopupUrl").toString() + page.get("pageId").toString(), true);

      Element menuClass = doc.createElement("menuClass");
      safeAppendTextNode(doc, menuClass, page.get("menuClass").toString(), true);


      pageElement.appendChild(pageName);
      pageElement.appendChild(pageUrl);
      pageElement.appendChild(popPageUrl);
      pageElement.appendChild(menuClass);

      if (selected) {
         pageElement.setAttribute("layout",
            (context.get("pageColumnLayout").equals("col1"))?"0":"1");

         Element columns = doc.createElement("columns");

         Element column = doc.createElement("column");
         column.setAttribute("index", "0");
         column.appendChild(createColumnToolsXml(doc, (List)context.get("pageColumn0Tools"), page));
         columns.appendChild(column);

         if (context.get("pageColumnLayout").equals("col1of2")) {
            Element column2 = doc.createElement("column");
            column2.setAttribute("index", "1");
            column2.appendChild(createColumnToolsXml(doc, (List)context.get("pageColumn1Tools"), page));
            columns.appendChild(column2);
         }
         pageElement.appendChild(columns);
      }

      return pageElement;
   }

   protected Element createColumnToolsXml(Document doc, List tools, Map page) throws ToolRenderException {
      Element toolsElement = doc.createElement("tools");

      for (Iterator<Map> i=tools.iterator();i.hasNext();) {
         toolsElement.appendChild(createToolXml(doc, i.next(), page));
      }

      return toolsElement;
   }

   protected Element createToolXml(Document doc, Map tool, Map page) throws ToolRenderException {
      Element toolElement = doc.createElement("tool");

      Element title = doc.createElement("title");
      safeAppendTextNode(doc, title, tool.get("toolTitle").toString(), true);

      Element escapedId = doc.createElement("escapedId");
      safeAppendTextNode(doc, escapedId, tool.get("toolPlacementIDJS").toString(), true);

      toolElement.appendChild(title);
      toolElement.appendChild(escapedId);

      appendTextElementNode(doc, "toolReset", tool.get("toolResetActionUrl").toString(), toolElement);
      appendTextElementNode(doc, "toolHelp", tool.get("toolHelpActionUrl").toString(), toolElement);

      toolElement.setAttribute("hasReset", tool.get("toolShowResetButton").toString());
      toolElement.setAttribute("hasHelp", tool.get("toolShowHelpButton").toString());


      if ((Boolean)tool.get("hasRenderResult")) {
         toolElement.setAttribute("renderResult", "true");
         RenderResult result = (RenderResult) tool.get("toolRenderResult");

         //SAK-18793 - readDocumentFromString returns null on error; this check prevents NPEs
         String contentStr = result.getContent();
         if (contentStr == null)
         {
             throw new ToolRenderException ("tool xml failed to render and is null");
         }

         Document content = Xml.readDocumentFromString(contentStr);
         Element contentRoot = (Element) doc.importNode(content.getDocumentElement(), true);
         Element contentElement = doc.createElement("content");
         contentElement.appendChild(contentRoot);
         toolElement.appendChild(contentElement);
      }
      else {
         //portal/tool/ad222467-e186-4cca-80e9-d12a9d6db392?panel=Main
         Element toolUrl = doc.createElement("url");
         safeAppendTextNode(doc, toolUrl, tool.get("toolUrl") + "?panel=Main", true);

         toolElement.appendChild(toolUrl);
      }

      return toolElement;
   }

   protected Map createSiteTypesMap() {
      Map siteTypes = new HashMap();



      return siteTypes;
   }

   protected Element createConfixXml(Document doc, boolean loggedIn, Map sitePages) {
      Element config = doc.createElement("config");

      String skinRepo = (String) context.get("logoSkinRepo") + "/" + context.get("logoSkin");
      String helpUrl = null;

      if (sitePages != null) {
         String presenceUrl = (String) sitePages.get("pageNavPresenceUrl");
         boolean showPresence = ((Boolean)sitePages.get("pageNavShowPresenceLoggedIn")).booleanValue();
         Element presence = doc.createElement("presence");
         safeAppendTextNode(doc, presence, presenceUrl, true);
         presence.setAttribute("include", new Boolean(showPresence && loggedIn).toString());
         config.appendChild(presence);
         helpUrl = (String) sitePages.get("pageNavHelpUrl");
         config.setAttribute("pageNavPublished", sitePages.get("pageNavPublished").toString());
      }

      Element logo = doc.createElement("logo");
      safeAppendTextNode(doc, logo, skinRepo + "/images/logo_inst.gif", true);
      Element banner = doc.createElement("banner");
      safeAppendTextNode(doc, banner, skinRepo + "/images/banner_inst.gif", true);
      Element logout = doc.createElement("logout");
      safeAppendTextNode(doc, logout, (String) context.get("loginLogInOutUrl"), true);

      String copyright = (String) context.get("");
      String service = (String) context.get("bottomNavService");
      String serviceVersion = (String) context.get("bottomNavServiceVersion");
      String sakaiVersion = (String) context.get("bottomNavSakaiVersion");
      String server = (String) context.get("bottomNavServer");

      String[] bottomNav = getListAsStringArray("bottomNav");
      List poweredByList = (List) context.get("bottomNavPoweredBy");

      String[] poweredByUrl = new String[poweredByList.size()];
      String[] poweredByImage = new String[poweredByList.size()];
      String[] poweredByAltText = new String[poweredByList.size()];

      config.appendChild(logo);
      config.appendChild(banner);
      config.appendChild(logout);

      appendTextElementNode(doc, "copyright", copyright, config);
      appendTextElementNode(doc, "service", service, config);
      appendTextElementNode(doc, "serviceVersion", serviceVersion, config);
      appendTextElementNode(doc, "sakaiVersion", sakaiVersion, config);
      appendTextElementNode(doc, "server", server, config);
      appendTextElementNode(doc, "helpUrl", helpUrl, config);

      appendTextElementNodes(doc, bottomNav, config, "bottomNavs", "bottomNav");

      for (int i = 0; i < poweredByList.size(); i++) {
         Map poweredBy = (Map)poweredByList.get(i);
         poweredByAltText[i] = (String) poweredBy.get("poweredByAltText");
         poweredByImage[i]= (String) poweredBy.get("poweredByImage");
         poweredByUrl[i] = (String) poweredBy.get("poweredByUrl");
         config.appendChild(createPoweredByXml(doc, poweredByAltText[i], poweredByImage[i], poweredByUrl[i]));
      }

      appendTextElementNodes(doc, poweredByUrl, config, "poweredByUrls", "poweredByUrl");
      appendTextElementNodes(doc, poweredByImage, config, "poweredByImages", "poweredByImage");
      appendTextElementNodes(doc, poweredByAltText, config, "poweredByAltTexts", "poweredByAltText");

      addExtraConfig(config);

      return config;
   }

   protected void addExtraConfig(Element config) {
      Element extra = config.getOwnerDocument().createElement("extra");
      config.appendChild(extra);
      for (Iterator<Map.Entry> i=context.entrySet().iterator();i.hasNext();) {
         Map.Entry entry = i.next();
         if (entry.getValue() instanceof String) {
            appendTextElementNode(config.getOwnerDocument(),
               (String) entry.getKey(), (String) entry.getValue(), extra);
         }
         else if (entry.getValue() instanceof String[]) {
            appendTextElementNodes(config.getOwnerDocument(),
               (String[]) entry.getValue(), extra, entry.getKey().toString() + "-list",
               entry.getKey().toString());
         }
         else if (entry.getValue() instanceof List) {
            List value = (List) entry.getValue();
            if (value.size() > 0 && value.get(0) instanceof String) {
               String[] values = new String[value.size()];
               for (int j=0;j<values.length;j++) {
                  if (value.get(j) != null) {
                     values[j] = value.get(j).toString();
                  }
                  else {
                     values[j] = null;
                  }
               }
               appendTextElementNodes(config.getOwnerDocument(),
                  values, extra, entry.getKey().toString() + "-list",
                  entry.getKey().toString());
            }
         }
      }

   }

   protected Element createPoweredByXml(Document doc, String text, String image, String url) {
      Element poweredBy = doc.createElement("poweredBy");

      appendTextElementNode(doc, "text", text, poweredBy);
      appendTextElementNode(doc, "image", image, poweredBy);
      appendTextElementNode(doc, "url", url, poweredBy);

      return poweredBy;
   }

   protected String[] getListAsStringArray(String key) {
      List list = (List) context.get(key);
      return (String[]) list.toArray(new String[]{});
   }

   protected Element createSkin(Document doc) {
      Element skins = doc.createElement("skins");
      String skinUrl = context.get("pageSkinRepo")+"/"+context.get("pageSkin")+"/portal.css";
      Element skin = doc.createElement("skin");
      skins.appendChild(skin);
      safeAppendTextNode(doc, skin, skinUrl, true);
      skin.setAttribute("order", "0");
      return skins;
   }

   protected Element createLoginXml(Document doc, HttpServletRequest req) {
      Element login = doc.createElement("loginInfo");

      appendTextElementNodeFromProp(doc, "topLogin", "siteNavTopLogin", login);
      appendTextElementNodeFromProp(doc, "logInOutUrl", "loginLogInOutUrl", login);
      appendTextElementNodeFromProp(doc, "loginText", "loginMessage", login);
      appendTextElementNodeFromProp(doc, "logoutText", "loginMessage", login);
      appendTextElementNodeFromProp(doc, "image1", "loginImage1", login);

      return login;
   }

   protected void appendTextElementNodeFromProp(Document doc, String nodeName, String propName, Element parent) {
      Object nodeValue = context.get(propName);

      if (nodeValue == null) {
         appendTextElementNode(doc, nodeName, null, parent);
      }
      else {
         appendTextElementNode(doc, nodeName, nodeValue.toString(), parent);
      }
   }

   protected Element createUserXml(Document doc, User current) {
      Element user = doc.createElement("currentUser");

      appendTextElementNode(doc, "id", current.getId(), user);
      appendTextElementNode(doc, "first", current.getFirstName(), user);
      appendTextElementNode(doc, "last", current.getLastName(), user);
      appendTextElementNode(doc, "email", current.getEmail(), user);

      return user;
   }

   protected void appendTextElementNode(Document doc, String name, String text, Element parent) {
      Element element = doc.createElement(name);
      safeAppendTextNode(doc, element, text, true);
      parent.appendChild(element);
   }

   protected void safeAppendTextNode(Document doc, Element element, String text, boolean cdata) {
      if (text != null) {
         element.appendChild(cdata?doc.createCDATASection(text):doc.createTextNode(text));
      }
   }

   protected void dumpDocument(Node node) {
      try {
         Transformer transformer = TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
         transformer.transform( new DOMSource(node), new StreamResult(System.out) );
      }
      catch ( Exception e )
      {
         e.printStackTrace();
      }
   }

   protected void appendTextElementNodes(Document doc, String[] strings, Element parent,
                                         String topNodeName, String nodeName) {
      Element topNode = doc.createElement(topNodeName);

      if (strings == null) {
         return;
      }

      for (int i=0;i<strings.length;i++) {
         appendTextElementNode(doc, nodeName, strings[i], topNode);
      }

      parent.appendChild(topNode);
   }

   public String getAlternateTemplate() {
      if (request.getParameter(ALT_TEMPLATE) != null) {
         return request.getParameter(ALT_TEMPLATE);
      }

      if (request.getAttribute(ALT_TEMPLATE) != null) {
         return request.getAttribute(ALT_TEMPLATE).toString();
      }

      return null;
   }

}
