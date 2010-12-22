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

import java.net.URL;

/**
 * Item of Groovy classpath. It may describe source file of folder that contains
 * source files. If <code>ClassPathEntry</code> point to the folder then like
 * Java package structure is expected.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ClassPathEntry
{
   /** Type of class-path entry. */
   public enum EntryType {
      SRC_DIR, FILE
   }

   /** Type of entry. */
   private EntryType type;

   /** URL. */
   private URL path;

   public ClassPathEntry(EntryType type, URL path)
   {
      this.type = type;
      this.path = path;
   }

   public EntryType getType()
   {
      return type;
   }

   public URL getPath()
   {
      return path;
   }

   public void setPath(URL path)
   {
      this.path = path;
   }
}
