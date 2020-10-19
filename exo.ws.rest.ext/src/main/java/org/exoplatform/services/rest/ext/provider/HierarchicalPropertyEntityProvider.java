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
package org.exoplatform.services.rest.ext.provider;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Converts stream to {@link HierarchicalProperty} and serializes
 * {@link HierarchicalProperty} in given stream.
 * 
 * @author <a href="dkatayev@gmail.com">Dmytro Katayev</a>
 * @version $Id: HierarchicalPropertyEntityProvider.java
 */
@Provider
public class HierarchicalPropertyEntityProvider implements EntityProvider<HierarchicalProperty>
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.ext.HierarchicalPropertyEntityProvider");

   /**
    * {@inheritDoc}
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type == HierarchicalProperty.class;
   }

   /**
    * {@inheritDoc}
    */
   public HierarchicalProperty readFrom(Class<HierarchicalProperty> t, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
      WebApplicationException
   {
      HierarchicalProperty rootProperty = null;
      LinkedList<HierarchicalProperty> curProperty = new LinkedList<HierarchicalProperty>();

      try
      {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLEventReader reader = factory.createXMLEventReader(entityStream);
         XMLEventReader fReader = factory.createFilteredReader(reader, new EventFilter()
         {

            public boolean accept(XMLEvent event)
            {
               return !(event.isCharacters() && ((Characters)event).isWhiteSpace());
            }
         });
         while (fReader.hasNext())
         {
            XMLEvent event = fReader.nextEvent();
            switch (event.getEventType())
            {
               case XMLEvent.START_ELEMENT :
                  StartElement element = event.asStartElement();
                  QName name = element.getName();
                  HierarchicalProperty prop = new HierarchicalProperty(name);
                  if (!curProperty.isEmpty())
                     curProperty.getLast().addChild(prop);
                  else
                     rootProperty = prop;
                  curProperty.addLast(prop);
                  break;
               case XMLEvent.END_ELEMENT :
                  curProperty.removeLast();
                  break;
               case XMLEvent.CHARACTERS :
                  String chars = event.asCharacters().getData();
                  curProperty.getLast().setValue(chars);
                  break;
               default :
                  break;
            }
         }

         return rootProperty;
      }
      catch (FactoryConfigurationError e)
      {
         throw new IOException(e.getMessage(), e);
      }
      catch (XMLStreamException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("An XMLStreamException occurs", e);
         return null;
      }
      catch (RuntimeException re)
      {
         String reName = re.getClass().getName();
         if (reName.equals("com.ctc.wstx.exc.WstxLazyException"))
         {
            if (LOG.isDebugEnabled())
               LOG.error(re.getMessage(), re);
            return null;
         }
         else
         {
            throw re;
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public long getSize(HierarchicalProperty t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType)
   {

      return -1;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return HierarchicalProperty.class.isAssignableFrom(type);
   }

   /**
    * {@inheritDoc}
    */
   public void writeTo(HierarchicalProperty t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
      WebApplicationException
   {

      Document e = (Document)t;
      try
      {
         TransformerFactory.newInstance().newTransformer().transform(new DOMSource(e), new StreamResult(entityStream));
      }
      catch (TransformerException tre)
      {
         throw new IOException("Can't write to output stream " + tre, tre);
      }
   }

}
