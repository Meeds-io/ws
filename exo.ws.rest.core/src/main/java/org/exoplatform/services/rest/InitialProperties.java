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

import java.util.Map;

/**
 * Container for properties, that may be injected in resource by @Context annotation.
 *   
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 *
 */
public interface InitialProperties
{

   /**
    * @return all properties.
    */
   Map<String, String> getProperties();

   /**
    * Get property.
    * 
    * @param name property name
    * @return value of property with specified name or null 
    */
   String getProperty(String name);

   /**
    * Set property.
    * 
    * @param name property name
    * @param value property value
    */
   void setProperty(String name, String value);

}
