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
package org.exoplatform.services.rest.impl.resource;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.impl.ApplicationProviders;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.provider.StringEntityProvider;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationTest extends BaseTest
{

   public static class Application1 extends javax.ws.rs.core.Application
   {

      private final Set<Class<?>> perreq = new HashSet<Class<?>>();

      private final Set<Object> singletons = new HashSet<Object>();

      public Application1()
      {
         perreq.add(Resource1.class);
         perreq.add(Resource2.class);
         perreq.add(ExceptionMapper1.class);
         perreq.add(MethodInvokerFilter1.class);
         perreq.add(RequestFilter1.class);

         singletons.add(new Resource3());
         singletons.add(new Resource4());
         singletons.add(new ExceptionMapper2());
         singletons.add(new ResponseFilter1());
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return perreq;
      }

      public Set<Object> getSingletons()
      {
         return singletons;
      }

   }

   // will be per-request resource
   @Path("a")
   public static class Resource1
   {

      @GET
      public String m0()
      {
         return hashCode() + "";
      }

   }

   // will be per-request resource
   @Path("b")
   public static class Resource2
   {

      @GET
      public void m0()
      {
         throw new RuntimeException("test Runtime Exception");
      }

   }

   // will be singleton resource
   @Path("c")
   public static class Resource3
   {

      @GET
      public String m0()
      {
         return hashCode() + "";
      }

   }

   // will be per-request resource
   @Path("d")
   public static class Resource4
   {

      @GET
      public void m0()
      {
         throw new IllegalStateException("test Illegal State Exception");
      }

   }

   @Provider
   public static class ExceptionMapper1 implements ExceptionMapper<RuntimeException>
   {

      public Response toResponse(RuntimeException exception)
      {
         return Response.status(200).entity(exception.getMessage()).build();
      }

   }

   @Provider
   public static class ExceptionMapper2 implements ExceptionMapper<IllegalStateException>
   {

      public Response toResponse(IllegalStateException exception)
      {
         return Response.status(200).entity(exception.getMessage()).build();
      }

   }

   @Filter
   public static class MethodInvokerFilter1 implements MethodInvokerFilter
   {

      public void accept(GenericMethodResource genericMethodResource)
      {
         invFilter = true;
      }

   }

   @Filter
   public static class RequestFilter1 implements RequestFilter
   {

      public void doFilter(GenericContainerRequest request)
      {
         requestFilter = true;
      }

   }

   @Filter
   public static class ResponseFilter1 implements ResponseFilter
   {

      public void doFilter(GenericContainerResponse response)
      {
         responseFilter = true;
      }

   }

   public void testRegistry()
   {
      Application app = new Application1();
      //binder.addApplication(app);
      applicationRegistry.addApplication(app);
      assertEquals(4, binder.getSize());
      ApplicationProviders appProviders = providersRegistry.getProviders(app.getClass().getName());
      assertEquals(1, appProviders.getRequestFilters(null).size());
      assertEquals(1, appProviders.getResponseFilters(null).size());
      assertEquals(1, appProviders.getMethodInvokerFilters(null).size());
      assertNotNull(appProviders.getExceptionMapper(RuntimeException.class));
      assertNotNull(appProviders.getExceptionMapper(IllegalStateException.class));
   }

   private static boolean requestFilter = false;

   private static boolean responseFilter = false;

   private static boolean invFilter = false;

   public void testAsResources() throws Exception
   {
      //binder.addApplication(new Application1());
      applicationRegistry.addApplication(new Application1());
      // per-request
      ContainerResponse resp = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(200, resp.getStatus());
      String hash10 = (String)resp.getEntity();
      resp = launcher.service("GET", "/a", "", null, null, null);
      String hash11 = (String)resp.getEntity();
      // new instance of resource for each request
      assertFalse(hash10.equals(hash11));

      // singleton
      resp = launcher.service("GET", "/c", "", null, null, null);
      assertEquals(200, resp.getStatus());
      String hash20 = (String)resp.getEntity();
      resp = launcher.service("GET", "/c", "", null, null, null);
      String hash21 = (String)resp.getEntity();
      // singleton resource
      assertTrue(hash20.equals(hash21));

      // check per-request ExceptionMapper as example of provider
      resp = launcher.service("GET", "/b", "", null, null, null);
      // should be 200 status instead 500 if ExceptionMapper works correct
      assertEquals(200, resp.getStatus());
      assertEquals("test Runtime Exception", resp.getEntity());

      // check singleton ExceptionMapper as example of provider
      resp = launcher.service("GET", "/d", "", null, null, null);
      // should be 200 status instead 500 if ExceptionMapper works correct
      assertEquals(200, resp.getStatus());
      assertEquals("test Illegal State Exception", resp.getEntity());

      // check are filters were visited
      assertTrue(requestFilter);
      assertTrue(responseFilter);
      assertTrue(invFilter);
   }

   public static class Application2 extends Application
   {

      private final Set<Class<?>> perreq = new HashSet<Class<?>>();

      private final Set<Object> singletons = new HashSet<Object>();

      public Application2()
      {
         perreq.add(Resource5.class);
         singletons.add(new StringEntityProvider1());
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return perreq;
      }

      @Override
      public Set<Object> getSingletons()
      {
         return singletons;
      }

   }

   @Path("abc")
   public static class Resource5
   {
      @POST
      public String m0(String m)
      {
         assertEquals(message.toUpperCase(), m);
         return m;
      }
   }

   @Provider
   public static class StringEntityProvider1 extends StringEntityProvider
   {
      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
      {
         return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream)
            .toUpperCase();
      }

      @Override
      public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException
      {
         super.writeTo(t.toLowerCase(), type, genericType, annotations, mediaType, httpHeaders, entityStream);
      }
   }

   private static final String message = "prOVIDers preFERence";

   public void testProvidersPreference() throws Exception
   {
      applicationRegistry.addApplication(new Application2());
      // If StringEntityProvider1 override default reader/writer for String
      // then string must be in upper case in service's method.
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = launcher.service("POST", "/abc", "", null, message.getBytes(), writer, null);
      assertEquals(200, resp.getStatus());
      // Must be returned in lower case.
      assertEquals(message.toLowerCase(), new String(writer.getBody()));
   }

}
