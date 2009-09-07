/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.testframework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * The Class MockServletContext.
 * 
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: $
 */
public class MockServletContext implements ServletContext
{

   /** The name_. */
   private String name_;

   /** The init params. */
   private HashMap initParams_;

   /** The attributes. */
   private HashMap attributes_;

   /** The context path. */
   private String contextPath_;

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
      name_ = name;
      initParams_ = new HashMap();
      attributes_ = new HashMap();
   }

   /**
    * Instantiates a new mock servlet context.
    * 
    * @param name the name
    * @param path the path
    */
   public MockServletContext(String name, String path)
   {
      this(name);
      contextPath_ = path;
      attributes_.put("javax.servlet.context.tempdir", path);
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name)
   {
      name_ = name;
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
      return 2;
   }

   /**
    * {@inheritDoc}
    */
   public int getMinorVersion()
   {
      return 4;
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

      Set set = new HashSet<String>();
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
      }
      return set;
   }

   /**
    * {@inheritDoc}
    */
   public URL getResource(String s) throws MalformedURLException
   {
      String path = "file:" + contextPath_ + s;
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
         e.printStackTrace();
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
      contextPath_ = s;
   }

   /**
    * {@inheritDoc}
    */
   public String getRealPath(String s)
   {
      return contextPath_ + s;
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
   public void setInitParameter(String name, String value)
   {
      initParams_.put(name, value);
   }

   /**
    * {@inheritDoc}
    */
   public String getInitParameter(String name)
   {
      return (String)initParams_.get(name);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getInitParameterNames()
   {
      Vector keys = new Vector(initParams_.keySet());
      return keys.elements();
   }

   /**
    * {@inheritDoc}
    */
   public Object getAttribute(String name)
   {
      return attributes_.get(name);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getAttributeNames()
   {
      Vector keys = new Vector(attributes_.keySet());
      return keys.elements();
   }

   /**
    * {@inheritDoc}
    */
   public void setAttribute(String name, Object value)
   {
      attributes_.put(name, value);
   }

   /**
    * {@inheritDoc}
    */
   public void removeAttribute(String name)
   {
      attributes_.remove(name);
   }

   /**
    * {@inheritDoc}
    */
   public String getServletContextName()
   {
      return name_;
   }

}
