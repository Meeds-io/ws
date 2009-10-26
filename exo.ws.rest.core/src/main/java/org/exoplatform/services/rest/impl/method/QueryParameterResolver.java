/*
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.method.TypeProducer;

import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class QueryParameterResolver extends ParameterResolver<QueryParam>
{

   /**
    * See {@link QueryParam}.
    */
   private final QueryParam queryParam;

   /**
    * @param queryParam QueryParam
    */
   QueryParameterResolver(QueryParam queryParam)
   {
      this.queryParam = queryParam;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object resolve(org.exoplatform.services.rest.Parameter parameter, ApplicationContext context)
      throws Exception
   {
      String param = this.queryParam.value();
      TypeProducer typeProducer =
         ParameterHelper.createTypeProducer(parameter.getParameterClass(), parameter.getGenericType());
      return typeProducer.createValue(param, context.getQueryParameters(!parameter.isEncoded()), parameter
         .getDefaultValue());
   }

}
