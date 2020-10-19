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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceBinderTest extends BaseTest
{

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testBind()
   {
      binder.addResource(Resource.class, null);
      assertEquals(1, binder.getSize());
   }

   public void testUnbind()
   {
      binder.addResource(Resource.class, null);
      binder.removeResource(Resource.class);
      assertEquals(0, binder.getSize());
   }

   @Path("/a/b/{c}")
   public static class Resource
   {

      @SuppressWarnings("unused")
      @PathParam("c")
      private String pathsegm;

      public Resource()
      {
      }

      public Resource(@Context UriInfo uriInfo)
      {
      }

      @GET
      @Produces("text/html")
      public void m1()
      {
      }

      @GET
      @Path("d")
      @Produces("text/html")
      public void m2()
      {
      }

      @Path("d")
      public void m3()
      {
      }
   }

   //-------------------------------------

   public void testSameResourceURI()
   {
      binder.addResource(SameURIResource1.class, null);
      assertEquals(1, binder.getSize());
      try
      {
         binder.addResource(SameURIResource2.class, null);
      }
      catch (ResourcePublicationException e)
      {
      }
      assertEquals(1, binder.getSize());

      binder.clear();
      binder.addResource(SameURIResource2.class, null);
      assertEquals(1, binder.getSize());
      try
      {
         binder.addResource(SameURIResource1.class, null);
      }
      catch (ResourcePublicationException e)
      {
      }
      assertEquals(1, binder.getSize());

      binder.clear();
      binder.addResource(new SameURIResource1(), null);
      assertEquals(1, binder.getSize());
      try
      {
         binder.addResource(new SameURIResource2(), null);
      }
      catch (ResourcePublicationException e)
      {
      }
      assertEquals(1, binder.getSize());

      binder.clear();
      binder.addResource(new SameURIResource2(), null);
      assertEquals(1, binder.getSize());
      try
      {
         binder.addResource(new SameURIResource1(), null);
      }
      catch (ResourcePublicationException e)
      {
      }
      assertEquals(1, binder.getSize());
   }

   @Path("/a/b/c/{d}/e")
   public static class SameURIResource1
   {
      @GET
      public void m0()
      {
      }
   }

   @Path("/a/b/c/{d}/e")
   public static class SameURIResource2
   {
      @GET
      public void m0()
      {
      }
   }

}
