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

import java.lang.annotation.Annotation;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.exoplatform.services.rest.Property;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ParameterResolverFactory
{

   /**
    * Constructor.
    */
   private ParameterResolverFactory()
   {
   }

   /**
    * Create parameter resolver for supplied annotation.
    * 
    * @param annotation JAX-RS annotation
    * @return ParameterResolver
    */
   @SuppressWarnings("unchecked")
   public static ParameterResolver createParameterResolver(Annotation annotation)
   {
      Class clazz = annotation.annotationType();
      if (clazz == CookieParam.class)
         return new CookieParameterResolver((CookieParam)annotation);
      if (clazz == Context.class)
         return new ContextParameterResolver((Context)annotation);
      if (clazz == FormParam.class)
         return new FormParameterResolver((FormParam)annotation);
      if (clazz == HeaderParam.class)
         return new HeaderParameterResolver((HeaderParam)annotation);
      if (clazz == MatrixParam.class)
         return new MatrixParameterResolver((MatrixParam)annotation);
      if (clazz == PathParam.class)
         return new PathParameterResolver((PathParam)annotation);
      if (clazz == QueryParam.class)
         return new QueryParameterResolver((QueryParam)annotation);
      if (clazz == Property.class)
         return new PropertyResolver((Property)annotation);
      return null;
   }

}
