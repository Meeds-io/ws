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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.ConstructorDescriptor;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.ConstructorDescriptorImpl;
import org.exoplatform.services.rest.impl.FieldInjectorImpl;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderDescriptorImpl implements ProviderDescriptor
{

   /**
    * Provider class.
    */
   private final Class<?> providerClass;

   /**
    * Resource class constructors.
    *
    * @see {@link ConstructorDescriptor}
    */
   private final List<ConstructorDescriptor> constructors;

   /**
    * Resource class fields.
    */
   private final List<FieldInjector> fields;

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

   /** Optional data. */
   private MultivaluedMap<String, String> properties;

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
      this.providerClass = providerClass;

      this.constructors = new ArrayList<ConstructorDescriptor>();
      this.fields = new ArrayList<FieldInjector>();
      if (scope == ComponentLifecycleScope.PER_REQUEST)
      {
         for (Constructor<?> constructor : providerClass.getConstructors())
         {
            constructors.add(new ConstructorDescriptorImpl(providerClass, constructor));
         }
         if (constructors.size() == 0)
         {
            String msg = "Not found accepted constructors for provider class " + providerClass.getName();
            throw new RuntimeException(msg);
         }
         // Sort constructors in number parameters order
         if (constructors.size() > 1)
         {
            Collections.sort(constructors, ConstructorDescriptorImpl.CONSTRUCTOR_COMPARATOR);
         }
         // process field
         for (java.lang.reflect.Field jfield : providerClass.getDeclaredFields())
         {
            fields.add(new FieldInjectorImpl(providerClass, jfield));
         }
      }

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
   public List<ConstructorDescriptor> getConstructorDescriptors()
   {
      return constructors;
   }

   /**
    * {@inheritDoc}
    */
   public List<FieldInjector> getFieldInjectors()
   {
      return fields;
   }

   /**
    * {@inheritDoc}
    */
   public Class<?> getObjectClass()
   {
      return providerClass;
   }

   /**
    * {@inheritDoc}
    */
   public MultivaluedMap<String, String> getProperties()
   {
      if (properties == null)
      {
         properties = new MultivaluedMapImpl();
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public List<String> getProperty(String key)
   {
      if (properties != null)
      {
         return properties.get(key);
      }
      return null;
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
