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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: CaseInsensitiveMap.java 2822 2009-08-27 14:14:41Z andrew00x $
 */
public class CaseInsensitiveMultivaluedMap<T> extends HashMap<String, List<T>>
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
   public List<T> get(Object key)
   {
      return getList(getKey(key));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<T> put(String key, List<T> value)
   {
      return super.put(getKey(key), value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<T> remove(Object key)
   {
      return super.remove(getKey(key));
   }
   
   public T getFirst(String key)
   {
      List<T> l = getList(key);
      if (l.size() == 0)
         return null;
      return l.get(0);
   }
   
   private List<T> getList(String key)
   {
      List<T> l = super.get(getKey(key));
      if (l == null)
         l = new ArrayList<T>();
      put(key, l);
      return l;
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