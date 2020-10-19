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
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderContextParameterInjectionTest extends BaseTest
{

   public static class MockEntity
   {
      String entity;
   }

   @Provider
   public static class EntityProviderChecker implements EntityProvider<MockEntity>
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private Request request;

      @Context
      private HttpHeaders httpHeaders;

      @Context
      private Providers providers;

      @Context
      private HttpServletRequest httpRequest;

      // EntityProvider can be used for reading/writing ONLY if all fields above
      // initialized

      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null;
      }

      public MockEntity readFrom(Class<MockEntity> type, Type genericType, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
         WebApplicationException
      {
         MockEntity me = new MockEntity();
         me.entity = IOHelper.readString(entityStream, IOHelper.DEFAULT_CHARSET_NAME);
         return me;
      }

      public long getSize(MockEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return 0;
      }

      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null;
      }

      public void writeTo(MockEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
         WebApplicationException
      {
         IOHelper.writeString(t.entity, entityStream, IOHelper.DEFAULT_CHARSET_NAME);
      }

   }

   @Provider
   public static class ExceptionMapperChecker implements ExceptionMapper<RuntimeException>
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private Request request;

      @Context
      private HttpHeaders httpHeaders;

      @Context
      private Providers providers;

      @Context
      private HttpServletRequest httpRequest;

      public Response toResponse(RuntimeException exception)
      {
         if (uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null)
            return Response.status(200).build();
         else
            return Response.status(500).build();
      }

   }

   @Provider
   @Produces("text/plain")
   public static class ContextResolverChecker implements ContextResolver<String>
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private Request request;

      @Context
      private HttpHeaders httpHeaders;

      @Context
      private Providers providers;

      @Context
      private HttpServletRequest httpRequest;

      public String getContext(Class<?> type)
      {
         if (uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null)
            return "to be to not to be";
         return null;
      }

   }

   public void setUp() throws Exception
   {
      super.setUp();
      providers.addMessageBodyReader(EntityProviderChecker.class);
      providers.addMessageBodyWriter(EntityProviderChecker.class);
      providers.addExceptionMapper(ExceptionMapperChecker.class);
      providers.addContextResolver(ContextResolverChecker.class);
   }

   public void tearDown() throws Exception
   {
      super.tearDown();
   }

   @Path("a")
   public static class Resource1
   {

      @Context
      private Providers providers;

      @POST
      @Path("1")
      public MockEntity m0(MockEntity me)
      {
         assertNotNull(me);
         assertEquals("to be or not to be", me.entity);
         me.entity = "to be";
         return me;
      }

      @GET
      @Path("2")
      public void m1()
      {
         throw new RuntimeException();
      }

      @GET
      @Path("3")
      public String m2()
      {
         ContextResolver<String> r = providers.getContextResolver(String.class, new MediaType("text", "plain"));
         return r.getContext(String.class);
      }
   }

   public void test0() throws Exception
   {
      registry(Resource1.class);

      EnvironmentContext envctx = new EnvironmentContext();

      HttpServletRequest httpRequest =
         new MockHttpServletRequest("/a/1", new ByteArrayInputStream("to be or not to be".getBytes()), 18, "POST", null);
      envctx.put(HttpServletRequest.class, httpRequest);

      ContainerResponse resp = launcher.service("POST", "/a/1", "", null, "to be or not to be".getBytes(), envctx);
      assertEquals("to be", ((MockEntity)resp.getEntity()).entity);

      httpRequest = new MockHttpServletRequest("/a/2", null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      resp = launcher.service("GET", "/a/2", "", null, null, envctx);
      assertEquals(200, resp.getStatus());

      httpRequest = new MockHttpServletRequest("/a/3", null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      resp = launcher.service("GET", "/a/3", "", null, null, envctx);
      assertEquals(200, resp.getStatus());
      assertEquals("to be to not to be", resp.getEntity());

      unregistry(Resource1.class);
   }

}
