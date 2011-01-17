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

import org.exoplatform.services.rest.BaseObjectModel;
import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.impl.resource.PathValue;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.uri.UriPattern;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FilterDescriptorImpl extends BaseObjectModel implements FilterDescriptor
{
   /**
    * @see PathValue
    */
   private final PathValue path;

   /**
    * @see UriPattern
    */
   private final UriPattern uriPattern;

   /**
    * @param filterClass {@link Class} of filter
    */
   public FilterDescriptorImpl(Class<?> filterClass)
   {
      this(filterClass, ComponentLifecycleScope.PER_REQUEST);
   }

   /**
    * @param filter instance
    */
   public FilterDescriptorImpl(Object filter)
   {
      this(filter.getClass(), ComponentLifecycleScope.SINGLETON);
   }

   /**
    * @param filterClass filter class
    * @param scope filter scope
    * @see ComponentLifecycleScope
    */
   private FilterDescriptorImpl(Class<?> filterClass, ComponentLifecycleScope scope)
   {
      super(filterClass, scope);
      final Path p = filterClass.getAnnotation(Path.class);
      if (p != null)
      {
         this.path = new PathValue(p.value());
         this.uriPattern = new UriPattern(p.value());
      }
      else
      {
         this.path = null;
         this.uriPattern = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   public void accept(ResourceDescriptorVisitor visitor)
   {
      visitor.visitFilterDescriptor(this);
   }

   /**
    * {@inheritDoc}
    */
   public PathValue getPathValue()
   {
      return path;
   }

   /**
    * {@inheritDoc}
    */
   public UriPattern getUriPattern()
   {
      return uriPattern;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      StringBuffer sb = new StringBuffer("[ FilterDescriptorImpl: ");
      sb.append("path: " + getPathValue() + "; ").append("filter class: " + getObjectClass() + "; ").append(
         getConstructorDescriptors() + "; ").append(getFieldInjectors()).append(" ]");
      return sb.toString();
   }
}
