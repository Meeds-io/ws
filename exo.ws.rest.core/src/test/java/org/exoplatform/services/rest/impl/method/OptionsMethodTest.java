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
package org.exoplatform.services.rest.impl.method;

import jakarta.servlet.http.HttpServletRequest;
import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.services.test.mock.MockPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Principal;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS. <br>
 * Date: 21 Jan 2009
 *
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: OptionsMethodTest.java
 */
public class OptionsMethodTest extends BaseTest
{

   @Target(ElementType.METHOD)
   @Retention(RetentionPolicy.RUNTIME)
   @HttpMethod("OPTIONS")
   public @interface OPTIONS {

   }

   @Path("/a")
   public static class Resource1
   {

      @OPTIONS
      public String m0()
      {
         return "options";
      }

   }

   @Path("/b")
   public static class Resource2
   {

      @GET
      public String m0()
      {
         return "get";
      }

   }

   public void testOptionsMethod() throws Exception
   {
      Resource1 resource1 = new Resource1();
      registry(resource1);
      assertEquals("options", launcher.service("OPTIONS", "/a", "", null, null, null).getEntity());
      unregistry(resource1);

      Resource2 resource2 = new Resource2();
      registry(resource2);
      assertEquals(403, launcher.service("OPTIONS", "/b", "", null, null, null).getStatus());


      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest = new MockHttpServletRequest("/b", null, 0, "OPTIONS", null);
      envctx.put(HttpServletRequest.class, httpRequest);
      envctx.put(SecurityContext.class, new MockSecurityContext("john"));

      assertEquals(200, launcher.service("OPTIONS", "/b", "", null, null, envctx).getStatus());
      assertNotNull(launcher.service("OPTIONS", "/b", "", null, null, envctx).getResponse().getMetadata());

   }

   protected static class MockSecurityContext implements SecurityContext {

      private final String username;

      public MockSecurityContext(String username) {
         this.username = username;
      }

      public Principal getUserPrincipal() {
         return new MockPrincipal(username);
      }

      public boolean isUserInRole(String role) {
         if(username == null) {
            return false;
         }
         if (role == null) {
            return false;
         }
         return true;
      }

      public boolean isSecure() {
         return false;
      }

      public String getAuthenticationScheme() {
         return null;
      }
   }

}
