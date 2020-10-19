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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DummySecurityContext implements SecurityContext
{

   private final Principal principal;

   private final Set<String> userRoles;

   public DummySecurityContext(Principal principal, Set<String> userRoles)
   {
      this.principal = principal;
      this.userRoles= new HashSet<String>(userRoles);
   }

   /**
    * {@inheritDoc}
    */
   public String getAuthenticationScheme()
   {
      // Consider as Basic Authentication
      return BASIC_AUTH;
   }

   /**
    * {@inheritDoc}
    */
   public Principal getUserPrincipal()
   {
      return principal;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isSecure()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isUserInRole(String role)
   {
      return userRoles.contains(role);
   }

}
