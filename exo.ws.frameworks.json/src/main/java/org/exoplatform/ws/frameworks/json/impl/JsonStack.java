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

package org.exoplatform.ws.frameworks.json.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class JsonStack<T>
{

   private final List<T> elements;

   JsonStack()
   {
      elements = new ArrayList<T>(16);
   }

   boolean isEmpty()
   {
      return elements.isEmpty();
   }

   T peek()
   {
      return isEmpty() ? null : elements.get(elements.size() - 1);
   }

   T pop()
   {
      return isEmpty() ? null : elements.remove(elements.size() - 1);
   }

   void push(T token)
   {
      elements.add(token);
   }

   void clear()
   {
      elements.clear();
   }
}
