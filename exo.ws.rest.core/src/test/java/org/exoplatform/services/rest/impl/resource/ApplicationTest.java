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

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationTest extends AbstractResourceTest
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
      binder.addApplication(new Application1());
      assertEquals(4, binder.getSize());
      assertEquals(1, providers.getRequestFilters(null).size());
      assertEquals(1, providers.getResponseFilters(null).size());
      assertEquals(1, providers.getMethodInvokerFilters(null).size());
      assertNotNull(providers.getExceptionMapper(RuntimeException.class));
      assertNotNull(providers.getExceptionMapper(IllegalStateException.class));
   }

   private static boolean requestFilter = false;
   private static boolean responseFilter = false;
   private static boolean invFilter = false;
   
   public void testAsResources() throws Exception
   {
      binder.addApplication(new Application1());
      // per-request
      ContainerResponse resp = service("GET", "/a", "", null, null);
      assertEquals(200, resp.getStatus());
      String hash10 = (String)resp.getEntity();
      resp = service("GET", "/a", "", null, null);
      String hash11 = (String)resp.getEntity();
      // new instance of resource for each request
      assertFalse(hash10.equals(hash11));

      // singleton
      resp = service("GET", "/c", "", null, null);
      assertEquals(200, resp.getStatus());
      String hash20 = (String)resp.getEntity();
      resp = service("GET", "/c", "", null, null);
      String hash21 = (String)resp.getEntity();
      // singleton resource
      assertTrue(hash20.equals(hash21));

      // check per-request ExceptionMapper as example of provider
      resp = service("GET", "/b", "", null, null);
      // should be 200 status instead 500 if ExceptionMapper works correct
      assertEquals(200, resp.getStatus());
      assertEquals("test Runtime Exception", resp.getEntity());

      // check singleton ExceptionMapper as example of provider
      resp = service("GET", "/d", "", null, null);
      // should be 200 status instead 500 if ExceptionMapper works correct
      assertEquals(200, resp.getStatus());
      assertEquals("test Illegal State Exception", resp.getEntity());
      
      // check are filters were visited
      assertTrue(requestFilter);
      assertTrue(responseFilter);
      assertTrue(invFilter);
   }

}
