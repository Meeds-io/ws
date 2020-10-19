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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.services.rest.method.TypeProducer;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Abstraction for single (not for collections) types.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTypeProducer implements TypeProducer
{

   /**
    * Create object from given string. In all extends for this class this method
    * must be specified to produce object of required type. String will be used
    * as parameter for constructor of object or static valueOf method.
    * 
    * @param value string value
    * @return newly created object
    * @throws Exception if any error occurs
    */
   protected abstract Object createValue(String value) throws Exception;

   /**
    * {@inheritDoc}
    */
   public Object createValue(String param, MultivaluedMap<String, String> values, String defaultValue) throws Exception
   {

      String value = values.getFirst(param);

      if (value != null)
         return createValue(value);
      else if (defaultValue != null)
         return createValue(defaultValue);

      return null;
   }

}
