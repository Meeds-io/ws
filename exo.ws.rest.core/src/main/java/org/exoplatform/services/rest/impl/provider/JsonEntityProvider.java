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
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils.Types;
import org.exoplatform.ws.frameworks.json.impl.JsonWriterImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

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
   // Not sure it is required but ...
   // Investigation about checking can type be write as JSON (useful JSON).
   // Probably should be better added this checking in JSON framework.
   // Or probably enough check only content type 'application/json'
   // and if this content type set trust it and try parse/write

   /** Do not process via JSON "known" JAX-RS types and some more. */
   private static final Class<?>[] IGNORED = new Class<?>[]{byte[].class, char[].class, DataSource.class,
      DOMSource.class, File.class, InputStream.class, OutputStream.class, JAXBElement.class, MultivaluedMap.class,
      Reader.class, Writer.class, SAXSource.class, StreamingOutput.class, StreamSource.class, String.class};

   private static boolean isIgnored(Class<?> type)
   {
      for (Class<?> c : IGNORED)
      {
         if (c.isAssignableFrom(type))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      //      return Object.class.isAssignableFrom(type);
      return !isIgnored(type);
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      try
      {
         JsonParserImpl parser = new JsonParserImpl();
         JsonDefaultHandler handler = new JsonDefaultHandler();

         parser.parse(entityStream, handler);
         JsonValue jsonValue = handler.getJsonObject();

         if (JsonValue.class.isAssignableFrom(type))
         {
            // If requested object is JsonValue then stop processing here.
            return jsonValue;
         }

         Types jtype = JsonUtils.getType(type);
         if (jtype == Types.ARRAY_BOOLEAN || jtype == Types.ARRAY_BYTE || jtype == Types.ARRAY_SHORT
            || jtype == Types.ARRAY_INT || jtype == Types.ARRAY_LONG || jtype == Types.ARRAY_FLOAT
            || jtype == Types.ARRAY_DOUBLE || jtype == Types.ARRAY_CHAR || jtype == Types.ARRAY_STRING
            || jtype == Types.ARRAY_OBJECT)
         {
            return ObjectBuilder.createArray(type, jsonValue);
         }
         if (jtype == Types.COLLECTION)
         {
            Class c = type;
            return ObjectBuilder.createCollection(c, genericType, jsonValue);
         }
         if (jtype == Types.MAP)
         {
            Class c = type;
            return ObjectBuilder.createObject(c, genericType, jsonValue);
         }
         return ObjectBuilder.createObject(type, jsonValue);

      }
      catch (JsonException e)
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
      //      return Object.class.isAssignableFrom(type);
      return !isIgnored(type);
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      try
      {
         JsonValue jsonValue = null;
         if (t instanceof JsonValue)
         {
            // Don't do any transformation if object is prepared JsonValue.
            jsonValue = (JsonValue)t;
         }
         else
         {
            JsonGeneratorImpl generator = new JsonGeneratorImpl();
            Types jtype = JsonUtils.getType(type);
            if (jtype == Types.ARRAY_BOOLEAN || jtype == Types.ARRAY_BYTE || jtype == Types.ARRAY_SHORT
               || jtype == Types.ARRAY_INT || jtype == Types.ARRAY_LONG || jtype == Types.ARRAY_FLOAT
               || jtype == Types.ARRAY_DOUBLE || jtype == Types.ARRAY_CHAR || jtype == Types.ARRAY_STRING
               || jtype == Types.ARRAY_OBJECT)
            {
               jsonValue = generator.createJsonArray(t);
            }
            else if (jtype == Types.COLLECTION)
            {
               jsonValue = generator.createJsonArray((Collection<?>)t);
            }
            else if (jtype == Types.MAP)
            {
               jsonValue = generator.createJsonObjectFromMap((Map<String, ?>)t);
            }
            else
            {
               jsonValue = generator.createJsonObject(t);
            }
         }
         JsonWriterImpl jsonWriter = new JsonWriterImpl(entityStream);
         jsonValue.writeTo(jsonWriter);
         jsonWriter.flush();
      }
      catch (JsonException e)
      {
         throw new IOException("Can't write to output stream " + e);
      }

   }

}
