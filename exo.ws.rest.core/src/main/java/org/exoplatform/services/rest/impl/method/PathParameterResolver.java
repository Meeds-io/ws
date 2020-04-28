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

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.method.TypeProducer;

import javax.ws.rs.PathParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PathParameterResolver extends ParameterResolver<PathParam>
{

   /**
    * See {@link PathParam}.
    */
   private final PathParam pathParam;

   /**
    * @param pathParam PathParam
    */
   PathParameterResolver(PathParam pathParam)
   {
      this.pathParam = pathParam;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object resolve(org.exoplatform.services.rest.Parameter parameter, ApplicationContext context)
      throws Exception
   {
      String param = this.pathParam.value();
      TypeProducer typeProducer =
         ParameterHelper.createTypeProducer(parameter.getParameterClass(), parameter.getGenericType());
      return typeProducer.createValue(param, context.getPathParameters(!parameter.isEncoded()), parameter
         .getDefaultValue());
   }

}
