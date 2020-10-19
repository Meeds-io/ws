/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.services.test.mock;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;


/**
 * The Class MockServletContext.
 * 
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: $
 */
public class MockServletContext implements ServletContext
{

   private static final Log LOG = ExoLogger.getLogger("exo.ws.testframework.MockServletContext");

   /** The name. */
   private String name;

   /** The init params. */
   private HashMap<String, String> initParams;

   /** The attributes. */
   private HashMap<String, Object> attributes;

   /** The context path. */
   private String contextPath;

   /** The log buffer. */
   private StringBuffer logBuffer = new StringBuffer();

   /**
    * Instantiates a new mock servlet context.
    */
   public MockServletContext()
   {
      this("MockServletContext1");
   }

   /**
    * Instantiates a new mock servlet context.
    * 
    * @param name the name
    */
   public MockServletContext(String name)
   {
      this(name, "/" + name);
   }

   /**
    * Instantiates a new mock servlet context.
    * 
    * @param name the name
    * @param path the path
    */
   public MockServletContext(String name, String path)
   {
      this.name = name;
      this.contextPath = path;

      this.initParams = new HashMap<String, String>();
      this.attributes = new HashMap<String, Object>();
      this.attributes.put("javax.servlet.context.tempdir", path);
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Gets the log buffer.
    * 
    * @return the log buffer
    */
   public String getLogBuffer()
   {
      try
      {
         return logBuffer.toString();
      }
      finally
      {
         logBuffer = new StringBuffer();
      }
   }

   /**
    * {@inheritDoc}
    */
   public ServletContext getContext(String s)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public int getMajorVersion()
   {
      return 3;
   }

   /**
    * {@inheritDoc}
    */
   public int getMinorVersion()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public String getMimeType(String s)
   {
      return "text/html";
   }

   /**
    * {@inheritDoc}
    */
   public Set getResourcePaths(String s)
   {

      if (!s.endsWith("/"))
         s = s + "/";

      Set<String> set = new HashSet<String>();
      set.add("/WEB-INF/");

      try
      {
         URL url = getResource(s);
         File dir = new File(url.getPath());
         if (dir.isDirectory())
         {
            File[] arr = dir.listFiles();
            for (int i = 0; i < arr.length; i++)
            {
               File tmp = arr[i];
               if (tmp.isDirectory())
                  set.add(s + "/" + tmp.getName() + "/");
               else
                  set.add(s + "/" + tmp.getName());
            }
         }
      }
      catch (MalformedURLException e)
      {
         if (LOG.isTraceEnabled())
         {
            LOG.trace("An exception occurred: " + e.getMessage());
         }
      }
      return set;
   }

   /**
    * {@inheritDoc}
    */
   public URL getResource(String s) throws MalformedURLException
   {
      String path = "file:" + contextPath + s;
      URL url = new URL(path);
      return url;
   }

   /**
    * {@inheritDoc}
    */
   public InputStream getResourceAsStream(String s)
   {
      try
      {
         return getResource(s).openStream();
      }
      catch (IOException e)
      {
         e.printStackTrace(); //NOSONAR
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public RequestDispatcher getRequestDispatcher(String s)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public RequestDispatcher getNamedDispatcher(String s)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   public Servlet getServlet(String s) throws ServletException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   public Enumeration getServlets()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   public Enumeration getServletNames()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void log(String s)
   {
      logBuffer.append(s);
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   public void log(Exception e, String s)
   {
      logBuffer.append(s + e.getMessage());
   }

   /**
    * {@inheritDoc}
    */
   public void log(String s, Throwable throwable)
   {
      logBuffer.append(s + throwable.getMessage());
   }

   /**
    * Sets the context path.
    * 
    * @param s the new context path
    */
   public void setContextPath(String s)
   {
      contextPath = s;
   }

   /**
    * {@inheritDoc}
    */
   public String getRealPath(String s)
   {
      return contextPath + s;
   }

   /**
    * {@inheritDoc}
    */
   public String getServerInfo()
   {
      return null;
   }

   /**
    * Sets the init parameter.
    * 
    * @param name the name
    * @param value the value
    */
   public boolean setInitParameter(String name, String value)
   {
      initParams.put(name, value);
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public String getInitParameter(String name)
   {
      return initParams.get(name);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getInitParameterNames()
   {
      Vector<String> keys = new Vector<String>(initParams.keySet());
      return keys.elements();
   }

   /**
    * {@inheritDoc}
    */
   public Object getAttribute(String name)
   {
      return attributes.get(name);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getAttributeNames()
   {
      Vector<String> keys = new Vector<String>(attributes.keySet());
      return keys.elements();
   }

   /**
    * {@inheritDoc}
    */
   public void setAttribute(String name, Object value)
   {
      attributes.put(name, value);
   }

   /**
    * {@inheritDoc}
    */
   public void removeAttribute(String name)
   {
      attributes.remove(name);
   }

   /**
    * {@inheritDoc}
    */
   public String getServletContextName()
   {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   public String getContextPath()
   {
      return contextPath;
   }

   // servlet 3.0 API

   /**
    * {@inheritDoc}
    */
   public int getEffectiveMajorVersion()
   {
      return 3;
   }

   /**
    * {@inheritDoc}
    */
   public int getEffectiveMinorVersion()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public Dynamic addServlet(String servletName, String className)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Dynamic addServlet(String servletName, Servlet servlet)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ServletRegistration getServletRegistration(String servletName)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, ? extends ServletRegistration> getServletRegistrations()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public FilterRegistration getFilterRegistration(String filterName)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, ? extends FilterRegistration> getFilterRegistrations()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public SessionCookieConfig getSessionCookieConfig()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes)
   {
   }

   /**
    * {@inheritDoc}
    */
   public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void addListener(String className)
   {
   }

   /**
    * {@inheritDoc}
    */
   public <T extends EventListener> void addListener(T t)
   {
   }

   /**
    * {@inheritDoc}
    */
   public void addListener(Class<? extends EventListener> listenerClass)
   {
   }

   /**
    * {@inheritDoc}
    */
   public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public JspConfigDescriptor getJspConfigDescriptor()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ClassLoader getClassLoader()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void declareRoles(String... roleNames)
   {
   }

}
