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
import org.exoplatform.services.rest.provider.EntityProvider;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
public class DOMSourceEntityProvider implements EntityProvider<DOMSource>
{

   /**
    * Document Builder Factory
    */
   private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
   /**
    * Transformer Factory
    */
   private static final TransformerFactory     TRF = TransformerFactory.newInstance();
   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.DOMSourceEntityProvider");

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType)
   {
      return type == DOMSource.class;
   }

   /**
    * {@inheritDoc}
    */
   public DOMSource readFrom(final Class<DOMSource> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
      final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException
   {
      try
      {
         DBF.setNamespaceAware(true);

         final Document d = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Document>()
         {
            public Document run() throws Exception
            {
               return DBF.newDocumentBuilder().parse(entityStream);
            }
         });

         return new DOMSource(d);
      }
      catch (PrivilegedActionException pae)
      {
         Throwable cause = pae.getCause();
         if (cause instanceof SAXParseException)
         {
            // if can't read from stream (e.g. steam is empty)
            if (LOG.isDebugEnabled())
            {
               LOG.debug(cause.getLocalizedMessage(), cause);
            }
            return null;
         }
         else if (cause instanceof SAXException)
         {
            throw new IOException("Can't read from input stream " + cause, cause);
         }
         else if (cause instanceof ParserConfigurationException)
         {
            throw new IOException("Can't read from input stream " + cause, cause);
         }
         else if (cause instanceof IOException)
         {
            throw (IOException)cause;
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
   }

   /**
    * {@inheritDoc}
    */
   public long getSize(final DOMSource t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType)
   {
      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType)
   {
      return DOMSource.class.isAssignableFrom(type);
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(final DOMSource t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
      final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException
   {
      final StreamResult out = new StreamResult(entityStream);
      try
      {
         TRF.newTransformer().transform(t, out);
      }
      catch (TransformerConfigurationException e)
      {
         throw new IOException("Can't write to output stream " + e, e);
      }
      catch (TransformerException e)
      {
         throw new IOException("Can't write to output stream " + e, e);
      }
      catch (TransformerFactoryConfigurationError e)
      {
         throw new IOException("Can't write to output stream " + e, e);
      }
   }
}
