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

import java.util.List;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.PathSegment;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MatrixParameterResolver extends ParameterResolver<MatrixParam>
{

   /**
    * See {@link MatrixParam}.
    */
   private final MatrixParam matrixParam;

   /**
    * @param matrixParam MatrixParam
    */
   MatrixParameterResolver(MatrixParam matrixParam)
   {
      this.matrixParam = matrixParam;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object resolve(org.exoplatform.services.rest.Parameter parameter, ApplicationContext context)
      throws Exception
   {
      String param = matrixParam.value();
      TypeProducer typeProducer =
         ParameterHelper.createTypeProducer(parameter.getParameterClass(), parameter.getGenericType());
      List<PathSegment> pathSegments = context.getUriInfo().getPathSegments(!parameter.isEncoded());

      PathSegment pathSegment = pathSegments.get(pathSegments.size() - 1);

      return typeProducer.createValue(param, pathSegment.getMatrixParameters(), parameter.getDefaultValue());
   }

}
