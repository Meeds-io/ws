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

package org.exoplatform.ws.frameworks.json;

import java.util.List;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class BeanWithSimpleEnum
{
   private String name;

   private StringEnum count;

   private StringEnum[] counts;

   private List<StringEnum> countList;

   public StringEnum getCount()
   {
      return count;
   }

   public List<StringEnum> getCountList()
   {
      return countList;
   }

   public StringEnum[] getCounts()
   {
      return counts;
   }

   public String getName()
   {
      return name;
   }

   public void setCount(StringEnum count)
   {
      this.count = count;
   }

   public void setCountList(List<StringEnum> countList)
   {
      this.countList = countList;
   }

   public void setCounts(StringEnum[] counts)
   {
      this.counts = counts;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}