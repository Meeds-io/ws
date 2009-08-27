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

import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OptionsRequestResourceMethodDescriptorImpl extends ResourceMethodDescriptorImpl
{

   /**
    * @param method See {@link Method}
    * @param httpMethod HTTP request method designator
    * @param parameters list of method parameters. See {@link MethodParameter}
    * @param parentResource parent resource for this method
    * @param consumes list of media types which this method can consume
    * @param produces list of media types which this method can produce
    * @param invoker method invoker
    */
   public OptionsRequestResourceMethodDescriptorImpl(Method method, String httpMethod,
      List<MethodParameter> parameters, AbstractResourceDescriptor parentResource, List<MediaType> consumes,
      List<MediaType> produces, MethodInvoker invoker)
   {
      super(method, httpMethod, parameters, parentResource, consumes, produces, invoker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getResponseType()
   {
      return Response.class;
   }

}
