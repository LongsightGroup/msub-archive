/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/signup/branches/2-9-x/tool/src/java/org/sakaiproject/signup/tool/jsf/MessageUIBean.java $
 * $Id: MessageUIBean.java 75910 2011-08-01 01:21:46Z steve.swinsburg@gmail.com $
***********************************************************************************
 *
 * Copyright (c) 2007, 2008, 2009 Yale University
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *   
 * See the LICENSE.txt distributed with this file.
 *
 **********************************************************************************/
package org.sakaiproject.signup.tool.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This class will provide a placeholder for keeping messages.
 * </P>
 */
public class MessageUIBean {

	private boolean error = false;
	private boolean info = false;

	private List<String> errorMessages = new ArrayList<String>();
	private List<String> infoMessages = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 */
	public MessageUIBean() {
	}

	/**
	 * This is a setter.
	 * 
	 * @param errorMsg
	 *            a string error message value.
	 */
	public void setErrorMessage(String msg) {
		if (!this.errorMessages.contains(msg)) {
			this.errorMessages.add(msg);
		}
		setError(true);
	}
	
	/**
	 * Setter for info message
	 * @param msg
	 */
	public void setInfoMessage(String msg) {
		if (!this.infoMessages.contains(msg)) {
			this.infoMessages.add(msg);
		}
		setInfo(true);
	}

	/**
	 * This is a getter method for UI.
	 * 
	 * @return a string value.
	 */
	public String getErrorMessage() {
		StringBuffer sb = new StringBuffer();
		for (String msg: errorMessages) {
			sb.append(msg);
			if(errorMessages.size() > 1) {
				sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
		}
		resetError();
		return sb.toString();
	}
	
	/**
	 * Getter for info messages
	 * @return
	 */
	public String getInfoMessage() {
		StringBuffer sb = new StringBuffer();
		for(String msg:infoMessages) {
			sb.append(msg);
			if(infoMessages.size() > 1) {
				sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
		}
		resetInfo();
		return sb.toString();
	}

	/**
	 * this is for UI purpose.
	 * 
	 * @return true if there is a error message.
	 */
	public boolean isError() {
		return error;
	}
	
	/**
	 * Do we have any info messages?
	 * @return
	 */
	public boolean isInfo() {
		return info;
	}

	/**
	 * This is a setter.
	 * 
	 * @param error
	 *            a boolean value.
	 */
	public void setError(boolean error) {
		this.error = error;
	}
	
	/**
	 * Setter for info flag
	 * @param error
	 */
	public void setInfo(boolean info) {
		this.info = info;
	}

	private void resetError() {
		errorMessages.clear();
		setError(false);
	}
	
	private void resetInfo() {
		infoMessages.clear();
		setInfo(false);
	}
}
