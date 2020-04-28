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

import org.exoplatform.services.rest.resource.ResourceDescriptor;

/**
 * Object field. Useful for initialization object field if type is used in
 * per-request mode.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface FieldInjector extends Parameter, ResourceDescriptor
{

   /**
    * @return field name
    */
   String getName();

   /**
    * Set Object {@link java.lang.reflect.Field} using ApplicationContext for
    * resolve actual field value.
    * 
    * @param resource root resource or provider
    * @param context ApplicationContext
    */
   void inject(Object resource, ApplicationContext context);

}
