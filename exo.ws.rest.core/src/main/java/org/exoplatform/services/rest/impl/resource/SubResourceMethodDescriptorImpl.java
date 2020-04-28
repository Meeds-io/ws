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
package org.exoplatform.services.rest.impl.resource;

import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;
import org.exoplatform.services.rest.uri.UriPattern;

import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SubResourceMethodDescriptorImpl implements SubResourceMethodDescriptor
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
    * This method will be invoked.
    */
   private final Method method;

   /**
    * HTTP request method designator.
    */
   private final String httpMethod;

   /**
    * List of method's parameters. See {@link MethodParameter} .
    */
   private final List<MethodParameter> parameters;

   /**
    * Parent resource for this method resource, in other words class which
    * contains this method.
    */
   private final AbstractResourceDescriptor parentResource;

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
    * Method invoker.
    */
   private final MethodInvoker invoker;

   /**
    * Constructs new instance of {@link SubResourceMethodDescriptor}.
    * 
    * @param path See {@link PathValue}
    * @param method See {@link Method}
    * @param httpMethod HTTP request method designator
    * @param parameters list of method parameters. See {@link MethodParameter}
    * @param parentResource parent resource for this method
    * @param consumes list of media types which this method can consume
    * @param produces list of media types which this method can produce
    * @param invoker method invoker
    */
   SubResourceMethodDescriptorImpl(PathValue path, Method method, String httpMethod, List<MethodParameter> parameters,
      AbstractResourceDescriptor parentResource, List<MediaType> consumes, List<MediaType> produces,
      MethodInvoker invoker)
   {
      this.path = path;
      this.uriPattern = new UriPattern(path.getPath());
      this.method = method;
      this.httpMethod = httpMethod;
      this.parameters = parameters;
      this.parentResource = parentResource;
      this.consumes = consumes;
      this.produces = produces;
      this.invoker = invoker;
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
   public String getHttpMethod()
   {
      return httpMethod;
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
   public void accept(ResourceDescriptorVisitor visitor)
   {
      visitor.visitSubResourceMethodDescriptor(this);
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
         "HTTP method: " + getHttpMethod() + "; ").append("produces media type: " + produces() + "; ").append(
         "consumes media type: " + consumes() + "; ").append("return type: " + getResponseType() + "; ").append(
         "invoker: " + getMethodInvoker()).append(" ]");
      return sb.toString();
   }

}
