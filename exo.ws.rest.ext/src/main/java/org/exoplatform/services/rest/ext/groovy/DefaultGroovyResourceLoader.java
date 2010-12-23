/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.services.rest.ext.groovy;

import groovy.lang.GroovyResourceLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: DefaultGroovyResourceLoader.java 2680 2010-06-22 11:43:00Z
 *          aparfonov $
 */
public class DefaultGroovyResourceLoader implements GroovyResourceLoader
{
   private static final String[] DEFAULT_SCRIPT_EXTENSIONS = new String[]{".groovy"};

   protected final String[] extensions;

   protected final URL[] roots;

   protected final URL[] files;

   // TODO need configurable ?
   private int maxEntries = 512;

   protected final Map<String, URL> resources;

   @SuppressWarnings("serial")
   public DefaultGroovyResourceLoader(URL[] roots, URL[] files, String[] extensions) throws MalformedURLException
   {
      this.files = files;
      this.roots = new URL[roots.length];
      if (extensions == null || extensions.length == 0)
         extensions = DEFAULT_SCRIPT_EXTENSIONS;
      this.extensions = extensions;
      for (int i = 0; i < roots.length; i++)
      {
         String str = roots[i].toString();
         if (str.charAt(str.length() - 1) != '/')
            this.roots[i] = new URL(str + '/');
         else
            this.roots[i] = roots[i];
      }
      resources = Collections.synchronizedMap(new LinkedHashMap<String, URL>() {
         protected boolean removeEldestEntry(Entry<String, URL> eldest)
         {
            return size() > maxEntries;
         }
      });
   }

   public DefaultGroovyResourceLoader(URL[] roots, URL[] files) throws MalformedURLException
   {
      this(roots, files, null);
   }

   public DefaultGroovyResourceLoader(URL[] roots) throws MalformedURLException
   {
      this(roots, new URL[0]);
   }

   public DefaultGroovyResourceLoader(URL root) throws MalformedURLException
   {
      this(new URL[]{root});
   }

   /**
    * {@inheritDoc}
    */
   public final URL loadGroovySource(String classname) throws MalformedURLException
   {
      URL resource = null;
      final String baseName = classname.replace('.', '/');
      String[] extensions = getScriptExtensions();
      for (int i = 0; i < extensions.length && resource == null; i++)
      {
         final String ext = extensions[i];
         try
         {
            resource = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
               public URL run() throws MalformedURLException
               {
                  return getResource(baseName + ext);
               }
            });
         }
         catch (PrivilegedActionException e)
         {
            Throwable cause = e.getCause();
            // MalformedURLException
            throw (MalformedURLException)cause;
         }
      }
      return resource;
   }

   protected URL getResource(String filename) throws MalformedURLException
   {
      URL resource = null;
      filename = filename.intern();
      synchronized (filename)
      {
         resource = resources.get(filename);
         boolean inCache = resource != null;
         if (inCache && !checkResource(resource))
            resource = null; // Resource in cache is unreachable.
         for (int i = 0; i < files.length && resource == null; i++)
         {
            URL tmp = files[i];
            if (tmp.toString().endsWith(filename) && checkResource(tmp))
               resource = tmp;
         }
         for (int i = 0; i < roots.length && resource == null; i++)
         {
            URL tmp = new URL(roots[i], filename);
            if (checkResource(tmp))
               resource = tmp;
         }
         if (resource != null)
            resources.put(filename, resource);
         else if (inCache)
            resources.remove(filename);
      }

      return resource;
   }

   protected boolean checkResource(URL resource)
   {
      try
      {
         resource.openStream().close();
         return true;
      }
      catch (IOException e)
      {
         return false;
      }
   }

   protected String[] getScriptExtensions()
   {
      return extensions;
   }
}
