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

package org.exoplatform.services.rest.impl.resource;

import org.exoplatform.services.rest.impl.method.MethodInvokerFactory;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ApplicationResource extends AbstractResourceDescriptorImpl
{

   /**
    * Identifier of application-supplied subclass of
    * {@link javax.ws.rs.core.Application} via this component was delivered.
    */
   private final String applicationId;

   /**
    * @param applicationId identifier of application-supplied subclass of
    *        {@link javax.ws.rs.core.Application} via this component was
    *        delivered.
    * @param resourceClass resource class
    */
   public ApplicationResource(String applicationId, Class<?> resourceClass)
   {
      super(resourceClass);
      this.applicationId = applicationId;
   }

   /**
    * @param applicationId identifier of application-supplied subclass of
    *        {@link javax.ws.rs.core.Application} via this component was
    *        delivered.
    * @param resourceClass resource class
    * @param invokerFactory invoker factory
    */
   public ApplicationResource(String applicationId, Class<?> resourceClass, MethodInvokerFactory invokerFactory)
   {
      super(resourceClass, invokerFactory);
      this.applicationId = applicationId;
   }

   /**
    * @param applicationId identifier of application-supplied subclass of
    *        {@link javax.ws.rs.core.Application} via this component was
    *        delivered.
    * @param resource resource instance
    * @param invokerFactory invoker factory
    */
   public ApplicationResource(String applicationId, Object resource, MethodInvokerFactory invokerFactory)
   {
      super(resource, invokerFactory);
      this.applicationId = applicationId;
   }

   /**
    * @param applicationId identifier of application-supplied subclass of
    *        {@link javax.ws.rs.core.Application} via this component was
    *        delivered.
    * @param resource resource instance
    */
   public ApplicationResource(String applicationId, Object resource)
   {
      super(resource);
      this.applicationId = applicationId;
   }

   /**
    * @return identifier (suppose to use FQN) of application-supplied subclass
    *         of {@link javax.ws.rs.core.Application} via this component was
    *         delivered.
    */
   public String getApplication()
   {
      return applicationId;
   }
}
