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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: CaseInsensetiveMap.java 167 2009-09-10 13:14:45Z max_shaposhnik $
 */
public class CaseInsensetiveMap<T> extends java.util.HashMap<String, T>
{

   private static final long serialVersionUID = -8562529039657285360L;

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
   public T get(Object key)
   {
      return super.get(getKey(key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T put(String key, T value)
   {
      return super.put(getKey(key), value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T remove(Object key)
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
