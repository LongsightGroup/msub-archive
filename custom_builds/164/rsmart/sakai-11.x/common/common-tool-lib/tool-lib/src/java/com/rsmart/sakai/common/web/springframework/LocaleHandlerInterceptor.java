/*
 * Copyright 2008 The rSmart Group
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Contributor(s): jbush
 */
package com.rsmart.sakai.common.web.springframework;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.MessageSource;
import org.sakaiproject.util.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;



/**
 *
 */
public class LocaleHandlerInterceptor implements HandlerInterceptor {
   private MessageSource messageSource;
   public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
      ResourceLoader rb = new ResourceLoader();
      Locale locale = rb.getLocale();
      httpServletRequest.setAttribute("locale", locale.toString());
      httpServletRequest.setAttribute("localeRef", locale);
      httpServletRequest.setAttribute("messageSource", messageSource);
      return true;
   }

   public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView modelAndView) throws Exception {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception exception) throws Exception {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public MessageSource getMessageSource() {
      return messageSource;
   }

   public void setMessageSource(MessageSource messageSource) {
      this.messageSource = messageSource;
   }
}
