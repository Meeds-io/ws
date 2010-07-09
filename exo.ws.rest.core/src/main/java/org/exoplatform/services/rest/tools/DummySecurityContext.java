/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.services.rest.tools;

import org.exoplatform.services.rest.impl.ContainerRequest;

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DummySecurityContext extends ContainerRequest
{
   private DummyPrincipal dummyPrincipal;

   public DummySecurityContext(String method, URI requestUri, URI baseUri, InputStream entityStream,
      MultivaluedMap<String, String> httpHeaders)
   {
      super(method, requestUri, baseUri, entityStream, httpHeaders);
      dummyPrincipal = new DummyPrincipal();
   }

   @Override
   public String getAuthenticationScheme()
   {
      // Consider as Basic Authentication
      return BASIC_AUTH;
   }

   @Override
   public Principal getUserPrincipal()
   {
      return dummyPrincipal;
   }

   @Override
   public boolean isSecure()
   {
      return false;
   }

   @Override
   public boolean isUserInRole(String role)
   {
      return dummyPrincipal.isUserInRole(role);
   }

   class DummyPrincipal implements Principal
   {

      Set<String> roles = new HashSet<String>();

      public DummyPrincipal()
      {
         roles.add("administrators");
         roles.add("users");
      }

      public String getName()
      {
         return "root";
      }

      public boolean isUserInRole(String role)
      {
         return roles.contains(role);
      }

   }

}
