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

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SourceEntityProviderTest extends BaseTest
{

   private byte[] data;

   private MediaType mediaType;

   public void setUp() throws Exception
   {
      super.setUp();
      mediaType = new MediaType("application", "xml");
      data =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><message>to be or not to be</message></root>"
            .getBytes("UTF-8");
   }

   @SuppressWarnings("unchecked")
   public void testReadStreamSourceEntityProvider() throws Exception
   {
      MessageBodyReader reader = providers.getMessageBodyReader(StreamSource.class, null, null, mediaType);
      assertNotNull(reader);
      assertTrue(reader.isReadable(StreamSource.class, StreamSource.class, null, mediaType));
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle(HttpHeaders.CONTENT_LENGTH, "" + data.length);
      StreamSource src =
         (StreamSource)reader.readFrom(StreamSource.class, StreamSource.class, null, mediaType, h,
            new ByteArrayInputStream(data));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      TransformerFactory.newInstance().newTransformer().transform(src, new StreamResult(out));
      write(out);
   }

   @SuppressWarnings("unchecked")
   public void testWriteStreamSourceEntityProvider() throws Exception
   {
      StreamSource src = new StreamSource(new ByteArrayInputStream(data));
      MessageBodyWriter writer = providers.getMessageBodyWriter(StreamSource.class, null, null, mediaType);
      assertNotNull(writer);
      assertTrue(writer.isWriteable(StreamSource.class, StreamSource.class, null, mediaType));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      writer.writeTo(src, StreamSource.class, StreamSource.class, null, mediaType, null, out);
      write(out);
   }

   @SuppressWarnings("unchecked")
   public void testReadSAXSourceEntityProvider() throws Exception
   {
      MessageBodyReader reader = providers.getMessageBodyReader(SAXSource.class, null, null, mediaType);
      assertNotNull(reader);
      assertTrue(reader.isReadable(SAXSource.class, SAXSource.class, null, mediaType));
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle(HttpHeaders.CONTENT_LENGTH, "" + data.length);
      SAXSource src =
         (SAXSource)reader.readFrom(SAXSource.class, SAXSource.class, null, mediaType, h,
            new ByteArrayInputStream(data));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      TransformerFactory.newInstance().newTransformer().transform(src, new StreamResult(out));
      write(out);
   }

   @SuppressWarnings("unchecked")
   public void testWriteSAXSourceEntityProvider() throws Exception
   {
      SAXSource src = new SAXSource(new InputSource(new ByteArrayInputStream(data)));
      MessageBodyWriter writer = providers.getMessageBodyWriter(SAXSource.class, null, null, mediaType);
      assertNotNull(writer);
      assertTrue(writer.isWriteable(SAXSource.class, SAXSource.class, null, mediaType));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      writer.writeTo(src, SAXSource.class, SAXSource.class, null, mediaType, null, out);
      write(out);
   }

   @SuppressWarnings("unchecked")
   public void testReadDOMSourceEntityProvider() throws Exception
   {
      MessageBodyReader reader = providers.getMessageBodyReader(DOMSource.class, null, null, mediaType);
      assertNotNull(reader);
      assertTrue(reader.isReadable(DOMSource.class, DOMSource.class, null, mediaType));
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle(HttpHeaders.CONTENT_LENGTH, "" + data.length);
      DOMSource src =
         (DOMSource)reader.readFrom(DOMSource.class, DOMSource.class, null, mediaType, h,
            new ByteArrayInputStream(data));
      Node root = src.getNode().getFirstChild();
      assertEquals("root", root.getNodeName());
      assertEquals("message", root.getFirstChild().getNodeName());
      assertEquals("to be or not to be", root.getFirstChild().getTextContent());
   }

   @SuppressWarnings("unchecked")
   public void testWriteDOMSourceEntityProvider() throws Exception
   {
      Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data));
      MessageBodyWriter writer = providers.getMessageBodyWriter(DOMSource.class, null, null, mediaType);
      assertNotNull(writer);
      assertTrue(writer.isWriteable(DOMSource.class, DOMSource.class, null, mediaType));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      writer.writeTo(new DOMSource(d), DOMSource.class, DOMSource.class, null, mediaType, null, out);
      write(out);
   }

   private static void write(ByteArrayOutputStream out) throws Exception
   {
      System.out.println(out.toString("UTF-8"));
   }
}
