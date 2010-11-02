/**
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.picocontainer.Startable;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Purpose of this component is deliver all JAX-RS components registered in eXo
 * container to {@link org.exoplatform.services.rest.impl.ApplicationRegistry}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StartableApplication extends Application implements Startable
{

   private ExoContainer container;

   private Set<Class<?>> cls = new HashSet<Class<?>>();

   private Set<Object> singletons = new HashSet<Object>();

   public StartableApplication(ExoContainerContext containerContext)
   {
      container = containerContext.getContainer();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Class<?>> getClasses()
   {
      return cls;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      for (Object resource : container.getComponentInstancesOfType(ResourceContainer.class))
         singletons.add(resource);
      for (Object resolver : container.getComponentInstancesOfType(ContextResolver.class))
         singletons.add(resolver);
      for (Object mapper : container.getComponentInstancesOfType(ExceptionMapper.class))
         singletons.add(mapper);
      for (Object reader : container.getComponentInstancesOfType(MessageBodyReader.class))
         singletons.add(reader);
      for (Object writer : container.getComponentInstancesOfType(MessageBodyWriter.class))
         singletons.add(writer);
      for (Object filter : container.getComponentInstancesOfType(RequestFilter.class))
         singletons.add(filter);
      for (Object filter : container.getComponentInstancesOfType(ResponseFilter.class))
         singletons.add(filter);
      for (Object filter : container.getComponentInstancesOfType(MethodInvokerFilter.class))
         singletons.add(filter);
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
   }

}
