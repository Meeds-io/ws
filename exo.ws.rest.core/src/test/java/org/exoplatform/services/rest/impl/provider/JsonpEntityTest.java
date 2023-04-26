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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:nfilotto@exoplatform.com">Nicolas Filotto</a>
 * @version $Id$
 *
 */
public class JsonpEntityTest extends BaseTest
{
   private static final String JS_FILE_CONTENT = "console.log('Test JS File')";

   @Path("/")
   public static class ResourceBook
   {
      @GET
      @Produces("application/javascript")
      public Book m1()
      {
         Book book = new Book();
         book.setTitle("Hamlet");
         book.setAuthor("William Shakespeare");
         book.setSendByPost(true);
         return book;
      }
   }

   @Path("/")
   public static class ResourceBookArray
   {
      @GET
      @Produces("text/javascript")
      public Book[] m1()
      {
         Book book1 = new Book();
         book1.setTitle("Hamlet");
         book1.setAuthor("William Shakespeare");
         book1.setSendByPost(true);
         Book book2 = new Book();
         book2.setTitle("Collected Stories");
         book2.setAuthor("Gabriel Garcia Marquez");
         book2.setSendByPost(true);
         return new Book[]{book1, book2};
      }
   }

   @Path("/")
   public static class TextJavascript {

     @GET
     @Produces("text/javascript")
     public String m1() {
       return JS_FILE_CONTENT;
     }

   }

   @Path("/")
   public static class ResourceBookCollection
   {
      @GET
      @Produces("application/json-p")
      public List<Book> m1()
      {
         Book book1 = new Book();
         book1.setTitle("Hamlet");
         book1.setAuthor("William Shakespeare");
         book1.setSendByPost(true);
         Book book2 = new Book();
         book2.setTitle("Collected Stories");
         book2.setAuthor("Gabriel Garcia Marquez");
         book2.setSendByPost(true);
         return Arrays.asList(book1, book2);
      }
   }

   @Path("/")
   public static class ResourceBookMap
   {
      @GET
      @Produces("text/json-p")
      public Map<String, Book> m1()
      {
         Book book1 = new Book();
         book1.setTitle("Hamlet");
         book1.setAuthor("William Shakespeare");
         book1.setSendByPost(true);
         Book book2 = new Book();
         book2.setTitle("Collected Stories");
         book2.setAuthor("Gabriel Garcia Marquez");
         book2.setSendByPost(true);
         Map<String, Book> m = new HashMap<String, Book>();
         m.put("12345", book1);
         m.put("54321", book2);
         return m;
      }
   }

   @Path("/")
   public static class ResourceString
   {
      @GET
      @Produces("application/javascript")
      public String m1()
      {
         return jsonBook;
      }
   }

   private static String jsonBook = "{\"title\":\"Hamlet\", \"author\":\"William Shakespeare\", \"sendByPost\":true}";

   public void testJsonReturnBean() throws Exception
   {
      ResourceBook r2 = new ResourceBook();
      registry(r2);
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("accept", "application/javascript");
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse response;
      try
      {
         response = launcher.service("GET", "/", "", h, null, writer, null);
         fail("An IOException is expected as the parameter jsonp has not been set");
      }
      catch (Exception e)
      {
         // expected
      }
      try
      {
         response = launcher.service("GET", "/?param=foo", "", h, null, writer, null);
         fail("An IOException is expected as the parameter jsonp has not been set");
      }
      catch (Exception e)
      {
         // expected
      }
      response = launcher.service("GET", "/?jsonp=functionName", "", h, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("application/javascript", response.getContentType().toString());

      Book book = (Book)response.getEntity();
      assertEquals("Hamlet", book.getTitle());
      assertEquals("William Shakespeare", book.getAuthor());
      assertTrue(book.isSendByPost());

      JsonDefaultHandler handler = testBody(writer, "functionName(");
      book = ObjectBuilder.createObject(Book.class, handler.getJsonObject());
      assertEquals("Hamlet", book.getTitle());
      assertEquals("William Shakespeare", book.getAuthor());
      assertTrue(book.isSendByPost());

      unregistry(r2);
   }

   private JsonDefaultHandler testBody(ByteArrayContainerResponseWriter writer, String start) throws JsonException
   {
      byte[] body = writer.getBody();
      byte[] startWith = start.getBytes();
      for (int i = 0; i < startWith.length; i++)
      {
         assertEquals(startWith[i], body[i]);
      }
      byte[] endWith = ");".getBytes();
      for (int i = 0; i < endWith.length; i++)
      {
         assertEquals(endWith[i], body[body.length - endWith.length + i]);
      }
      JsonParserImpl parser = new JsonParserImpl();
      JsonDefaultHandler handler = new JsonDefaultHandler();
      parser.parse(new ByteArrayInputStream(writer.getBody(), startWith.length, body.length - endWith.length
         - startWith.length), handler);
      return handler;
   }

   public void testJsonReturnBeanArray() throws Exception
   {
      ResourceBookArray r2 = new ResourceBookArray();
      registry(r2);
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("accept", "text/javascript");
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse response = launcher.service("GET", "/?jsonp=obj.functionName", "", h, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("text/javascript", response.getContentType().toString());

      Book[] book = (Book[])response.getEntity();
      assertEquals("Hamlet", book[0].getTitle());
      assertEquals("William Shakespeare", book[0].getAuthor());
      assertTrue(book[0].isSendByPost());
      assertEquals("Collected Stories", book[1].getTitle());
      assertEquals("Gabriel Garcia Marquez", book[1].getAuthor());
      assertTrue(book[1].isSendByPost());

      JsonDefaultHandler handler = testBody(writer, "obj.functionName(");
      book = (Book[])ObjectBuilder.createArray(new Book[0].getClass(), handler.getJsonObject());
      assertEquals("Hamlet", book[0].getTitle());
      assertEquals("William Shakespeare", book[0].getAuthor());
      assertTrue(book[0].isSendByPost());
      assertEquals("Collected Stories", book[1].getTitle());
      assertEquals("Gabriel Garcia Marquez", book[1].getAuthor());
      assertTrue(book[1].isSendByPost());

      unregistry(r2);
   }

   public void testTextJavascriptReturnText() throws Exception {
     TextJavascript r2 = new TextJavascript();
     registry(r2);
     MultivaluedMap<String, String> h = new MultivaluedMapImpl();
     h.putSingle("accept", "text/javascript");
     ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

     ContainerResponse response = launcher.service("GET", "/", "", h, null, writer, null);
     assertEquals(200, response.getStatus());
     assertEquals("text/javascript", response.getContentType().toString());

     String js = (String) response.getEntity();
     assertEquals(JS_FILE_CONTENT, js);

     unregistry(r2);
   }

   @SuppressWarnings({"unchecked", "serial"})
   public void testJsonReturnBeanCollection() throws Exception
   {
      ResourceBookCollection r2 = new ResourceBookCollection();
      registry(r2);
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("accept", "application/json-p");
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse response = launcher.service("GET", "/?jsonp=obj%5B%22function-name%22%5D", "", h, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("application/json-p", response.getContentType().toString());

      List<Book> book = (List<Book>)response.getEntity();
      assertEquals("Hamlet", book.get(0).getTitle());
      assertEquals("William Shakespeare", book.get(0).getAuthor());
      assertTrue(book.get(0).isSendByPost());
      assertEquals("Collected Stories", book.get(1).getTitle());
      assertEquals("Gabriel Garcia Marquez", book.get(1).getAuthor());
      assertTrue(book.get(1).isSendByPost());

      JsonDefaultHandler handler = testBody(writer, "obj[\"function-name\"](");
      ParameterizedType genericType = (ParameterizedType)new ArrayList<Book>()
      {
      }.getClass().getGenericSuperclass();
      book = ObjectBuilder.createCollection(List.class, genericType, handler.getJsonObject());
      assertEquals("Hamlet", book.get(0).getTitle());
      assertEquals("William Shakespeare", book.get(0).getAuthor());
      assertTrue(book.get(0).isSendByPost());
      assertEquals("Collected Stories", book.get(1).getTitle());
      assertEquals("Gabriel Garcia Marquez", book.get(1).getAuthor());
      assertTrue(book.get(1).isSendByPost());

      unregistry(r2);
   }

   @SuppressWarnings({"unchecked", "serial"})
   public void testJsonReturnBeanMap() throws Exception
   {
      ResourceBookMap r2 = new ResourceBookMap();
      registry(r2);
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("accept", "text/json-p");
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse response = launcher.service("GET", "/?jsonp=foo", "", h, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("text/json-p", response.getContentType().toString());

      Map<String, Book> book = (Map<String, Book>)response.getEntity();
      assertEquals("Hamlet", book.get("12345").getTitle());
      assertEquals("William Shakespeare", book.get("12345").getAuthor());
      assertTrue(book.get("12345").isSendByPost());
      assertEquals("Collected Stories", book.get("54321").getTitle());
      assertEquals("Gabriel Garcia Marquez", book.get("54321").getAuthor());
      assertTrue(book.get("54321").isSendByPost());

      ParameterizedType genericType = (ParameterizedType)new HashMap<String, Book>()
      {
      }.getClass().getGenericSuperclass();
      JsonDefaultHandler handler = testBody(writer, "foo(");
      book = ObjectBuilder.createObject(Map.class, genericType, handler.getJsonObject());
      assertEquals("Hamlet", book.get("12345").getTitle());
      assertEquals("William Shakespeare", book.get("12345").getAuthor());
      assertTrue(book.get("12345").isSendByPost());
      assertEquals("Collected Stories", book.get("54321").getTitle());
      assertEquals("Gabriel Garcia Marquez", book.get("54321").getAuthor());
      assertTrue(book.get("54321").isSendByPost());

      unregistry(r2);
   }

   public void testJsonReturnString() throws Exception
   {
      ResourceString r2 = new ResourceString();
      registry(r2);
      MultivaluedMap<String, String> h = new MultivaluedMapImpl();
      h.putSingle("accept", "application/javascript");
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse response = launcher.service("GET", "/?jsonp=callbackFunction", "", h, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("application/javascript", response.getContentType().toString());
      assertEquals(jsonBook, response.getEntity());
      assertEquals("callbackFunction(" + jsonBook + ");", new String(writer.getBody()));

      unregistry(r2);
   }

   public static class Book
   {

      private String title;
      private String author;
      private boolean sendByPost;

      public String getTitle()
      {
         return title;
      }

      public void setTitle(String title)
      {
         this.title = title;
      }

      public String getAuthor()
      {
         return author;
      }

      public void setAuthor(String author)
      {
         this.author = author;
      }

      public boolean isSendByPost()
      {
         return sendByPost;
      }

      public void setSendByPost(boolean sendByPost)
      {
         this.sendByPost = sendByPost;
      }
   }
}
