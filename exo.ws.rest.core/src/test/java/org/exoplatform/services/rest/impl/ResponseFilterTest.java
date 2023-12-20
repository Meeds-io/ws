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

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResponseFilterTest extends BaseTest
{

   @Filter
   public static class ResponseFilter1 implements ResponseFilter
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private HttpHeaders httpHeaders;

      private Providers providers;

      private HttpServletRequest httpRequest;

      private ResourceBinder binder; // exo container component

      public ResponseFilter1(@Context Providers providers, @Context HttpServletRequest httpRequest,
         ResourceBinder binder)
      {
         this.providers = providers;
         this.httpRequest = httpRequest;
         this.binder = binder;
      }

      public void doFilter(GenericContainerResponse response)
      {
         if (uriInfo != null && httpHeaders != null && providers != null && httpRequest != null && binder != null)
            response.setResponse(Response.status(200).entity("to be or not to be").type("text/plain").build());
      }

   }

   @Path("a/b/c/{x:.*}")
   @Filter
   public static class ResponseFilter2 implements ResponseFilter
   {

      public void doFilter(GenericContainerResponse response)
      {
         response.setResponse(Response.status(response.getStatus()).entity(response.getEntity()).type(
            "application/json").build());
      }

   }

   @Path("a")
   public static class Resource1
   {

      @POST
      public void m0()
      {
      }

      @POST
      @Path("b/c/d/e")
      @Produces("text/plain")
      public String m1()
      {
         // text/plain will be overridden in response filter
         return "{\"name\":\"andrew\", \"password\":\"hello\"}";
      }

   }

   //------------------------------------

   public void testFilter() throws Exception
   {
      Resource1 r = new Resource1();
      registry(r);

      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest("/a", null, 0, "POST", null);
      envctx.put(HttpServletRequest.class, httpRequest);

      ContainerResponse resp = launcher.service("POST", "/a", "", null, null, envctx);
      assertEquals(204, resp.getStatus());

      // should not be any changes after add this
      providers.addResponseFilter(new ResponseFilter2());
      resp = launcher.service("POST", "/a", "", null, null, envctx);
      assertEquals(204, resp.getStatus());

      // add response filter and try again
      providers.addResponseFilter(ResponseFilter1.class);

      resp = launcher.service("POST", "/a", "", null, null, envctx);
      assertEquals(200, resp.getStatus());
      assertEquals("text/plain", resp.getContentType().toString());
      assertEquals("to be or not to be", resp.getEntity());

      unregistry(r);
   }

   public void testFilter2() throws Exception
   {
      Resource1 r = new Resource1();
      registry(r);
      ContainerResponse resp = launcher.service("POST", "/a/b/c/d/e", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("text/plain", resp.getContentType().toString());
      assertEquals("{\"name\":\"andrew\", \"password\":\"hello\"}", resp.getEntity());

      // add response filter and try again
      providers.addResponseFilter(new ResponseFilter2());

      resp = launcher.service("POST", "/a/b/c/d/e", "", null, null, null);
      assertEquals(200, resp.getStatus());
      assertEquals("application/json", resp.getContentType().toString());
      assertEquals("{\"name\":\"andrew\", \"password\":\"hello\"}", resp.getEntity());

      unregistry(r);
   }

}
