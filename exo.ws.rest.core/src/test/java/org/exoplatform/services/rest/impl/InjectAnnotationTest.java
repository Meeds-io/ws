/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import org.exoplatform.services.rest.BaseTest;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class InjectAnnotationTest extends BaseTest
{
   public static class InjectableComponent
   {
      public String message = "injected from container";
   }

   public static class InjectionProvider0 implements InjectionProvider<InjectableComponent>
   {
      public InjectableComponent get()
      {
         InjectableComponent injectable = new InjectableComponent();
         injectable.message = "injected from provider";
         return injectable;
      }

      public boolean isSupported(Class<InjectableComponent> clazz)
      {
         return InjectableComponent.class.isAssignableFrom(clazz);
      }
   }

   @Path("a")
   public static class Resource1
   {
      @Inject
      private InjectableComponent injected;

      @GET
      public String m()
      {
         assertNotNull(injected);
         return injected.message;
      }
   }

   @Path("b")
   public static class Resource2
   {
      @Inject
      private Provider<InjectableComponent> injected;

      @GET
      public String m()
      {
         assertNotNull(injected);
         InjectableComponent inst = injected.get();
         return inst.message;
      }
   }

   public void testInjectFromContainer() throws Exception
   {
      container.registerComponentInstance(InjectableComponent.class.getName(), new InjectableComponent());
      registry(Resource1.class);
      ContainerResponse response = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from container", response.getEntity());
      unregistry(Resource1.class);
      container.unregisterComponent(InjectableComponent.class.getName());
   }

   public void testInjectFromProvider() throws Exception
   {
      container.registerComponentInstance(InjectionProvider0.class.getName(), new InjectionProvider0());
      registry(Resource1.class);
      ContainerResponse response = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from provider", response.getEntity());
      unregistry(Resource1.class);
      container.unregisterComponent(InjectionProvider0.class.getName());
   }

   public void testInjectProvider() throws Exception
   {
      container.registerComponentInstance(InjectionProvider0.class.getName(), new InjectionProvider0());
      registry(Resource2.class);
      ContainerResponse response = launcher.service("GET", "/b", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from provider", response.getEntity());
      unregistry(Resource2.class);
      container.unregisterComponent(InjectionProvider0.class.getName());
   }
}
