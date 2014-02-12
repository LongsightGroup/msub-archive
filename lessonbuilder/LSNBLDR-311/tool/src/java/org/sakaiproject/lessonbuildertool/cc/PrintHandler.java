package org.sakaiproject.lessonbuildertool.cc;

/***********
 * This code is based on a reference implementation done for the IMS Consortium.
 * The copyright notice for that implementation is included below. 
 * All modifications are covered by the following copyright notice.
 *
 * Copyright (c) 2011 Rutgers, the State University of New Jersey
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
 */

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/main/java/org/imsglobal/cc/PrintHandler.java $
 * $Id: PrintHandler.java 227 2011-01-08 18:26:55Z drchuck $
 **********************************************************************************
 *
 * Copyright (c) 2010 IMS Global Learning Consortium
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

import org.apache.commons.io.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.filter.ElementFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.CharArrayWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URLEncoder;

import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.Validator;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.content.cover.ContentTypeImageService;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import org.sakaiproject.lessonbuildertool.service.GroupPermissionsService;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.lessonbuildertool.cc.QtiImport;
import org.sakaiproject.lessonbuildertool.service.LessonEntity;
import org.sakaiproject.lessonbuildertool.service.QuizEntity;
import org.sakaiproject.lessonbuildertool.service.ForumInterface;
import org.sakaiproject.lessonbuildertool.service.BltiInterface;
import org.sakaiproject.lessonbuildertool.service.AssignmentInterface;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.sakaiproject.tool.assessment.services.qti.QTIService;
import org.sakaiproject.tool.assessment.qti.constants.QTIVersion;

/* PJN NOTE:
 * This class is an example of what an implementer might want to do as regards overloading DefaultHandler.
 * In this case, messages are written to the screen. If a method in default handler is not overridden, then it does
 * nothing.
 */

public class PrintHandler extends DefaultHandler implements AssessmentHandler, DiscussionHandler, AuthorizationHandler,
                                       MetadataHandler, LearningApplicationResourceHandler, QuestionBankHandler,
                                       WebContentHandler, WebLinkHandler{

  private static Log log = LogFactory.getLog(PrintHandler.class);

  private static final String HREF="href";
  private static final String TYPE="type";
  private static final String FILE="file";
  private static final String XML=".xml";
  private static final String URL="url";
  private static final String TITLE="title";
  private static final String TEXT="text";
  private static final String TEXTTYPE="texttype";
  private static final String TEXTHTML="text/html";
  private static final String DESCRIPTION="description";
  private static final String GENERAL="general";
  private static final String STRING="string";
  private static final String LANGSTRING="langstring";
  private static final String ATTACHMENT="attachment";
  private static final String ATTACHMENTS="attachments";
  private static final String INTENDEDUSE="intendeduse";
    
  private static final String CC_ITEM_TITLE="title";
  private static final String CC_WEBCONTENT="webcontent";
  private static final String CC_WEBLINK0="imswl_xmlv1p0";
  private static final String CC_WEBLINK1="imswl_xmlv1p1";
  private static final String CC_WEBLINK2="imswl_xmlv1p2";
  private static final String CC_TOPIC0="imsdt_xmlv1p0";
  private static final String CC_TOPIC1="imsdt_xmlv1p1";
  private static final String CC_TOPIC2="imsdt_xmlv1p2";
  private static final String CC_ASSESSMENT0="imsqti_xmlv1p2/imscc_xmlv1p0/assessment";
  private static final String CC_ASSESSMENT1="imsqti_xmlv1p2/imscc_xmlv1p1/assessment";
  private static final String CC_ASSESSMENT2="imsqti_xmlv1p2/imscc_xmlv1p2/assessment";
  private static final String CC_QUESTION_BANK0="imsqti_xmlv1p2/imscc_xmlv1p0/question-bank";
  private static final String CC_QUESTION_BANK1="imsqti_xmlv1p2/imscc_xmlv1p1/question-bank";
  private static final String CC_QUESTION_BANK2="imsqti_xmlv1p2/imscc_xmlv1p2/question-bank";
  private static final String CC_BLTI0="imsbasiclti_xmlv1p0";
  private static final String CC_BLTI1="imsbasiclti_xmlv1p1";
  private static final int MAX_ATTEMPTS = 100;

  private List<SimplePage> pages = new ArrayList<SimplePage>();
    // list parallel to pages containing sequence of last item on the page
  private List<Integer> sequences= new ArrayList<Integer>();
  CartridgeLoader utils = null;
  SimplePageToolDao simplePageToolDao = null;

  private String title = null;
  private String description = null;
  private String baseName = null;
  private String baseUrl = null;
  private String siteId = null;
  private LessonEntity quiztool = null;
  private LessonEntity topictool = null;
  private LessonEntity bltitool = null;
  private LessonEntity assigntool = null;
  private Set<String>roles = null;
  boolean usesRole = false;
  boolean usesPatternMatch = false;
  boolean usesCurriculum = false;
  boolean importtop = false;
  Integer assignmentNumber = 1;

    // this is the CC file name for all files added
  private Set<String> filesAdded = new HashSet<String>();
    // this is the CC file name (of the XML file) -> Sakaiid for non-file items
  private Map<String,String> itemsAdded = new HashMap<String,String>();

  public PrintHandler(SimplePageBean bean, CartridgeLoader utils, SimplePageToolDao dao, LessonEntity q, LessonEntity l, LessonEntity b, LessonEntity a, boolean itop) {
      super();
      this.utils = utils;
      this.simplePageBean = bean;
      this.simplePageToolDao = dao;
      this.siteId = bean.getCurrentSiteId();
      this.quiztool = q;
      this.topictool = l;
      this.bltitool = b;
      this.assigntool = a;
      this.importtop = itop;
  }

  public void setAssessmentDetails(String the_ident, String the_title) {
      if (log.isDebugEnabled())
	  log.debug("assessment ident: "+the_ident +" title: "+the_title);
  }

  public void endCCFolder() {
      if (log.isDebugEnabled())
	  log.debug("cc folder ends");
      int top = pages.size()-1;
      sequences.remove(top);
      pages.remove(top);
  }

  public void endCCItem() {
      if (log.isDebugEnabled())
	  log.debug("cc item ends");
  }

  public void startCCFolder(Element folder) {
      String title = this.title;
      if (folder != null)
	  title = folder.getChildText(TITLE, ns.getNs());

      // add top level pages to left margin
      SimplePage page = null;
      if (pages.size() == 0) {
	  page = simplePageBean.addPage(title, false);  // add new top level page
	  if (description != null && !description.trim().equals("")) {
	      SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), 1, SimplePageItem.TEXT, "", "");
	      item.setHtml(Validator.escapeHtml(description));
	      simplePageBean.saveItem(item);
	      sequences.add(2);
	  } else
	      sequences.add(1);
      } else {
	  page = simplePageToolDao.makePage("0", siteId, title, 0L, 0L);
	  simplePageBean.saveItem(page);
	  SimplePage parent = pages.get(pages.size()-1);
	  int seq = simplePageBean.getItemsOnPage(parent.getPageId()).size() + 1;

	  SimplePageItem item = simplePageToolDao.makeItem(parent.getPageId(), seq, SimplePageItem.PAGE, Long.toString(page.getPageId()), title);
	  simplePageBean.saveItem(item);
	  sequences.add(1);
      }
      pages.add(page);
  }

  public void startCCItem(String the_id, String the_title) {
      if (log.isDebugEnabled()) {
	  log.debug("cc item "+the_id+" begins");
	  log.debug("title: "+the_title);
      }
  }

  private ContentCollection makeBaseFolder(String name) {

      if (siteId == null) {
	  simplePageBean.setErrKey("simplepage.nosite", "");
	  return null;
      }

      if (importtop) {
	  try {
	      ContentCollection top = ContentHostingService.getCollection(ContentHostingService.getSiteCollection(siteId));
	      return top;
	  } catch (Exception e) {
	      simplePageBean.setErrKey("simplepage.create.resource.failed",name + " " +e);
	      return null;
	  }
      }

      if (name == null) 
	  name = "Common Cartridge";
      if (name.trim().length() == 0) 
	  name = "Common Cartridge";

      // we must reject certain characters that we cannot even escape and get into Tomcat via a URL                               

      StringBuffer newname = new StringBuffer(ContentHostingService.getSiteCollection(siteId));

      int length = name.length();
      for (int i = 0; i < length; i++) {
	  if (Validator.INVALID_CHARS_IN_RESOURCE_ID.indexOf(name.charAt(i)) != -1)
	      newname.append("_");
	  else
	      newname.append(name.charAt(i));
      }

      length = newname.length();
      if (length > (ContentHostingService.MAXIMUM_RESOURCE_ID_LENGTH - 5))
	  length = ContentHostingService.MAXIMUM_RESOURCE_ID_LENGTH - 5; // for trailing / and possible count
      newname.setLength(length);

      name = newname.toString() + "1";

      ContentCollectionEdit collection = null;
      int tries = 1;
      int olength = name.length();
      for (; tries <= MAX_ATTEMPTS; tries++) {
	  try {
	      collection = ContentHostingService.addCollection(name + "/");  // append / here because we may hack on the name

	      String display = name;
	      int main = name.lastIndexOf("/");
	      if (main >= 0)
		  display = display.substring(main+1);
	      collection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, display);

	      ContentHostingService.commitCollection(collection);
	      break;   // got it
	  } catch (IdUsedException e) {
	      name = name.substring(0, olength) + "-" + tries;
	  } catch (Exception e) {
	      simplePageBean.setErrKey("simplepage.create.resource.failed",name + " " +e);
	      return null;
	  }
      }
      if (collection == null) {
	  simplePageBean.setErrKey("simplepage.resource100", name);
	  return null;
      }
      return collection;
  }

  private String getFileName(Element resource) {
      Element file = resource.getChild(FILE, ns.getNs());
      if (file != null)
	  return file.getAttributeValue(HREF);
      else
	  return null;
  }

  public String getGroupForRole(String role) {
      // if group already exists, this will return the existing one
      try {
	  return GroupPermissionsService.makeGroup(siteId, role);
      } catch (Exception e) {
	  log.debug("Unable to create group " + role);
	  return null;
      }
  }

  public void setCCItemXml(Element the_xml, Element resource, AbstractParser parser, CartridgeLoader loader, boolean nopage) {
      if (log.isDebugEnabled()) {
    	  String pageTitle = "";
    	  if (pages.size() >= 1 )
    		  pageTitle = pages.get(pages.size()-1).getTitle();
		  log.debug("\nadd item to page " + pageTitle +
				 " xml: "+the_xml + 
				 " title " + (the_xml==null?"Question Pool" : the_xml.getChildText(CC_ITEM_TITLE, ns.getNs())) +
				 " type " + resource.getAttributeValue(TYPE) +
				 " href " + resource.getAttributeValue(HREF));
      }

      String type = resource.getAttributeValue(TYPE);
      boolean isBank = type.equals(CC_QUESTION_BANK0) || type.equals(CC_QUESTION_BANK1) || type.equals(CC_QUESTION_BANK2);

      boolean hide = false;
      List<String>roles = new ArrayList<String>();
      Iterator mdroles = resource.getDescendants(new ElementFilter("intendedEndUserRole", ns.getLom()));
      if (mdroles != null) {
	  while (mdroles.hasNext()) {
	      Element role = (Element)mdroles.next();
	      if (!"Learner".equals(role.getChildText("value",  ns.getLom()))) {
		  usesRole = true;
	      }
	      if ("Mentor".equals(role.getChildText("value",  ns.getLom()))) {
		  roles.add(getGroupForRole("Mentor"));
	      }
	      if ("Instructor".equals(role.getChildText("value",  ns.getLom()))) {
		  roles.add(getGroupForRole("Instructor"));
	      }
	  }
      }	  
      if (nopage)
	  hide = true;

      // for question banks we don't need a current page, as we don't put banks on a page
      if (pages.size() == 0 && !isBank && !nopage)
	  startCCFolder(null);

      int top = pages.size()-1;
      SimplePage page = (isBank || nopage) ? null : pages.get(top);

      Integer seq = (isBank || nopage) ? 0 : sequences.get(top);
      String title = null;
      if (the_xml == null)
	  title = "Question Pool";
      else
	  title = the_xml.getChildText(CC_ITEM_TITLE, ns.getNs());

      try {
	  if (type.equals(CC_WEBCONTENT) && !hide) {
	      // note: when this code is called the actual sakai resource hasn't been created yet
	      String sakaiId = baseName + resource.getAttributeValue(HREF);
	      String extension = Validator.getFileExtension(sakaiId);
	      String mime = ContentTypeImageService.getContentType(extension);
	      String intendedUse = resource.getAttributeValue(INTENDEDUSE);
	      String fileName = getFileName(resource);
	      //For HTML types, make a page instead of a Resource
	      SimplePageItem item;
	      if (mime != null && fileName != null && mime.startsWith("text/")) {
	    	  item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.TEXT, sakaiId, title);
	    	  StringWriter writer = new StringWriter();
	    	  IOUtils.copy(utils.getFile(fileName), writer);
	    	  String html = writer.toString();

  	 		  StringBuilder error = new StringBuilder();
	    	  //Read HTML
	    	  Integer filter = simplePageBean.getFilterLevel(null);
              if (filter.equals(simplePageBean.FILTER_HIGH))
            	  html = FormattedText.processFormattedText(html, error);
              else
            	  html = FormattedText.processHtmlDocument(html, error);
              item.setHtml(html);
	      }
	      else  {
	    	  item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.RESOURCE, sakaiId, title);
	    	  item.setHtml(mime);
	      }
	      
	      item.setSameWindow(true);
	      if (intendedUse != null) {
		  intendedUse = intendedUse.toLowerCase();
		  if (intendedUse.equals("lessonplan"))
		      item.setDescription(simplePageBean.getMessageLocator().getMessage("simplepage.import_cc_lessonplan"));
		  else if (intendedUse.equals("syllabus"))
		      item.setDescription(simplePageBean.getMessageLocator().getMessage("simplepage.import_cc_syllabus"));
		  else if (assigntool != null && intendedUse.equals("assignment")) {
		      if (itemsAdded.get(fileName) == null) {
			  // itemsAdded.put(fileName, SimplePageItem.DUMMY); // don't add the same test more than once
			  AssignmentInterface a = (AssignmentInterface) assigntool;
			  // file hasn't been written yet to contenthosting. A2 requires it to be there
			  addFile(resource.getAttributeValue(HREF));
			  String assignmentId = a.importObject(title, sakaiId, mime); // sakaiid for assignment
			  if (assignmentId!= null) {
			      item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.ASSIGNMENT, assignmentId, title);
			      sakaiId = assignmentId;
			  }
		      }
		  }
	      }
	      simplePageBean.saveItem(item);
	      if (roles.size() > 0) {  // has to be written already or we can't set groups
	    	  // file hasn't been written yet to contenthosting. setitemgroups requires it to be there
	    	  addFile(fileName);
	    	  simplePageBean.setItemGroups(item, roles.toArray(new String[0]));
	      }
	      sequences.set(top, seq+1);
	  } else if (type.equals(CC_WEBCONTENT)) { // i.e. hidden. if it's an assignment have to load it
	      String intendedUse = resource.getAttributeValue(INTENDEDUSE);
	      if (assigntool != null && intendedUse != null && intendedUse.equals("assignment")) {
		  String fileName = getFileName(resource);
		  if (itemsAdded.get(fileName) == null) {
		      itemsAdded.put(fileName, SimplePageItem.DUMMY); // don't add the same test more than once
		      String sakaiId = baseName + resource.getAttributeValue(HREF);
		      String extension = Validator.getFileExtension(sakaiId);
		      String mime = ContentTypeImageService.getContentType(extension);
		      AssignmentInterface a = (AssignmentInterface) assigntool;
		      // file hasn't been written yet to contenthosting. A2 requires it to be there
		      addFile(resource.getAttributeValue(HREF));
		      // in this case there's no item to take a title from
		      String atitle = simplePageBean.getMessageLocator().getMessage("simplepage.importcc-assigntitle").replace("{}", (assignmentNumber++).toString());
		      String assignmentId = a.importObject(atitle, sakaiId, mime); // sakaiid for assignment
		  }
	      }
	  } else if (type.equals(CC_WEBLINK0) || type.equals(CC_WEBLINK1) || type.equals(CC_WEBLINK2)){
	      String filename = getFileName(resource);
	      Element linkXml =  parser.getXML(loader, filename);
	      Namespace linkNs = ns.link_ns();
	      Element urlElement = linkXml.getChild(URL, linkNs);
	      String url = urlElement.getAttributeValue(HREF);

	      // the name must end in XML, so we can just turn it into URL
	      filename = filename.substring(0, filename.length()-3) + "url";
	      String sakaiId = baseName + filename;

	      if (! filesAdded.contains(filename)) {
		  // we store the URL as a text/url resource
		  ContentResourceEdit edit = ContentHostingService.addResource(sakaiId);
		  edit.setContentType("text/url");
		  edit.setResourceType("org.sakaiproject.content.types.urlResource");
		  edit.setContent(url.getBytes("UTF-8"));
		  edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, 
						       Validator.escapeResourceName(filename));
		  ContentHostingService.commitResource(edit, NotificationService.NOTI_NONE);
		  filesAdded.add(filename);
	      }

	      if (!hide) {
		  // now create the Sakai item
		  SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.RESOURCE, sakaiId, title);
		  item.setHtml(simplePageBean.getTypeOfUrl(url));  // checks the web site to see what it actually is
		  item.setSameWindow(true);
		  simplePageBean.saveItem(item);
		  if (roles.size() > 0)
		      simplePageBean.setItemGroups(item, roles.toArray(new String[0]));
		  sequences.set(top, seq+1);
	      }
	      
	  } else if (topictool != null && ( type.equals(CC_TOPIC0) || type.equals(CC_TOPIC1) || type.equals(CC_TOPIC2))){
	      String filename = getFileName(resource);
	      Element topicXml =  parser.getXML(loader, filename);
	      Namespace topicNs = ns.topic_ns();
	      String topicTitle = topicXml.getChildText(TITLE, topicNs);
	      if (topicTitle == null)
		  topicTitle = simplePageBean.getMessageLocator().getMessage("simplepage.cc-defaulttopic");
	      String text = topicXml.getChildText(TEXT, topicNs);
	      boolean texthtml = false;
	      if (text != null) {
		  Element textNode = topicXml.getChild(TEXT, topicNs);
		  String textformat = textNode.getAttributeValue(TEXTTYPE);
		  if (TEXTHTML.equalsIgnoreCase(textformat))
		      texthtml = true;
	      }

	      String base = baseUrl + filename;
	      int slash = base.lastIndexOf("/");
	      if (slash >= 0)
		  base = base.substring(0, slash+1); // include trailing slash

	      if (texthtml) {
		  text =  text.replaceAll("\\$IMS-CC-FILEBASE\\$", base);
	      }

	      // I'm going to assume that URLs in the CC files are legal, but if
	      // I add to them I nneed to URLencode what I add

	      Element attachmentlist = topicXml.getChild(ATTACHMENTS, topicNs);
	      List<Element>attachments = new ArrayList<Element>();
	      if (attachmentlist != null)
		  attachments = attachmentlist.getChildren();
	      List<String>attachmentHrefs = new ArrayList<String>();
	      for (Element a: attachments) 
		  attachmentHrefs.add(a.getAttributeValue(HREF));

	      ForumInterface f = (ForumInterface)topictool;

	      if (nopage)
		  title = simplePageBean.getMessageLocator().getMessage("simplepage.cc-defaultforum");

	      // log.warn("about to call forum import base " + base);
	      // title is for the cartridge. That will be used as the forum
	      // if already added, don't do it again
	      String sakaiId = itemsAdded.get(filename);
	      if (sakaiId == null) {
	          if ( f != null ) 
	              sakaiId = f.importObject(title, topicTitle, text, texthtml, base, siteId, attachmentHrefs, hide);
		  if (sakaiId != null)
		      itemsAdded.put(filename, sakaiId);
	      }

	      if (!hide) {
		  // log.warn("about to add formum item");
		  SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.FORUM, sakaiId, title);
		  simplePageBean.saveItem(item);
		  if (roles.size() > 0)
		      simplePageBean.setItemGroups(item, roles.toArray(new String[0]));
		  sequences.set(top, seq+1);
		  // log.warn("finished with forum item");
	      }

	  } else if (quiztool != null && (
		  type.equals(CC_ASSESSMENT0) || type.equals(CC_ASSESSMENT1) || type.equals (CC_ASSESSMENT2) ||
		  type.equals(CC_QUESTION_BANK0) || type.equals(CC_QUESTION_BANK1) || type.equals(CC_QUESTION_BANK2))) {

	      String fileName = getFileName(resource);
	      String sakaiId = null;
	      
	      if (itemsAdded.get(fileName) == null) {
		  itemsAdded.put(fileName, SimplePageItem.DUMMY); // don't add the same test more than once
		  
		  InputStream instream = utils.getFile(fileName);
		  //		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  //PrintWriter outwriter = new PrintWriter(baos);
		  File qtitemp = File.createTempFile("ccqti", "txt");
		  PrintWriter outwriter = new PrintWriter(qtitemp);
	      
		  // I'm going to assume that URLs in the CC files are legal, but if
		  // I add to them I nneed to URLencode what I add
		  String base = baseUrl + fileName;
		  int slash = base.lastIndexOf("/");
		  if (slash >= 0)
		      base = base.substring(0, slash+1); // include trailing slash

		  QtiImport imp = new QtiImport();
		  try {
		      boolean thisUsesPattern = imp.mainproc(instream, outwriter, isBank, base, siteId, simplePageBean);
		      if (thisUsesPattern)
			  usesPatternMatch = true;
		      if (imp.getUsesCurriculum())
			  usesCurriculum = true;
		  } catch (Exception e) {
		      e.printStackTrace();
		  }

		  outwriter.close();
		  InputStream inputStream = new FileInputStream(qtitemp);

		  try {
		      DocumentBuilderFactory builderFactory =
			  DocumentBuilderFactory.newInstance();
		      builderFactory.setNamespaceAware(true);
		      builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		      builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		      DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		      Document document = documentBuilder.parse(inputStream);

		      QuizEntity q = (QuizEntity)quiztool;

		      sakaiId = q.importObject(document, isBank, siteId, hide);
		      if (sakaiId == null)
			  sakaiId = SimplePageItem.DUMMY;

		  } catch (Exception e) {
		      log.warn("CC import error creating or parsing QTI file " + fileName + " " +  e);
		      simplePageBean.setErrKey("simplepage.create.object.failed", e.toString());
		  }

		  inputStream.close();
		  qtitemp.delete();

	      }

	      // question banks don't appear on the page
	      if (!isBank && !hide) {
		  SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.ASSESSMENT, (sakaiId == null ? SimplePageItem.DUMMY : sakaiId), title);
		  simplePageBean.saveItem(item);
		  if (roles.size() > 0)
		      simplePageBean.setItemGroups(item, roles.toArray(new String[0]));
		  sequences.set(top, seq+1);
	      }

	  } else if (type.equals(CC_QUESTION_BANK0) || type.equals(CC_QUESTION_BANK1))
	      ; // handled elsewhere
	  // current code seems to assume that BLTI tool is part of the page so skip if no page
	  else if (!nopage && (type.equals(CC_BLTI0) || type.equals(CC_BLTI1))) { 
	      String filename = getFileName(resource);
	      Element ltiXml =  parser.getXML(loader, filename);
	      XMLOutputter outputter = new XMLOutputter();
	      String strXml = outputter.outputString(ltiXml);       
	      Namespace bltiNs = ns.blti_ns();
	      String bltiTitle = ltiXml.getChildText(TITLE, bltiNs);

	      Element customElement = ltiXml.getChild("custom", bltiNs);
	      List<Element>customs = new ArrayList<Element>();
	      if (customElement != null)
		  customs = customElement.getChildren();
	      StringBuffer sb = new StringBuffer();
              String custom = null;
	      for (Element a: customs) {
		  String key = a.getAttributeValue("name");
		  String value = a.getText();
                  if ( key == null ) continue;
                  key = key.trim();
                  if ( value == null ) continue;
		  sb.append(key.trim());
                  sb.append("=");
                  sb.append(value.trim());
                  sb.append("\n");
	      }
              if ( sb.length() > 0 ) custom = sb.toString();

	      String launchUrl = ltiXml.getChildText("secure_launch_url", bltiNs);
	      if ( launchUrl == null ) launchUrl = ltiXml.getChildText("launch_url", bltiNs);

              	String sakaiId = null;
              	if ( bltitool != null ) {
	      		sakaiId = ((BltiInterface) bltitool).doImportTool(launchUrl, bltiTitle, strXml, custom);
                }

		if (!hide) {
		    if ( sakaiId != null) {
			// log.warn("Adding LTI content item "+sakaiId);
			SimplePageItem item = simplePageToolDao.makeItem(page.getPageId(), seq, SimplePageItem.BLTI, sakaiId, title);
			item.setHeight(""); // default depends upon format, so it's supplied at runtime
			simplePageBean.saveItem(item);
			if (roles.size() > 0)
			    simplePageBean.setItemGroups(item, roles.toArray(new String[0]));
			sequences.set(top, seq+1);
		    } else {
			log.warn("LTI Import Failed..");
		    }
		}
	  } else if (type.equals(CC_WEBCONTENT) && hide) {
	      // handled elsewhere
	  } else
	      log.debug("implemented type: " + resource.getAttributeValue(TYPE));
      } catch (Exception e) {
	  e.printStackTrace();
	  log.debug(">>>Exception " + e);
      }

  }

  public void addAttachment(String attachment_path) {
      if (log.isDebugEnabled())
	  log.debug("adding an attachment: "+attachment_path);
  }

  public void endDiscussion() {
      if (log.isDebugEnabled())
	  log.debug("end discussion");
  }

  public void startManifest() {
      if (log.isDebugEnabled())
	  log.debug("start manifest");
  }

  public void checkCurriculum(Element the_xml) {
      Element md = the_xml.getChild("curriculumStandardsMetadataSet",ns.csmd_ns());
      if (md != null) {
	  if (md.getChild("curriculumStandardsMetadata",ns.csmd_ns()) != null) {
	      usesCurriculum = true;
	  }
      }
  }

  public void setManifestXml(Element the_xml) {
      if (log.isDebugEnabled())

	  log.debug("manifest xml: "+the_xml);

  }

  public void endManifest() {
      if (log.isDebugEnabled())
	  log.debug("end manifest");
      if (usesRole)
	  simplePageBean.setErrKey("simplepage.cc-uses-role", null);
      // the pattern match is restricted enough that we can actually do it
      // if (usesPatternMatch)
      //  simplePageBean.setErrKey("simplepage.import_cc_usespattern", null);
      if (usesCurriculum)
	  simplePageBean.setErrKey("simplepage.cc-uses-curriculum", null);

  }

  public void startDiscussion(String topic_name, String text_type, String text, boolean isProtected) {
      if (log.isDebugEnabled()){
	  log.debug("start a discussion: "+topic_name);
	  log.debug("text type: "+text_type);
	  log.debug("text: "+text); 
	  log.debug("protected: "+isProtected);
      }
  }

  public void endWebLink() {
      if (log.isDebugEnabled())
	  log.debug("end weblink");
  }

  public void startWebLink(String the_title, String the_url, String the_target, String the_window_features, boolean isProtected) {
      if (log.isDebugEnabled()) {
	  log.debug("start weblink: "+the_title);
	  log.debug("link to: "+the_url);
	  log.debug("target window: "+the_target);
	  log.debug("window features: "+the_window_features);
	  log.debug("protected: "+isProtected);
      }
  }
 
  public void setWebLinkXml(Element the_link) {
      if (log.isDebugEnabled())
	  log.debug("weblink xml: "+the_link);
  }

  public void addFile(Element elem) {
	  //These are processed as standard text
	  String href = elem.getAttributeValue(HREF);
	  String sakaiId = baseName + elem.getAttributeValue(HREF);
	  String extension = Validator.getFileExtension(sakaiId);
	  String mime = ContentTypeImageService.getContentType(extension);
	  if (mime != null && mime.startsWith("text/"))
		  return;
	  addFile(href);
  }

  public void addFile(String the_file_id) {

      if (filesAdded.contains(the_file_id))
	  return;

      InputStream infile = null;
      for (int tries = 1; tries < 3; tries++) {
        try {
	  infile = utils.getFile(the_file_id);
	  String name = the_file_id;
	  int slash = the_file_id.lastIndexOf("/");
	  if (slash >=0 )
	      name = name.substring(slash+1);
	  String extension = Validator.getFileExtension(name);
	  String type = ContentTypeImageService.getContentType(extension);

	  log.debug("Length of baseName is:" + baseName.length());
	  log.debug("Length of the_file_id is:" + the_file_id.length());
	  //Restrict file length to 250
	  if ((baseName + the_file_id).length() > 250) {
		 the_file_id = the_file_id.substring(0,250-baseName.length()); 
	  }
	  ContentResourceEdit edit = ContentHostingService.addResource(baseName + the_file_id);

	  edit.setContentType(type);
	  edit.setContent(infile);
	  edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
	  // if roles specified for this resource and student not in it, hide it
	  if (roles != null && !roles.contains("Learner"))
	      edit.setAvailability(true, null, null);
	  ContentHostingService.commitResource(edit, NotificationService.NOTI_NONE);
	  filesAdded.add(the_file_id);

        } catch (IdUsedException e) {
	  // remove existing if we are importing whole site.
	  // otherwise this is an error (and should be impossible, as this is a new directory)
	  if (importtop && tries == 1) {
	      try {
		  ContentHostingService.removeResource(baseName + the_file_id);
		  continue;
	      } catch (Exception e1) {
	      }
	  }
	  simplePageBean.setErrKey("simplepage.create.resource.failed", e + ": " + the_file_id);
	  log.warn("CC loader: unable to get file " + the_file_id + " error: " + e);
        } catch (Exception e) {
	  simplePageBean.setErrKey("simplepage.create.resource.failed", e + ": " + the_file_id);
	  log.warn("CC loader: unable to get file " + the_file_id + " error: " + e);
        }
        break;  // if we get to the end, no need to retry; really a goto would be clearer
      }
  }

  public void endWebContent() {
      if (log.isDebugEnabled())
	  log.debug("ending webcontent");
  }

  public void startWebContent(String entry_point, boolean isProtected) {
      if (log.isDebugEnabled()) {
	  log.debug("start web content");
	  log.debug("protected: "+isProtected);
	  if (entry_point!=null) {
	      log.debug("entry point is: "+entry_point);
	  }
      }
  }

  public void endLearningApplicationResource() {
      if (log.isDebugEnabled())
	  log.debug("end learning application resource");
  }

  public void startLearningApplicationResource(String entry_point, boolean isProtected) {
      if (log.isDebugEnabled()) {
	  log.debug("start learning application resource");
	  log.debug("protected: "+isProtected);
	  if (entry_point!=null) {
	      log.debug("entry point is: "+entry_point);
	  }
      }
  }

  public void endAssessment() {
      if (log.isDebugEnabled())
	  log.debug("end assessment");    
  }

  public void setAssessmentXml(Element xml) {
      if (log.isDebugEnabled())
	  log.debug("assessment xml: "+xml);
  }

  public void startAssessment(String the_file_name, boolean isProtected) {
      if (log.isDebugEnabled()) {
	  log.debug("start assessment contained in: "+the_file_name);
	  log.debug("protected: "+isProtected);
      }
  }

  public void endQuestionBank() {
      if (log.isDebugEnabled())
	  log.debug("end question bank");
  }

  public void setQuestionBankXml(Element the_xml) {
      if (log.isDebugEnabled())
	  log.debug("question bank xml: "+the_xml);
  }

  public void startQuestionBank(String the_file_name, boolean isProtected) {
      if (log.isDebugEnabled()) {
	  log.debug("start question bank in: "+the_file_name);
	  log.debug("protected: "+isProtected);
      }
  }

  public void setAuthorizationServiceXml(Element the_node) {
      if (log.isDebugEnabled())
	  log.debug(the_node);
  }

  public void setAuthorizationService(String cartridgeId, String webservice_url) {
      if (log.isDebugEnabled())
	  log.debug("adding auth service for "+cartridgeId+" @ "+webservice_url);
  }

  public void endAuthorization() {
      if (log.isDebugEnabled())
	  log.debug("end of authorizations");
  }

  public void startAuthorization(boolean isCartridgeScope, boolean isResourceScope, boolean isImportScope) {
      if (log.isDebugEnabled()) {
	  log.debug("start of authorizations");
	  log.debug("protect all: "+isCartridgeScope);
	  log.debug("protect resources: "+isResourceScope);
	  log.debug("protect import: "+isImportScope);
      }
  }

  public void endManifestMetadata() {
      if (log.isDebugEnabled())
	  log.debug("end of manifest metadata");
  }

  public void startManifestMetadata(String schema, String schema_version) {
      if (log.isDebugEnabled()) {
	  log.debug("start manifest metadata");
	  log.debug("schema: "+schema);
	  log.debug("schema_version: "+schema_version);
      }
  }
 
  public void setPresentationXml(Element the_xml) {
      if (log.isDebugEnabled())
	  log.debug("QTI presentation xml: "+the_xml);
  }

  public void setQTICommentXml(Element the_xml) {
      if (log.isDebugEnabled())
	  log.debug("QTI comment xml: "+the_xml);
  }

  public void setSection(String ident, String title) {
      if (log.isDebugEnabled()) {
	  log.debug("set section ident: "+ident);
	  log.debug("set section title: "+title);
      }
  }

  public void setSectionXml(Element the_xml) {
      if (log.isDebugEnabled())
	  log.debug("set Section Xml: "+the_xml);
  }

  public void endQTIMetadata() {
      if (log.isDebugEnabled())
	  log.debug("end of QTI metadata");
  }

  public void setManifestMetadataXml(Element the_md) {
      if (log.isDebugEnabled())
	  log.debug("manifest md xml: "+the_md);    
      // NOTE: need to handle languages
      if (the_md != null) {
      Element general = the_md.getChild(GENERAL, ns.getLom());
      if (general != null) {
	  Element tnode = general.getChild(TITLE, ns.getLom());
	  if (tnode != null) {
    	  title = tnode.getChildTextTrim(LANGSTRING, ns.getLom());
	      if (title == null || title.equals(""))
	    	  title = tnode.getChildTextTrim(STRING, ns.getLom());
	  }
	  Element tdescription=general.getChild(DESCRIPTION, ns.getLom());
	  if (tdescription != null) {
	      description = tdescription.getChildTextTrim(STRING, ns.getLom());
	  }

      }
      }
      if (title == null || title.equals(""))
	  title = "Cartridge";
      if ("".equals(description))
	  description = null;
      ContentCollection baseCollection = makeBaseFolder(title);
      baseName = baseCollection.getId();
      baseUrl = baseCollection.getUrl();
      // kill the hostname part. We want to use relative URLs
      int relPart = baseUrl.indexOf("/access/");
      if (relPart >= 0)
	  baseUrl = baseUrl.substring(relPart);
      //Start a folder to hold the entire sites content rather than create new top level pages
      String singlePage = ServerConfigurationService.getString("lessonbuilder.cc.import.singlepage", "true");
      if ("true".equals(singlePage))
    	  startCCFolder(null);

  }

  public void setResourceMetadataXml(Element the_md) {
      Iterator mdroles = the_md.getDescendants(new ElementFilter("intendedEndUserRole", ns.lom_ns()));
      while (mdroles.hasNext()) {
	  Element role = (Element)mdroles.next();
	  if (roles == null)
	      roles = new HashSet<String>();
	  roles.add(role.getChildText("value",  ns.lom_ns()));
      }

      if (log.isDebugEnabled())
	  log.debug("resource md xml: "+the_md); 
  }

  public void addQTIMetadataField(String label, String entry) {
      if (log.isDebugEnabled()) {
	  log.debug("QTI md label: "+label);
	  log.debug("QTI md entry: "+entry);
      }
}

  public void setQTIComment(String the_comment) {
      if (log.isDebugEnabled())
	  log.debug("QTI comment: "+the_comment);
  }

  public void endDependency() {
      if (log.isDebugEnabled())
	  log.debug("end dependency");
  }

  public void startDependency(String source, String target) {
      if (log.isDebugEnabled())
	  log.debug("start dependency- resource : "+source+" is dependent upon: "+target);
  }

  public void startResource(String id, boolean isProtected) {

      roles = null;
      if (log.isDebugEnabled())
	  log.debug("start resource: "+id+ " protected: "+isProtected);
  }

  public void setResourceXml(Element the_xml) {
      if (log.isDebugEnabled())
	  log.debug("resource xml: "+the_xml);
  }

  public void endResource() {
      if (log.isDebugEnabled())
	  log.debug("end resource"); 
  }

  public void addAssessmentItem(QTIItem the_item) {
      if (log.isDebugEnabled())
	  log.debug("add QTI assessment item: "+the_item.toString());
    
  }

  public void addQTIMetadataXml(Element the_md) {
      if (log.isDebugEnabled())
	  log.debug("add QTI metadata xml: "+the_md);
    
  }

  public void startQTIMetadata() {
      if (log.isDebugEnabled())
	  log.debug("start QTI metadata");
  }

  public void setDiscussionXml(Element the_element) {
      if (log.isDebugEnabled())
	  log.debug("set discussion xml: "+the_element); 
  }

  public void addQuestionBankItem(QTIItem the_item) {
      if (log.isDebugEnabled())
	  log.debug("add QTI QB item: "+the_item.toString()); 
  }

  public void setQuestionBankDetails(String the_ident) {
      if (log.isDebugEnabled())
	  log.debug("set qti qb details: "+the_ident);  
  }

}

