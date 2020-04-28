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
   public static interface GenericIngectable<T>
   {
   }

   public static class InjectableComponent implements GenericIngectable<String>
   {
      public String message = "injected from container";
   }

   public static class Provider90 implements Provider<GenericIngectable<String>>
   {
      public GenericIngectable<String> get()
      {
         InjectableComponent injectable = new InjectableComponent();
         injectable.message = "injected from provider";
         return injectable;
      }
   }

   @Path("a")
   public static class Resource1
   {
      @Inject
      private GenericIngectable<String> injected;

      @GET
      public String m()
      {
         assertNotNull(injected);
         return ((InjectableComponent)injected).message;
      }
   }

   @Path("b")
   public static class Resource2
   {
      @Inject
      //private Provider<InjectableComponent> injected;
      private Provider<GenericIngectable<String>> injected;

      @GET
      public String m()
      {
         assertNotNull(injected);
         InjectableComponent inst = (InjectableComponent)injected.get();
         return inst.message;
      }
   }

   @Path("a")
   public static class Resource3
   {
      @Inject
      private GenericIngectable<String> injected;
      private boolean injectedThroughSetter = false;

      @GET
      public String m()
      {
         assertNotNull(injected);
         assertTrue(injectedThroughSetter);
         return ((InjectableComponent)injected).message;
      }

      public void setInjected(GenericIngectable<String> injected)
      {
         this.injectedThroughSetter = true;
         this.injected = injected;
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
      container.registerComponentInstance(Provider90.class.getName(), new Provider90());
      registry(Resource1.class);
      ContainerResponse response = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from provider", response.getEntity());
      unregistry(Resource1.class);
      container.unregisterComponent(Provider90.class.getName());
   }

   public void testInjectProvider() throws Exception
   {
      container.registerComponentInstance(Provider90.class.getName(), new Provider90());
      registry(Resource2.class);
      ContainerResponse response = launcher.service("GET", "/b", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from provider", response.getEntity());
      unregistry(Resource2.class);
      container.unregisterComponent(Provider90.class.getName());
   }

   public void testInjectWithSetter() throws Exception
   {
      container.registerComponentInstance(InjectableComponent.class.getName(), new InjectableComponent());
      registry(Resource3.class);
      ContainerResponse response = launcher.service("GET", "/a", "", null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("injected from container", response.getEntity());
      unregistry(Resource3.class);
      container.unregisterComponent(InjectableComponent.class.getName());
   }
}
