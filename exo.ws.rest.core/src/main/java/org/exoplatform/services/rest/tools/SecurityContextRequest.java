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

package org.exoplatform.services.rest.tools;

import org.exoplatform.services.rest.impl.ContainerRequest;

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * For test purposes only. Need this to emulate authenticated user.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class SecurityContextRequest extends ContainerRequest
{
   private final SecurityContext sctx;

   public SecurityContextRequest(String method, URI requestUri, URI baseUri, InputStream entityStream,
      MultivaluedMap<String, String> httpHeaders, SecurityContext sctx)
   {
      super(method, requestUri, baseUri, entityStream, httpHeaders);
      this.sctx = sctx;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getAuthenticationScheme()
   {
      return sctx != null ? sctx.getAuthenticationScheme() : null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Principal getUserPrincipal()
   {
      return sctx != null ? sctx.getUserPrincipal() : null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isSecure()
   {
      return sctx != null ? sctx.isSecure() : false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isUserInRole(String role)
   {
      return sctx != null ? sctx.isUserInRole(role) : false;
   }

}
