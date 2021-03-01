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
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.method.TypeProducer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FormParameterResolver extends ParameterResolver<FormParam>
{

   /**
    * Form generic type.
    */
   private static final Type FORM_TYPE = (ParameterizedType)MultivaluedMapImpl.class.getGenericInterfaces()[0];

   /**
    * See {@link FormParam}.
    */
   private final FormParam formParam;

   /**
    * @param formParam FormParam
    */
   FormParameterResolver(FormParam formParam)
   {
      this.formParam = formParam;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object resolve(org.exoplatform.services.rest.Parameter parameter, ApplicationContext context)
      throws Exception
   {
      String param = this.formParam.value();
      TypeProducer typeProducer =
         ParameterHelper.createTypeProducer(parameter.getParameterClass(), parameter.getGenericType());

      MediaType conetentType = context.getHttpHeaders().getMediaType();
      MessageBodyReader reader =
         context.getProviders().getMessageBodyReader(MultivaluedMap.class, FORM_TYPE, null, conetentType);
      if (reader == null)
         throw new IllegalStateException("Can't find appropriate entity reader for entity type "
            + MultivaluedMap.class.getName() + " and content-type " + conetentType);
   
      InputStream entityStream = context.getContainerRequest().getEntityStream();
      if (entityStream.available()==0 && EnvironmentContext.getCurrent()!=null &&
          EnvironmentContext.getCurrent().get(HttpServletRequest.class)!=null) {
         //the stream have already been read
         //get parameters in request
         HttpServletRequest httpServletRequest =
             (HttpServletRequest) EnvironmentContext.getCurrent().get(HttpServletRequest.class);
         String requestContent = "";
         for (Enumeration<String> e = httpServletRequest.getParameterNames(); e.hasMoreElements();) {
            String key = e.nextElement();
            if (!requestContent.equals("")) {
               requestContent=requestContent+"&";
            }
            requestContent=requestContent+key+"="+httpServletRequest.getParameter(key);
         }
         entityStream = new ByteArrayInputStream(requestContent.getBytes());
      }
      MultivaluedMap<String, String> form =
         (MultivaluedMap<String, String>)reader.readFrom(MultivaluedMap.class, FORM_TYPE, null, conetentType, context
            .getHttpHeaders().getRequestHeaders(), entityStream);
      return typeProducer.createValue(param, form, parameter.getDefaultValue());
   }

   
}
