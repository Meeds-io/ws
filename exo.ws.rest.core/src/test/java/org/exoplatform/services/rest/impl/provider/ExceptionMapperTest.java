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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExceptionMapperTest extends BaseTest
{

   public static class ExceptionMapper1 implements ExceptionMapper<IllegalArgumentException>
   {

      public Response toResponse(IllegalArgumentException exception)
      {
         return Response.status(200).entity("IllegalArgumentException").build();
      }

   }

   public static class ExceptionMapper2 implements ExceptionMapper<RuntimeException>
   {

      public Response toResponse(RuntimeException exception)
      {
         return Response.status(200).entity("RuntimeException").build();
      }

   }

   public static class ExceptionMapper3 implements ExceptionMapper<WebApplicationException>
   {

      public Response toResponse(WebApplicationException exception)
      {
         return Response.status(200).entity("WebApplicationException").build();
      }

   }

   public static class ExceptionMapper4 implements ExceptionMapper<MockException>
   {

      public Response toResponse(MockException exception)
      {
         return Response.status(200).entity("MockException").build();
      }

   }

   public static class MockException extends Exception
   {

      private static final long serialVersionUID = 5029726201933185270L;

   }

   public void setUp() throws Exception
   {
      super.setUp();
      providers.addExceptionMapper(ExceptionMapper1.class);
      providers.addExceptionMapper(ExceptionMapper2.class);
      providers.addExceptionMapper(ExceptionMapper3.class);
      providers.addExceptionMapper(ExceptionMapper4.class);
   }

   @Path("a")
   public static class Resource1
   {

      @GET
      @Path("1")
      public void m1()
      {
         throw new IllegalArgumentException();
      }

      @GET
      @Path("2")
      public void m2()
      {
         throw new RuntimeException();
      }

      @GET
      @Path("3")
      public void m3()
      {
         throw new WebApplicationException(Response.status(400).build());
      }

      @GET
      @Path("4")
      public void m4()
      {
         throw new WebApplicationException(Response.status(500).entity(
            "this exception must not be hidden by any ExceptionMapper").build());
      }

      @GET
      @Path("5")
      public void m5() throws MockException
      {
         throw new MockException();
      }

   }

   public void testExceptionMappers() throws Exception
   {
      registry(Resource1.class);

      ContainerResponse resp = launcher.service("GET", "/a/1", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("IllegalArgumentException", resp.getEntity());

      resp = launcher.service("GET", "/a/2", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("RuntimeException", resp.getEntity());

      resp = launcher.service("GET", "/a/3", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("WebApplicationException", resp.getEntity());

      resp = launcher.service("GET", "/a/4", "", null, null, null);
      // WebApplicationException with entity - must not be overridden
      assertEquals(500, resp.getStatus());
      assertEquals("this exception must not be hidden by any ExceptionMapper", resp.getEntity());

      resp = launcher.service("GET", "/a/5", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("MockException", resp.getEntity());

      unregistry(Resource1.class);
   }

}
