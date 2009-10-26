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
package org.exoplatform.services.rest.resource;

import org.exoplatform.services.rest.impl.resource.PathValue;
import org.exoplatform.services.rest.uri.UriPattern;

import javax.ws.rs.Path;

/**
 * Describe sub-resource method. Sub-resource method is
 * {@link java.lang.reflect.Method} of resource class which has own {@link Path}
 * annotation and {@link javax.ws.rs.HttpMethod} annotation. This method can't
 * handle request directly.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SubResourceMethodDescriptor extends ResourceMethodDescriptor
{

   /**
    * @return See {@link PathValue}
    */
   PathValue getPathValue();

   /**
    * @return See {@link UriPattern}
    */
   UriPattern getUriPattern();

}
