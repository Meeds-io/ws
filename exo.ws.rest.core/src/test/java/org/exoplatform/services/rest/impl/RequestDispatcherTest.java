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
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.Property;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestDispatcherTest extends BaseTest
{

   @Path("/a")
   public static class Resource1
   {
      @POST
      public String m0()
      {
         return "m0";
      }

      @POST
      @Path("/b")
      public String m1()
      {
         return "m1";
      }

      @Path("b/c")
      public SubResource1 m2()
      {
         return new SubResource1();
      }
   }

   public static class SubResource1
   {
      @POST
      public String m0()
      {
         return "m2.0";
      }

      @POST
      @Path("d")
      public String m1()
      {
         return "m2.1";
      }

      @Path("d/e")
      public SubResource2 m2()
      {
         return new SubResource2();
      }
   }

   public static class SubResource2
   {
      @POST
      public String m0()
      {
         return "m3.0";
      }

      @POST
      @Path("f")
      public String m1()
      {
         return "m3.1";
      }
   }

   public void testResource1() throws Exception
   {
      Resource1 r1 = new Resource1();
      registry(r1);
      assertEquals("m0", launcher.service("POST", "/a", "", null, null, null).getEntity());
      assertEquals("m1", launcher.service("POST", "/a/b", "", null, null, null).getEntity());
      assertEquals("m2.0", launcher.service("POST", "/a/b/c", "", null, null, null).getEntity());
      assertEquals("m2.1", launcher.service("POST", "/a/b/c/d", "", null, null, null).getEntity());
      assertEquals("m3.0", launcher.service("POST", "/a/b/c/d/e", "", null, null, null).getEntity());
      assertEquals("m3.1", launcher.service("POST", "/a/b/c/d/e/f", "", null, null, null).getEntity());
      unregistry(r1);
   }

   //--------------------------------------
   @Path("/")
   public static class Resource2
   {
      @POST
      public String m0()
      {
         return "m0";
      }

      @POST
      @Path("a")
      public String m1()
      {
         return "m1";
      }

      @POST
      @Path("1/a/b /c/{d}")
      public String m2(@PathParam("d") String d)
      {
         return d;
      }

      @POST
      @Path("2/a/b /c/{d}")
      public String m3(@Encoded @PathParam("d") String d)
      {
         return d;
      }

   }

   public void testResource2() throws Exception
   {
      Resource2 r2 = new Resource2();
      registry(r2);
      assertEquals("m0", launcher.service("POST", "/", "", null, null, null).getEntity());
      assertEquals("m1", launcher.service("POST", "/a", "", null, null, null).getEntity());
      assertEquals("#x y", launcher.service("POST", "/1/a/b%20/c/%23x%20y", "", null, null, null).getEntity());
      assertEquals("%23x%20y", launcher.service("POST", "/2/a/b%20/c/%23x%20y", "", null, null, null).getEntity());
      unregistry(r2);
   }

   //--------------------------------------
   @Path("/a/b/{c}/{d}")
   public static class Resource3
   {

      @Context
      private UriInfo uriInfo;

      private String c;

      private String d;

      public Resource3(@PathParam("c") String c)
      {
         this.c = c;
      }

      public Resource3(@PathParam("c") String c, @PathParam("d") String d)
      {
         this.c = c;
         this.d = d;
      }

      @GET
      @Path("m0")
      public String m0()
      {
         return uriInfo.getRequestUri().toString();
      }

      @GET
      @Path("m1")
      public String m1()
      {
         return c;
      }

      @GET
      @Path("m2")
      public String m2()
      {
         return d;
      }
   }

   public void testResourceConstructorAndFields() throws Exception
   {
      registry(Resource3.class);
      assertEquals("/a/b/c/d/m0", launcher.service("GET", "/a/b/c/d/m0", "", null, null, null).getEntity());
      assertEquals("c", launcher.service("GET", "/a/b/c/d/m1", "", null, null, null).getEntity());
      assertEquals("d", launcher.service("GET", "/a/b/c/d/m2", "", null, null, null).getEntity());
      unregistry(Resource3.class);
   }

   //--------------------------------------
   public static class TestContainerComponent
   {
   }

   @Path("{a}")
   public static class Resource4
   {

      @Context
      private UriInfo uriInfo;

      @Context
      private HttpServletRequest request;

      private TestContainerComponent tc;

      public Resource4(@PathParam("a") String test)
      {
         // this constructor must not be used. There is constructors with more
         // parameter
         fail("Must not be used.");
      }

      public Resource4(TestContainerComponent tc, @PathParam("a") String test)
      {
         this.tc = tc;
      }

      @GET
      @Path("{b}")
      public void m0()
      {
         assertNotNull(tc);
         assertNotNull(uriInfo);
         assertNotNull(request);
      }

   }

   public void testResourceConstructorsContainer() throws Exception
   {
      container.registerComponentInstance(TestContainerComponent.class.getName(), new TestContainerComponent());
      registry(Resource4.class);

      EnvironmentContext envctx = new EnvironmentContext();

      HttpServletRequest httpRequest = new MockHttpServletRequest("/aaa/bbb", null, 0, "GET", null);
      envctx.put(HttpServletRequest.class, httpRequest);

      launcher.service("GET", "/aaa/bbb", "", null, null, envctx);

      unregistry(Resource4.class);
   }

   // --------------------------------------
   public static class Failure
   {
      // not member of exo-container
   }

   @Path("/_a/b/{c}/{d}")
   public static class ResourceFail
   {

      public ResourceFail(Failure failure, @PathParam("c") String c, @PathParam("d") String d)
      {
      }

      @GET
      @Path("m0")
      public String m0()
      {
         return "m0";
      }
   }

   public void testResourceConstructorFail() throws Exception
   {
      registry(ResourceFail.class);
      GenericContainerResponse resp = launcher.service("GET", "/_a/b/c/d/m0", "", null, null, null);
      String entity = (String)resp.getEntity();
      assertTrue(entity.startsWith("Can't instantiate resource "));
      assertEquals(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
      unregistry(ResourceFail.class);
   }

   //--------------------------------------

   @Path("a/{b}/{c}")
   public static class Resource5
   {

      @PathParam("b")
      private String b;

      private String c;

      @QueryParam("q1")
      private String q1;

      public Resource5(@PathParam("c") String c)
      {
         this.c = c;
      }

      @GET
      @Path("{d}")
      public void m1(@PathParam("d") String d, @QueryParam("q2") String q2)
      {
         assertEquals("b", b);
         assertEquals("c", c);
         assertEquals("d", d);
         assertEquals("q1", q1);
         assertEquals("q2", q2);
      }

   }

   public void testResource5() throws Exception
   {
      registry(Resource5.class);
      launcher.service("GET", "/a/b/c/d?q1=q1&q2=q2", "", null, null, null);
      unregistry(Resource5.class);
   }

   //--------------------------------------

   public void testFieldSuperClass() throws Exception
   {
      registry(EndResource.class);
      launcher.service("GET", "/a", "", null, null, null);
      unregistry(EndResource.class);
   }

   public abstract static class AbstractResource
   {
      @Context
      protected UriInfo uriInfo;

      @Context
      public Request request;

      @Context
      protected UriInfo something;
   }

   public abstract static class ExtResource extends AbstractResource
   {
      @Context
      protected SecurityContext sc;

   }

   @Path("a")
   public static class EndResource extends ExtResource
   {
      @Context
      private HttpHeaders header;

      @Context
      private SecurityContext something;

      @GET
      public void m1()
      {
         assertNotNull(uriInfo);
         assertNotNull(request);
         assertNotNull(this.something);
         assertNotNull(super.something);
         assertTrue(this.something instanceof SecurityContext);
         assertTrue(super.something instanceof UriInfo);
         assertNotNull(sc);
         assertNotNull(header);
      }
   }

   // -----------------------------------------------

   public void testPropertyInjection() throws Exception
   {
      registry(Resource6.class);
      RequestHandlerImpl.setProperty("prop1", "hello");
      RequestHandlerImpl.setProperty("prop2", "test");
      launcher.service("GET", "/a", "", null, null, null);
      unregistry(Resource6.class);

   }

   @Path("a")
   public static class Resource6
   {

      @Property("prop1")
      private String prop1;

      private final String prop2;

      public Resource6(@Property("prop2") String cProp)
      {
         this.prop2 = cProp;
      }

      @GET
      public void m1()
      {
         assertEquals("hello", prop1);
         assertEquals("test", prop2);
      }

   }

   @Path("/a")
   public static class Resource7
   {
      @GET
      @Path("b/c/{d:.+}.{e}")
      public String m1(@PathParam("d") String d)
      {
         return d;
      }

      @GET
      @Path("b/e/{d:.+}.{e}")
      public String m2(@PathParam("e") String e)
      {
         return e;
      }

      @GET
      @Path("b/{d:.+}.{e}.{f}")
      public String m3(@PathParam("e") String e)
      {
         return e;
      }
   }

   public void testResource7() throws Exception
   {
      registry(Resource7.class);
      assertEquals("m0.m1", launcher.service("GET", "/a/b/c/m0.m1.m2", "", null, null, null).getEntity());
      assertEquals("m0", launcher.service("GET", "/a/b/c/m0.m1", "", null, null, null).getEntity());
      assertEquals("m2", launcher.service("GET", "/a/b/e/m0.m1.m2", "", null, null, null).getEntity());
      assertEquals("m2", launcher.service("GET", "/a/b/m0.m1.m2.m3", "", null, null, null).getEntity());
      unregistry(Resource7.class);
   }

}
