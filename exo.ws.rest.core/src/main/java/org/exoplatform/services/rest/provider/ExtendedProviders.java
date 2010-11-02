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
package org.exoplatform.services.rest.provider;

import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface ExtendedProviders extends Providers
{

   /**
    * Get list of most acceptable writer's media type for specified type.
    * 
    * @param type type
    * @param genericType generic type
    * @param annotations annotations
    * @return sorted acceptable media type collection
    * @see MediaTypeHelper#MEDIA_TYPE_COMPARATOR
    */
   List<MediaType> getAcceptableWriterMediaTypes(Class<?> type, Type genericType, Annotation[] annotations);

   /**
    * @param path request path
    * @return acceptable method invocation filters
    */
   List<ObjectFactory<FilterDescriptor>> getMethodInvokerFilters(String path);

   /**
    * @param path request path
    * @return acceptable request filters
    */
   List<ObjectFactory<FilterDescriptor>> getRequestFilters(String path);

   /**
    * @param path request path
    * @return acceptable response filters
    */
   List<ObjectFactory<FilterDescriptor>> getResponseFilters(String path);

}
