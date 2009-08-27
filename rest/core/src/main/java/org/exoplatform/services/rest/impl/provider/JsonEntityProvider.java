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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.services.rest.provider.EntityProvider;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.BeanBuilder;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonWriterImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class JsonEntityProvider implements EntityProvider<Object>
{

   // It is common task for #isReadable() and #isWriteable
   // TODO Not sure it is required but ...
   // Investigation about checking can type be write as JSON (useful JSON).
   // Probably should be better added this checking in JSON framework.
   // Or probably enough check only content type 'application/json'
   // and if this content type set trust it and try parse/write

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // say as support all objects, see _TODO_ above
      return Object.class.isAssignableFrom(type);
   }

   /**
    * {@inheritDoc}
    */
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      try
      {
         JsonParser jsonParser = new JsonParserImpl();
         JsonHandler jsonHandler = new JsonDefaultHandler();
         jsonParser.parse(entityStream, jsonHandler);
         JsonValue jsonValue = jsonHandler.getJsonObject();
         // jsonValue can be null if stream empty
         if (jsonValue == null)
            return null;
         return new BeanBuilder().createObject(type, jsonValue);
      }
      catch (Exception e)
      {
         throw new IOException("Can't read from input stream " + e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      // say as support all objects, see _TODO_ above
      return Object.class.isAssignableFrom(type);
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      try
      {
         JsonValue jv = new JsonGeneratorImpl().createJsonObject(t);
         JsonWriter jsonWriter = new JsonWriterImpl(entityStream);
         jv.writeTo(jsonWriter);
         jsonWriter.flush();
      }
      catch (JsonException e)
      {
         throw new IOException("Can't write to output stream " + e);
      }

   }

}
