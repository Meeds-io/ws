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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.PerRequestObjectFactory;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.SingletonObjectFactory;
import org.exoplatform.services.rest.impl.resource.ApplicationResource;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorValidator;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.picocontainer.Startable;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Purpose of this class is to get all subclasses of
 * {@link javax.ws.rs.core.Application} from eXo container and to process set of
 * object of classes provided by it as JAX-RS components.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ApplicationRegistry implements Startable
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ApplicationRegistry.class);

   protected final ExoContainer container;

   protected final ResourceBinder resources;

   protected final ProvidersRegistry providers;

   protected final ResourceDescriptorValidator rdv = ResourceDescriptorValidator.getInstance();

   public ApplicationRegistry(ExoContainerContext containerContext, ResourceBinder resources,
      ProvidersRegistry providers, StartableApplication eXo /* Be sure eXo components are initialized. */)
   {
      this.resources = resources;
      this.providers = providers;
      this.container = containerContext.getContainer();
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void start()
   {
      List<Application> all = container.getComponentInstancesOfType(Application.class);
      for (Application app : all)
      {
         addApplication(app);
      }
   }

   @SuppressWarnings("unchecked")
   public void addApplication(Application app)
   {
      String applicationId = app.getClass().getName();
      ApplicationProviders appProviders = new ApplicationProviders(applicationId);
      for (Object obj : app.getSingletons())
      {
         Class clazz = obj.getClass();
         if (clazz.getAnnotation(Provider.class) != null)
         {
            if (obj instanceof ContextResolver)
               appProviders.addContextResolver((ContextResolver)obj);
            if (obj instanceof ExceptionMapper)
               appProviders.addExceptionMapper((ExceptionMapper)obj);
            if (obj instanceof MessageBodyReader)
               appProviders.addMessageBodyReader((MessageBodyReader)obj);
            if (obj instanceof MessageBodyWriter)
               appProviders.addMessageBodyWriter((MessageBodyWriter)obj);
         }
         else if (clazz.getAnnotation(Filter.class) != null)
         {
            if (obj instanceof MethodInvokerFilter)
               appProviders.addMethodInvokerFilter((MethodInvokerFilter)obj);
            if (obj instanceof RequestFilter)
               appProviders.addRequestFilter((RequestFilter)obj);
            if (obj instanceof ResponseFilter)
               appProviders.addResponseFilter((ResponseFilter)obj);
         }
         else if (clazz.getAnnotation(Path.class) != null)
         {
            AbstractResourceDescriptor descriptor = new ApplicationResource(applicationId, obj);
            descriptor.accept(rdv);
            resources.addResource(new SingletonObjectFactory<AbstractResourceDescriptor>(descriptor, obj));
         }
         else
         {
            LOG.warn("Unknown class type: " + clazz.getName() + " found in " + applicationId);
         }
      }
      for (Class clazz : app.getClasses())
      {
         if (clazz.getAnnotation(Provider.class) != null)
         {
            if (ContextResolver.class.isAssignableFrom(clazz))
               appProviders.addContextResolver(clazz);
            if (ExceptionMapper.class.isAssignableFrom(clazz))
               appProviders.addExceptionMapper(clazz);
            if (MessageBodyReader.class.isAssignableFrom(clazz))
               appProviders.addMessageBodyReader(clazz);
            if (MessageBodyWriter.class.isAssignableFrom(clazz))
               appProviders.addMessageBodyWriter(clazz);
         }
         else if (clazz.getAnnotation(Filter.class) != null)
         {
            if (MethodInvokerFilter.class.isAssignableFrom(clazz))
               appProviders.addMethodInvokerFilter(clazz);
            if (RequestFilter.class.isAssignableFrom(clazz))
               appProviders.addRequestFilter(clazz);
            if (ResponseFilter.class.isAssignableFrom(clazz))
               appProviders.addResponseFilter(clazz);
         }
         else if (clazz.getAnnotation(Path.class) != null)
         {
            AbstractResourceDescriptor descriptor = new ApplicationResource(applicationId, clazz);
            descriptor.accept(rdv);
            resources.addResource(new PerRequestObjectFactory<AbstractResourceDescriptor>(descriptor));
         }
         else
         {
            LOG.warn("Unknown class type: " + clazz.getName() + " found in: " + applicationId);
         }
      }
      this.providers.addProviders(appProviders);
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
   }

}
