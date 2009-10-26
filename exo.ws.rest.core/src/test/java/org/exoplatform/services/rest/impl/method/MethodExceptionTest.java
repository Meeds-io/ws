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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.UnhandledException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date: 21 Jan 2009
 * 
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: TestMethodException.java
 */
public class MethodExceptionTest extends AbstractResourceTest
{

   @SuppressWarnings("serial")
   public static class UncheckedException extends Exception
   {

      public UncheckedException()
      {
         super();
      }

      public UncheckedException(String msg)
      {
         super(msg);
      }

   }

   @Path("/a")
   public static class Resource1
   {

      @GET
      @Path("/0")
      public void m0() throws WebApplicationException
      {
         throw new WebApplicationException();
      }

      @GET
      @Path("/1")
      public Response m1() throws WebApplicationException
      {
         return new WebApplicationException().getResponse();
      }

      @GET
      @Path("/2")
      public void m2() throws Exception
      {
         throw new UncheckedException("Unchecked exception");
      }

   }

   public void testExceptionProcessing() throws Exception
   {
      Resource1 resource = new Resource1();
      registry(resource);

      assertEquals(500, service("GET", "/a/0", "", null, null).getStatus());
      assertEquals(500, service("GET", "/a/1", "", null, null).getStatus());
      try
      {
         assertEquals(500, service("GET", "/a/2", "", null, null).getStatus());
         fail();
      }
      catch (UnhandledException e)
      {
      }
      unregistry(resource);
   }

   //
}
