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
package org.exoplatform.httputils;

import java.util.HashMap;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: CaseInsensitiveMap.java 2822 2009-08-27 14:14:41Z andrew00x $
 */
public class CaseInsensitiveMap extends HashMap<String, String>
{

   private static final long serialVersionUID = 6637313979061607685L;

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean containsKey(Object key)
   {
      return super.containsKey(getKey(key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String get(Object key)
   {
      return super.get(getKey(key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String put(String key, String value)
   {
      return super.put(getKey(key), value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String remove(Object key)
   {
      return super.remove(getKey(key));
   }

   private String getKey(Object key)
   {
      if (key == null)
      {
         return null;
      }
      return key.toString().toLowerCase();
   }

}