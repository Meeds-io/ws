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
package org.exoplatform.services.rest.impl.resource;

import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.uri.UriPattern;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SubResourceLocatorDescriptorImpl implements SubResourceLocatorDescriptor
{

   /**
    * See {@link PathValue}.
    */
   private final PathValue path;

   /**
    * See {@link UriPattern}.
    */
   private final UriPattern uriPattern;

   /**
    * See {@link Method}.
    */
   private final Method method;

   /**
    * Parent resource for this method resource, in other words class which
    * contains this method.
    */
   private final AbstractResourceDescriptor parentResource;

   /**
    * List of method's parameters. See {@link MethodParameter} .
    */
   private final List<MethodParameter> parameters;

   /**
    * Method invoker.
    */
   private final MethodInvoker invoker;

   /**
    * Constructs new instance of {@link SubResourceLocatorDescriptor}.
    * 
    * @param path See {@link PathValue}
    * @param method See {@link Method}
    * @param parameters list of method parameters. See {@link MethodParameter}
    * @param parentResource parent resource for this method
    * @param invoker method invoker
    */
   SubResourceLocatorDescriptorImpl(PathValue path, Method method, List<MethodParameter> parameters,
      AbstractResourceDescriptor parentResource, MethodInvoker invoker)
   {
      this.path = path;
      this.uriPattern = new UriPattern(path.getPath());
      this.method = method;
      this.parameters = parameters;
      this.parentResource = parentResource;
      this.invoker = invoker;
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
   public void accept(ResourceDescriptorVisitor visitor)
   {
      visitor.visitSubResourceLocatorDescriptor(this);
   }

   /**
    * {@inheritDoc}
    */
   public Method getMethod()
   {
      return method;
   }

   /**
    * {@inheritDoc}
    */
   public List<MethodParameter> getMethodParameters()
   {
      return parameters;
   }

   /**
    * {@inheritDoc}
    */
   public AbstractResourceDescriptor getParentResource()
   {
      return parentResource;
   }

   /**
    * {@inheritDoc}
    */
   public MethodInvoker getMethodInvoker()
   {
      return invoker;
   }

   /**
    * {@inheritDoc}
    */
   public Class<?> getResponseType()
   {
      return getMethod().getReturnType();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      StringBuffer sb = new StringBuffer("[ SubResourceMethodDescriptorImpl: ");
      sb.append("resource: " + getParentResource() + "; ").append("path: " + getPathValue() + "; ").append(
         "return type: " + getResponseType() + "; ").append("invoker: " + getMethodInvoker()).append(" ]");
      return sb.toString();
   }
}
