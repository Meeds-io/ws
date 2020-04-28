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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.services.rest.BaseObjectModel;
import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderDescriptorImpl extends BaseObjectModel implements ProviderDescriptor
{
   /**
    * List of media types which this method can consume. See
    * {@link javax.ws.rs.Consumes} .
    */
   private final List<MediaType> consumes;

   /**
    * List of media types which this method can produce. See
    * {@link javax.ws.rs.Produces} .
    */
   private final List<MediaType> produces;

   /**
    * @param providerClass provider class
    */
   public ProviderDescriptorImpl(Class<?> providerClass)
   {
      this(providerClass, ComponentLifecycleScope.PER_REQUEST);
   }

   /**
    * @param provider provider instance
    */
   public ProviderDescriptorImpl(Object provider)
   {
      this(provider.getClass(), ComponentLifecycleScope.SINGLETON);
   }

   /**
    * @param providerClass provider class
    * @param scope provider scope
    */
   private ProviderDescriptorImpl(Class<?> providerClass, ComponentLifecycleScope scope)
   {
      super(providerClass, scope);
      this.consumes = MediaTypeHelper.createConsumesList(providerClass.getAnnotation(Consumes.class));
      this.produces = MediaTypeHelper.createProducesList(providerClass.getAnnotation(Produces.class));
   }

   /**
    * {@inheritDoc}
    */
   public void accept(ResourceDescriptorVisitor visitor)
   {
      visitor.visitProviderDescriptor(this);
   }

   /**
    * {@inheritDoc}
    */
   public List<MediaType> consumes()
   {
      return consumes;
   }

   /**
    * {@inheritDoc}
    */
   public List<MediaType> produces()
   {
      return produces;
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer("[ ProviderDescriptorImpl: ");
      sb.append("provider class: " + getObjectClass() + "; ").append("produces media type: " + produces() + "; ")
         .append("consumes media type: " + consumes() + "; ").append(getConstructorDescriptors() + "; ").append(
            getFieldInjectors()).append(" ]");
      return sb.toString();

   }
}
