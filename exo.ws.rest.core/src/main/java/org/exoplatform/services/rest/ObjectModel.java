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
 * Abstract description of object.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ObjectModel
{

   /**
    * @return collections constructor, return empty collection not null if
    *         object is singleton. There is no setter for this to add new
    *         ConstructorInjector use
    *         <code>ObjectModel.getConstructorDescriptors().add(ConstructorInjector)</code>
    */
   List<ConstructorDescriptor> getConstructorDescriptors();

   /**
    * @return collections of object fields, return empty collection not null if
    *         object is singleton. There is no setter for this to add new
    *         ConstructorInjector use
    *         <code>ObjectModel.getFieldInjectors().add(FieldInjector)</code>
    */
   List<FieldInjector> getFieldInjectors();

   /**
    * @return {@link Class} of object
    */
   Class<?> getObjectClass();

   /**
    * @param key
    * @return property by key
    * @see #getProperties()
    */
   List<String> getProperty(String key);

   /**
    * Optional attributes.
    *
    * @return all properties. If there is no any optional attributes then empty
    *         map returned never <code>null</code>
    */
   MultivaluedMap<String, String> getProperties();

}
