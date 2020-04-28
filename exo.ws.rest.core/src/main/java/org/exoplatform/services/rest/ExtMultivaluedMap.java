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
package org.exoplatform.services.rest;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Extension of {@link MultivaluedMap} that allows to get not null value (empty
 * list) even there is no mapping value to supplied key.
 * 
 * @param <K> key
 * @param <V> value
 * @see #getList(Object)
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ExtMultivaluedMap<K, V> extends MultivaluedMap<K, V>
{

   /**
    * @param key key
    * @return never null even any value not found in the map, return empty list
    *         instead
    */
   List<V> getList(K key);

}
