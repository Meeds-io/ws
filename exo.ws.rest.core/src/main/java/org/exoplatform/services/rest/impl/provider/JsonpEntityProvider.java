/*
 * Copyright (C) 2014 eXo Platform SAS.
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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * This provider adds the support of JSONP defined by http://www.json-p.org/
 * 
 * @author <a href="mailto:nfilotto@exoplatform.com">Nicolas Filotto</a>
 * @version $Id$
 *
 */
@Provider
@Produces({"application/javascript", "text/javascript", "application/json-p", "text/json-p"})
public class JsonpEntityProvider extends JsonEntityProvider
{

   /**
    * The name of the parameter that we use to define the callback
    */
   private static final String JSONP_PARAMETER = "jsonp";

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      ApplicationContext context = ApplicationContextImpl.getCurrent();
      if (context == null)
         throw new IOException("Cannot get the jsonp parameter, the context is null");
      String callback = context.getQueryParameters().getFirst(JSONP_PARAMETER);
      if (callback == null)
         throw new IOException("The parameter '" + JSONP_PARAMETER + "' has not been set");
      StringBuilder result = new StringBuilder();
      result.append(callback).append('(');
      String charset = mediaType.getParameters().get("charset");
      if (t instanceof String)
      {
         result.append(t);
         result.append(");");
         IOHelper.writeString(result.toString(), entityStream, charset);
      }
      else
      {
         IOHelper.writeString(result.toString(), entityStream, charset);
         super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
         IOHelper.writeString(");", entityStream, charset);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return super.isWriteable(type, genericType, annotations, mediaType) || String.class.isAssignableFrom(type);
   }

}
