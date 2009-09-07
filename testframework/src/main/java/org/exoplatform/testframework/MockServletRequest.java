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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Created by The eXo Platform SARL Author : Mestrallet Benjamin
 * benjmestrallet@users.sourceforge.net
 */
public class MockServletRequest implements HttpServletRequest
{

   /** The parameters. */
   private Map parameters;

   /** The attributes. */
   private Map attributes;

   /** The session. */
   private HttpSession session;

   /** The locale. */
   private Locale locale;

   /** The secure. */
   private boolean secure;

   /** The headers. */
   private Map headers;

   /** The enc. */
   private String enc = "ISO-8859-1";

   /** The path info_. */
   private String pathInfo_;

   /** The request ur i_. */
   private String requestURI_;

   /** The url. */
   private URL url;

   /** The method. */
   private String method = "GET";

   /** The context path. */
   private String contextPath = "";

   /** The remote user. */
   private String remoteUser = "REMOTE USER FROM MOCK";

   /**
    * Instantiates a new mock servlet request.
    * 
    * @param session the session
    * @param locale the locale
    */
   public MockServletRequest(HttpSession session, Locale locale)
   {

      this(session, locale, false);

   }

   /**
    * Instantiates a new mock servlet request.
    * 
    * @param session the session
    * @param locale the locale
    * @param secure the secure
    */
   public MockServletRequest(HttpSession session, Locale locale, boolean secure)
   {

      this(session, null, null, locale, secure);
   }

   /**
    * Instantiates a new mock servlet request.
    * 
    * @param session the session
    * @param url the url
    * @param contextPath the context path
    * @param locale the locale
    * @param secure the secure
    */
   public MockServletRequest(HttpSession session, URL url, String contextPath, Locale locale, boolean secure)
   {
      this.session = session;
      this.locale = locale;
      headers = new HashMap();
       parameters = new HashMap();
      attributes = new HashMap();
      this.secure = secure;
      if (url == null)
      {
         try
         {
            this.url = new URL("http://localhost:8080/");
            this.contextPath = "/context";
         }
         catch (MalformedURLException e)
         {
         }
      }
      else
      {
         this.url = url;
         this.contextPath = contextPath;
      }
   }

   /**
    * Reset.
    */
   public void reset()
   {
      parameters = new HashMap();
      attributes = new HashMap();
   }

   /**
    * {@inheritDoc}
    */
   public String getAuthType()
   {
      return DIGEST_AUTH;
   }

   /**
    * {@inheritDoc}
    */
   public Cookie[] getCookies()
   {
      return new Cookie[0];
   }

   /**
    * {@inheritDoc}
    */
   public long getDateHeader(String s)
   {
      return 0L;
   }

   /**
    * {@inheritDoc}
    */
   public String getHeader(String s)
   {
      return (String)headers.get(s);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getHeaders(String s)
   {
      if (headers.get(s) instanceof Collection)
         return Collections.enumeration((Collection)headers.get(s));
      else
      {
         Vector v = new Vector();
         v.add(headers.get(s));
         return v.elements();
      }
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getHeaderNames()
   {
      return Collections.enumeration(headers.keySet());
   }

   /**
    * {@inheritDoc}
    */
   public int getIntHeader(String s)
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public String getMethod()
   {

      return method;
   }

   /**
    * {@inheritDoc}
    */
   public String getPathInfo()
   {
      return pathInfo_;
   }

   /**
    * Sets the path info.
    * 
    * @param s the new path info
    */
   public void setPathInfo(String s)
   {
      pathInfo_ = s;
   }

   /**
    * {@inheritDoc}
    */
   public String getPathTranslated()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getContextPath()
   {
      return contextPath;
   }

   /**
    * {@inheritDoc}
    */
   public String getQueryString()
   {
      return url.getQuery();
   }

   /**
    * {@inheritDoc}
    */
   public String getRemoteUser()
   {
      return remoteUser;
   }

   /**
    * Sets the remote user.
    * 
    * @param remoteUser the new remote user
    */
   public void setRemoteUser(String remoteUser)
   {
      this.remoteUser = remoteUser;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isUserInRole(String s)
   {
      if ("auth-user".equals(s))
         return true;
      else
         return false;
   }

   /**
    * {@inheritDoc}
    */
   public Principal getUserPrincipal()
   {
      return new MockPrincipal();
   }

   /**
    * {@inheritDoc}
    */
   public String getRequestedSessionId()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getRequestURI()
   {
      if (this.requestURI_ == null)
         return url.getPath();
      else
         return requestURI_;
   }

   /**
    * Sets the request uri.
    * 
    * @param s the new request uri
    */
   public void setRequestURI(String s)
   {
      this.requestURI_ = s;
   }

   /**
    * {@inheritDoc}
    */
   public StringBuffer getRequestURL()
   {
      return new StringBuffer(url.toString());
   }

   /**
    * {@inheritDoc}
    */
   public String getServletPath()
   {
      return url.getPath();
   }

   /**
    * {@inheritDoc}
    */
   public HttpSession getSession(boolean b)
   {
      return session;
   }

   /**
    * {@inheritDoc}
    */
   public HttpSession getSession()
   {
      return session;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRequestedSessionIdValid()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRequestedSessionIdFromCookie()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRequestedSessionIdFromURL()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Deprecated
   public boolean isRequestedSessionIdFromUrl()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public Object getAttribute(String s)
   {
      return attributes.get(s);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getAttributeNames()
   {
      return new Vector(attributes.keySet()).elements();
   }

   /**
    * {@inheritDoc}
    */
   public String getCharacterEncoding()
   {
      return enc;
   }

   /**
    * {@inheritDoc}
    */
   public void setCharacterEncoding(String s) throws UnsupportedEncodingException
   {
      enc = s;
   }

   /**
    * {@inheritDoc}
    */
   public int getContentLength()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public String getContentType()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ServletInputStream getInputStream() throws IOException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getParameter(String s)
   {
      return (String)parameters.get(s);
   }

   /**
    * Sets the parameter.
    * 
    * @param s the s
    * @param value the value
    */
   public void setParameter(String s, Object value)
   {
      parameters.put(s, value);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getParameterNames()
   {
      return new Vector(parameters.keySet()).elements();
   }

   /**
    * {@inheritDoc}
    */
   public String[] getParameterValues(String s)
   {

      ArrayList<String> arr = new ArrayList<String>();
      Iterator it = parameters.keySet().iterator();
      while (it.hasNext())
      {

         String pname = (String)it.next();
         if (pname.equals(s))
            arr.add((String)parameters.get(s));
      }
      return arr.toArray(new String[arr.size()]);

   }

   /**
    * {@inheritDoc}
    */
   public Map getParameterMap()
   {
      return parameters;
   }

   /**
    * {@inheritDoc}
    */
   public String getProtocol()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getScheme()
   {
      return url.getProtocol();
   }

   /**
    * {@inheritDoc}
    */
   public String getServerName()
   {
      return url.getHost();
   }

   /**
    * {@inheritDoc}
    */
   public int getServerPort()
   {
      return url.getPort();
   }

   /**
    * {@inheritDoc}
    */
   public BufferedReader getReader() throws IOException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getRemoteAddr()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getRemoteHost()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void setAttribute(String s, Object o)
   {
      attributes.put(s, o);
   }

   /**
    * {@inheritDoc}
    */
   public void removeAttribute(String s)
   {
      attributes.remove(s);
   }

   /**
    * {@inheritDoc}
    */
   public Locale getLocale()
   {
      return locale;
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getLocales()
   {
      System.out.println("MOCK get Locale : " + locale);
      Vector v = new Vector();
      v.add(locale);
      return v.elements();
   }

   /**
    * {@inheritDoc}
    */
   public boolean isSecure()
   {
      return secure;
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
   public String getRealPath(String s)
   {
      return null;
   }

   //servlet 2.4 method
   /**
    * Gets the local port.
    * 
    * @return the local port
    */
   public int getLocalPort()
   {
      return 0;
   }

   /**
    * Gets the local addr.
    * 
    * @return the local addr
    */
   public String getLocalAddr()
   {
      return "127.0.0.1";
   }

   /**
    * Gets the local name.
    * 
    * @return the local name
    */
   public String getLocalName()
   {
      return "localhost";
   }

   /**
    * Gets the remote port.
    * 
    * @return the remote port
    */
   public int getRemotePort()
   {
      return 0;
   }
}
