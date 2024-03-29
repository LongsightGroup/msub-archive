/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.jsf.app;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * SakaiVariableResolver extends the standard variable resolved of the selected faced implementation. Special Sakai features include the ability to name any Sakai component (or Spring bean) by component (bean) name.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SakaiVariableResolver extends VariableResolver
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SakaiVariableResolver.class);

	/** The VariableResolver already in place that we add features to. */
	protected VariableResolver m_resolver = null;

	/**
	 * Construct taking the VariableResolver alreay in place that we decorate.
	 * 
	 * @param other
	 *        The VariableResolver already in place.
	 */
	public SakaiVariableResolver(VariableResolver other)
	{
		m_resolver = other;
		if (M_log.isDebugEnabled()) M_log.debug("constructed around: " + m_resolver);
	}

	/**
	 * @inheritDoc
	 */
	public Object resolveVariable(FacesContext context, String name) throws EvaluationException
	{
		if (M_log.isDebugEnabled()) M_log.debug("resolving: " + name);

		Object rv = null;

		// first, give the other a shot
		if (m_resolver != null)
		{
			rv = m_resolver.resolveVariable(context, name);
			if (rv != null)
			{
				if (M_log.isDebugEnabled()) M_log.debug("resolving: " + name + " with other to: " + rv);
				return rv;
			}
		}

		// now, we extend. Check for a component/bean, using the local Spring method to pick up local and global definitions.
		WebApplicationContext wac = null;
		
		try
		{
			wac	 = WebApplicationContextUtils.getWebApplicationContext((ServletContext) context
				.getExternalContext().getContext());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (wac != null)
		{
			// try the name as given
			try
			{
				rv = wac.getBean(name);
				if (rv != null)
				{
					if (M_log.isDebugEnabled()) M_log.debug("resolving: " + name + " via spring to : " + rv);
					return rv;
				}
			}
			catch (NoSuchBeanDefinitionException ignore)
			{
				// the bean doesn't exist, but its not necessarily an error
			}

			// since the jsf environment does not allow a dot in the name, convert from underbar to dot and try again
			if (name.indexOf('_') != -1)
			{
				String alternate = name.replace('_', '.');
				try
				{
					rv = wac.getBean(alternate);
	
					if (rv != null)
					{
						if (M_log.isDebugEnabled()) M_log.debug("resolving: " + alternate + " via spring to : " + rv);
						return rv;
					}
				}
				catch (NoSuchBeanDefinitionException ignore)
				{
					// the bean doesn't exist, but its not necessarily an error
				}
			}
		}

		// if no wac, try using the component manager
		else
			rv = ComponentManager.get(name);
		{
			if (rv != null)
			{
				if (M_log.isDebugEnabled()) M_log.debug("resolving: " + name + " via component manager to : " + rv);
				return rv;
			}

			// since the jsf environment does not allow a dot in the name, convert from underbar to dot and try again
			if (name.indexOf('_') != -1)
			{
				String alternate = name.replace('_', '.');
				rv = ComponentManager.get(alternate);

				if (rv != null)
				{
					if (M_log.isDebugEnabled()) M_log.debug("resolving: " + alternate + " via component manager to : " + rv);
					return rv;
				}
			}
		}

		if (M_log.isDebugEnabled()) M_log.debug("resolving: " + name + " unresolved!");
		return null;
	}
}



