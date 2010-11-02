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

/**
 * Application specific set of providers. Providers which were delivered via
 * subclass of javax.ws.rs.core.Application will have an advantage over default
 * providers. Default (embedded in this framework) providers will be in use only
 * if JAX-RS application does not provide own providers with same purposes.
 * 
 * @author <a href="andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ApplicationProviders extends ProviderBinder
{

   private final String applicationId;

   public ApplicationProviders(String applicationId)
   {
      this.applicationId = applicationId;
   }

   /**
    * @return identifier (suppose to use FQN) of application-supplied subclass
    *         of {@link javax.ws.rs.core.Application} via this set of JAX-RS
    *         providers were delivered.
    */
   public String getApplication()
   {
      return applicationId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void init()
   {
   }

}
