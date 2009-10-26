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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.resource.GenericMethodResource;
import org.exoplatform.services.rest.wadl.WadlProcessor;
import org.exoplatform.services.rest.wadl.research.Application;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OptionsRequestMethodInvoker implements MethodInvoker
{

   /**
    * {@inheritDoc}
    */
   public Object invokeMethod(Object resource, GenericMethodResource genericMethodResource, ApplicationContext context)
   {
      Application wadlApplication =
         new WadlProcessor().process(genericMethodResource.getParentResource(), context.getBaseUri());
      return Response.ok(wadlApplication, MediaTypeHelper.WADL_TYPE).build();
   }

}
