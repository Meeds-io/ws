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

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.provider.EntityProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML, MediaTypeHelper.WADL})
public class JAXBObjectEntityProvider implements EntityProvider<Object>
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.JAXBObjectEntityProvider");

   /**
    * @see Providers
    */
   @Context
   Providers providers;

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.getAnnotation(XmlRootElement.class) != null;
   }

   /**
    * {@inheritDoc}
    */
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException
   {
      try
      {
         final JAXBContext jaxbctx = getJAXBContext(type, mediaType);

         return SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Object>()
         {
            public Object run() throws Exception
            {
               return jaxbctx.createUnmarshaller().unmarshal(entityStream);
            }
         });
      }
      catch (PrivilegedActionException pae)
      {
         Throwable cause = pae.getCause();
         if (cause instanceof UnmarshalException)
         {
            // if can't read from stream (e.g. steam is empty)
            if (LOG.isDebugEnabled())
            {
               LOG.debug(cause.getLocalizedMessage(), cause);
            }
            return null;
         }
         else if (cause instanceof JAXBException)
         {
            throw new IOException("Can't read from input stream " + cause);
         }
         else if (cause instanceof IllegalArgumentException)
         {
            throw (IllegalArgumentException)cause;
         }
         else if (cause instanceof RuntimeException)
         {
            throw (RuntimeException)cause;
         }
         else
         {
            throw new RuntimeException(cause);
         }
      }
      catch (JAXBException e)
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
      return type.getAnnotation(XmlRootElement.class) != null;
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      try
      {
         JAXBContext jaxbctx = getJAXBContext(type, mediaType);
         Marshaller m = jaxbctx.createMarshaller();
         // Must respect application specified character set.
         String charset = mediaType.getParameters().get("charset");
         if (charset != null)
            m.setProperty(Marshaller.JAXB_ENCODING, charset);

         m.marshal(t, entityStream);
      }
      catch (JAXBException e)
      {
         throw new IOException("Can't write to output stream " + e);
      }
   }

   /**
    * @param type type
    * @param mediaType media type
    * @return JAXBContext JAXBContext
    * @throws JAXBException if JAXBContext creation failed
    */
   protected JAXBContext getJAXBContext(Class<?> type, MediaType mediaType) throws JAXBException
   {
      ContextResolver<JAXBContextResolver> resolver =
         providers.getContextResolver(JAXBContextResolver.class, mediaType);
      if (resolver == null)
         throw new RuntimeException("Not found any JAXBContextResolver for media type " + mediaType);
      JAXBContextResolver jaxbres = resolver.getContext(type);
      return jaxbres.getJAXBContext(type);
   }

}
