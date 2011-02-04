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
package org.exoplatform.services.rest;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ApplicationRegistry;
import org.exoplatform.services.rest.impl.DependencySupplier;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.ProvidersRegistry;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTest extends TestCase
{

   protected StandaloneContainer container;

   protected ProviderBinder providers;

   protected ResourceBinder binder;

   protected RequestHandlerImpl requestHandler;

   protected ResourceLauncher launcher;

   protected ApplicationRegistry applicationRegistry;

   protected ProvidersRegistry providersRegistry;

   public void setUp() throws Exception
   {
      String conf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      //StandaloneContainer.setConfigurationPath("src/test/resources/conf/standalone/test-configuration.xml");
      StandaloneContainer.setConfigurationURL(conf);
      container = StandaloneContainer.getInstance();

      applicationRegistry = (ApplicationRegistry)container.getComponentInstanceOfType(ApplicationRegistry.class);
      binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
      requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      providersRegistry = (ProvidersRegistry)container.getComponentInstanceOfType(ProvidersRegistry.class);
      DependencySupplier dependencySupplier =
         (DependencySupplier)container.getComponentInstanceOfType(DependencySupplier.class);

      // reset default providers to be sure it is clean.
      ProviderBinder.setInstance(new ProviderBinder());
      providers = ProviderBinder.getInstance();

      binder.clear();

      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers, dependencySupplier));
      
      launcher = new ResourceLauncher(requestHandler);
   }

   public void tearDown() throws Exception
   {
   }

   public void registry(Object resource) throws Exception
   {
      //    container.registerComponentInstance(resource);
      binder.addResource(resource, null);
   }

   public void registry(Class<?> resourceClass) throws Exception
   {
      //    container.registerComponentImplementation(resourceClass.getName(), resourceClass);
      binder.addResource(resourceClass, null);
   }

   public void unregistry(Object resource)
   {
      //    container.unregisterComponentByInstance(resource);
      binder.removeResource(resource.getClass());
   }

   public void unregistry(Class<?> resourceClass)
   {
      //    container.unregisterComponent(resourceClass.getName());
      binder.removeResource(resourceClass);
   }

}
