/*
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

import groovy.lang.GroovyClassLoader;

import org.exoplatform.services.rest.ext.groovy.ClassPathEntry.EntryType;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory of Groovy class loader. It can provide preset GroovyClassLoader
 * instance or customized instance of GroovyClassLoader able resolve additional
 * Groovy source files.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GroovyClassLoaderProvider.java 3701 2010-12-22 10:15:37Z
 *          aparfonov $
 */
public class GroovyClassLoaderProvider
{
   /** Preset default GroovyClassLoader. */
   private GroovyClassLoader defaultClassLoader;

   /**
    * Create GroovyClassLoaderProvider that will use specified GroovyClassLoader
    * as default.
    * 
    * @param defaultClassLoader GroovyClassLoader
    */
   public GroovyClassLoaderProvider(GroovyClassLoader defaultClassLoader)
   {
      this.defaultClassLoader = defaultClassLoader;
   }

   public GroovyClassLoaderProvider()
   {
      defaultClassLoader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>() {
         public GroovyClassLoader run()
         {
            return new GroovyClassLoader(getClass().getClassLoader());
         }
      });
   }

   /**
    * Get default GroovyClassLoader.
    * 
    * @return default GroovyClassLoader
    */
   public GroovyClassLoader getGroovyClassLoader()
   {
      return defaultClassLoader;
   }

   /**
    * Get customized instance of GroovyClassLoader that able to resolve
    * additional Groovy source files.
    * 
    * @param classPath additional Groovy sources
    * @return GroovyClassLoader
    * @throws MalformedURLException if any of entries in <code>classPath</code>
    *            has invalid URL.
    */
   public GroovyClassLoader getGroovyClassLoader(ClassPath classPath) throws MalformedURLException
   {
      List<URL> files = new ArrayList<URL>();
      List<URL> roots = new ArrayList<URL>();
      ClassPathEntry[] classPathEntries = classPath.getEntries();
      if (classPathEntries != null && classPathEntries.length > 0)
      {
         for (int i = 0; i < classPathEntries.length; i++)
         {
            ClassPathEntry classPathEntry = classPathEntries[i];
            if (EntryType.SRC_DIR == classPathEntry.getType())
            {
               roots.add(classPathEntry.getPath());
            }
            else
            {
               files.add(classPathEntry.getPath());
            }
         }
      }
      final GroovyClassLoader parent = getGroovyClassLoader();
      GroovyClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<GroovyClassLoader>() {
         public GroovyClassLoader run()
         {
            return new GroovyClassLoader(parent);
         }
      });
      classLoader.setResourceLoader(new DefaultGroovyResourceLoader(roots.toArray(new URL[roots.size()]), files
         .toArray(new URL[files.size()]), classPath.getExtensions()));
      return classLoader;
   }

   /**
    * Set default Groovy class loader.
    * 
    * @param defaultClassLoader default Groovy class loader
    * @throws NullPointerException if <code>defaultClassLoader == null</code>
    */
   public void setGroovyClassLoader(GroovyClassLoader defaultClassLoader)
   {
      if (defaultClassLoader == null)
         throw new NullPointerException("GroovyClassLoader may not be null. ");
      this.defaultClassLoader = defaultClassLoader;
   }
}
