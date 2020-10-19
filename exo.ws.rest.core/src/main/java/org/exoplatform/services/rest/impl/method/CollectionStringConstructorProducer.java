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

import java.lang.reflect.Constructor;

/**
 * Produce collections each element of it is object which has constructor with
 * single String argument.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class CollectionStringConstructorProducer extends BaseCollectionProducer
{

   /**
    * This constructor will be used for creation collection elements.
    */
   private Constructor<?> constructor;

   /**
    * Constructs new instance of CollectionStringConstructorProducer.
    * 
    * @param collectionClass class of collection which must be created
    * @param constructor this constructor will be used for produce elements of
    *          collection
    */
   CollectionStringConstructorProducer(Class<?> collectionClass, Constructor<?> constructor)
   {
      super(collectionClass);
      this.constructor = constructor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Object createValue(String value) throws Exception
   {
      if (value == null)
         return null;

      return constructor.newInstance(value);
   }

}
