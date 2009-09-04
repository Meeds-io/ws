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
package org.exoplatform.services.rest.impl;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.Property;

import javax.servlet.http.HttpServletRequest;
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
public class RequestDispatcherTest extends AbstractResourceTest
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
      assertEquals("m0", service("POST", "/a", "", null, null).getEntity());
      assertEquals("m1", service("POST", "/a/b", "", null, null).getEntity());
      assertEquals("m2.0", service("POST", "/a/b/c", "", null, null).getEntity());
      assertEquals("m2.1", service("POST", "/a/b/c/d", "", null, null).getEntity());
      assertEquals("m3.0", service("POST", "/a/b/c/d/e", "", null, null).getEntity());
      assertEquals("m3.1", service("POST", "/a/b/c/d/e/f", "", null, null).getEntity());
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
      assertEquals("m0", service("POST", "/", "", null, null).getEntity());
      assertEquals("m1", service("POST", "/a", "", null, null).getEntity());
      assertEquals("#x y", service("POST", "/1/a/b%20/c/%23x%20y", "", null, null).getEntity());
      assertEquals("%23x%20y", service("POST", "/2/a/b%20/c/%23x%20y", "", null, null).getEntity());
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
      assertEquals("/a/b/c/d/m0", service("GET", "/a/b/c/d/m0", "", null, null).getEntity());
      assertEquals("c", service("GET", "/a/b/c/d/m1", "", null, null).getEntity());
      assertEquals("d", service("GET", "/a/b/c/d/m2", "", null, null).getEntity());
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
      service("GET", "/aaa/bbb", "", null, null);
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
      GenericContainerResponse resp = service("GET", "/_a/b/c/d/m0", "", null, null);
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
      service("GET", "/a/b/c/d?q1=q1&q2=q2", "", null, null);
      unregistry(Resource5.class);
   }

   //--------------------------------------
   
   public void testFieldSuperClass() throws Exception
   {
      registry(EndResource.class);
      service("GET", "/a", "", null, null);
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
      service("GET", "/a", "", null, null);
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
   
}
