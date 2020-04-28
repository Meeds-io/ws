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
package org.exoplatform.services.rest.resource;

import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.method.MethodParameter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Abstraction for method in resource, this essence is common for
 * {@link ResourceMethodDescriptor}, {@link SubResourceMethodDescriptor},
 * {@link SubResourceLocatorDescriptor} .
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface GenericMethodResource
{

   /**
    * @return See {@link Method}
    */
   Method getMethod();

   /**
    * @return List of method parameters
    */
   List<MethodParameter> getMethodParameters();

   /**
    * @return parent resource descriptor
    */
   AbstractResourceDescriptor getParentResource();

   /**
    * @return invoker that must be used for processing current method
    */
   MethodInvoker getMethodInvoker();

   /**
    * @return Java type returned by method, see {@link #getMethod()}
    */
   Class<?> getResponseType();

}
