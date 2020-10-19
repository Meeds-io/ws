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
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestFilterTest extends BaseTest
{

   @Filter
   public static class RequestFilter1 implements RequestFilter
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private HttpHeaders httpHeaders;

      private Providers providers;

      private HttpServletRequest httpRequest;

      private ResourceBinder binder; // exo container component

      public RequestFilter1(@Context Providers providers, @Context HttpServletRequest httpRequest, ResourceBinder binder)
      {
         this.providers = providers;
         this.httpRequest = httpRequest;
         this.binder = binder;
      }

      public void doFilter(GenericContainerRequest request)
      {
         if (uriInfo != null && httpHeaders != null && providers != null && httpRequest != null && binder != null)
            request.setMethod("POST");
      }

   }

   @Path("a/b/c/{x:.*}")
   @Filter
   public static class RequestFilter2 implements RequestFilter
   {

      public void doFilter(GenericContainerRequest request)
      {
         request.setMethod("DELETE");
      }

   }

   @Path("a")
   public static class Resource1
   {

      @POST
      public void m0()
      {
      }

      @DELETE
      @Path("b/c/d/e")
      public void m1()
      {

      }

      @PUT
      @Path("c/d/e")
      public void m2()
      {

      }
   }

   public void testWithoutFilter1() throws Exception
   {
      registry(Resource1.class);
      ContainerResponse resp = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(405, resp.getStatus());
      assertEquals(1, resp.getHttpHeaders().get("allow").size());
      assertTrue(resp.getHttpHeaders().get("allow").get(0).toString().contains("POST"));
      unregistry(Resource1.class);
   }

   public void testWithFilter2() throws Exception
   {
      registry(Resource1.class);

      // add filter that can change method
      providers.addRequestFilter(RequestFilter1.class);

      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest("/a", null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);

      // should get status 204
      ContainerResponse resp = launcher.service("GET", "/a", "", null, null, envctx);
      assertEquals(204, resp.getStatus());

      unregistry(Resource1.class);

   }

   public void testFilter2() throws Exception
   {
      registry(Resource1.class);
      ContainerResponse resp = launcher.service("GET", "/a/b/c/d/e", "", null, null, null);
      assertEquals(405, resp.getStatus());
      assertEquals(1, resp.getHttpHeaders().get("allow").size());
      assertTrue(resp.getHttpHeaders().get("allow").get(0).toString().contains("DELETE"));

      // add filter that can change method
      providers.addRequestFilter(new RequestFilter2());

      // not should get status 204
      resp = launcher.service("GET", "/a/b/c/d/e", "", null, null, null);
      assertEquals(204, resp.getStatus());

      unregistry(Resource1.class);
   }

}
