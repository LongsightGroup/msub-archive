<%@ page import="org.apache.commons.fileupload.DiskFileUpload"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItem"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@page import="org.sakaiproject.component.cover.ServerConfigurationService"%>
<!--
 ***********************************************************************************
 * $URL$
 * $Id$  
 ***********************************************************************************
 *
 * Copyright (c) 2008 Etudes, Inc.
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
 **********************************************************************************
-->
<%
			String uploadTempDir = ServerConfigurationService.getString("melete.uploadDir", "");
			for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();)
				{
					String oneKey = (String) e.nextElement();
					if(oneKey.startsWith("sferyx"))
					{
							// store the image at uploads directory
						try {
							org.apache.commons.fileupload.disk.DiskFileItem item = (org.apache.commons.fileupload.disk.DiskFileItem)request.getAttribute(oneKey);
							int lastSlash = item.getName().lastIndexOf("/");
							String fileName = item.getName().substring(lastSlash);
							lastSlash = fileName.lastIndexOf("\\");
							if (lastSlash > -1 ) {
								fileName = fileName.substring(lastSlash).replace('\\', '/');
							}
							
							InputStream in = item.getInputStream();
							FileOutputStream fout = null;
							if (uploadTempDir!= null && uploadTempDir.length() > 0)	
							{
								fout = new FileOutputStream(uploadTempDir + fileName);
								
								// Transfer bytes from in to out
								byte[] buf = new byte[1024];
								int len;
								while ((len = in.read(buf)) > 0) {
								    fout.write(buf, 0, len);
								}
								in.close();
								fout.close();
							}
							else {
								System.out.println("melete upload directory property is not set. temp item place is:" + item.getStoreLocation());
							}
							
						}  catch (Exception ex) {
							// do nothing
						}
					} // if end
				} // for end			
%>