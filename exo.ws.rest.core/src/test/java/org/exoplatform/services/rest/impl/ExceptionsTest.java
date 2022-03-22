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
package org.exoplatform.services.rest.impl;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS. <br>
 * Date: 24 Dec 2009
 *
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: ExceptionsTest
 */
public class ExceptionsTest extends BaseTest
{

   @Path("a")
   public static class Resource1
   {

      @GET
      @Path("0")
      public void m0() throws WebApplicationException
      {
         Exception e = new Exception(errorMessage);
         throw new WebApplicationException(e, 500);
      }

      @GET
      @Path("1")
      public void m1() throws WebApplicationException
      {
         Response response = Response.status(500).entity(errorMessage).type("text/plain").build();
         throw new WebApplicationException(new Exception(), response);
      }

      @GET
      @Path("2")
      public Response m2() throws WebApplicationException
      {
         throw new WebApplicationException(500);
      }

      @GET
      @Path("3")
      public void m3() throws Exception
      {
         throw new RuntimeException(errorMessage);
      }

      @GET
      @Path("4")
      public Response m4() throws Exception
      {
         return Response.status(500).entity(errorMessage).type("text/plain").build();
      }

      @GET
      @Path("5")
      public Response m5() throws Exception
      {
         return Response.status(200).entity("My normal result").type("text/plain").build();
      }
   }

   private static String errorMessage = "test-error-message";

   private Resource1 resource;

   public void setUp() throws Exception
   {
      super.setUp();
      resource = new Resource1();
      registry(resource);
   }

   public void tearDown() throws Exception
   {
      unregistry(resource);
      super.tearDown();
   }

   public void testErrorResponse() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", "/a/4", "", null, null, writer, null);
      assertEquals(500, response.getStatus());
      String entity = new String(writer.getBody());
      assertEquals(errorMessage, entity);
   }

   public void testUncheckedException() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", "/a/3", "", null, null, writer, null);
      assertEquals(500, response.getStatus());
      String entity = new String(writer.getBody());
      assertEquals(errorMessage, entity);
   }

   public void testWebApplicationExceptionWithCause() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", "/a/0", "", null, null, writer, null);
      assertEquals(500, response.getStatus());
      String entity = new String(writer.getBody());
      assertEquals(new Exception(errorMessage).toString(), entity);
   }

   public void testWebApplicationExceptionWithoutCause() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", "/a/2", "", null, null, writer, null);
      assertEquals(500, response.getStatus());
      assertNull(response.getEntity());
   }

   public void testWebApplicationExceptionWithResponse() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", "/a/1", "", null, null, writer, null);
      assertEquals(500, response.getStatus());
      String entity = new String(writer.getBody());
      assertEquals(errorMessage, entity);
   }

   public void testErrorOnRequestLifeCycleEnd() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      try
      {
         FailingComponentRequestLifecycle.FAIL.set(true);
         launcher.service("GET", "/a/5", "", null, null, writer, null);
      }
      finally
      {
         FailingComponentRequestLifecycle.FAIL.set(false);
      }
   }

   public static class FailingComponentRequestLifecycle implements ComponentRequestLifecycle
   {
      public static AtomicBoolean FAIL = new AtomicBoolean();

      /**
       * {@inheritDoc}
       */
      public void startRequest(ExoContainer container)
      {
      }

      /**
       * {@inheritDoc}
       */
      public void endRequest(ExoContainer container)
      {
         if (FAIL.get())
            throw new RuntimeException(errorMessage);
      }
   }
}
