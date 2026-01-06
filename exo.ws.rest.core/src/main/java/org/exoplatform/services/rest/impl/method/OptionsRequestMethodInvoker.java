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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.resource.GenericMethodResource;
import org.exoplatform.services.rest.wadl.WadlProcessor;
import org.exoplatform.services.rest.wadl.research.Application;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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

      SecurityContext securityContext = ApplicationContextImpl.getCurrent().getSecurityContext();
      String role = "users";

      if (!securityContext.isUserInRole(role)) {
         // user is not in allowed roles
         throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(
             "You do not have access rights to this resource, please contact your administrator. ").type(
             MediaType.TEXT_PLAIN).build());
      }

      Application wadlApplication =
         new WadlProcessor().process(genericMethodResource.getParentResource(), context.getBaseUri());
      return Response.ok(wadlApplication, MediaTypeHelper.WADL_TYPE).build();
   }

}
