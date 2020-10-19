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
package org.exoplatform.services.rest.impl.header;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

import java.util.List;

import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CookieHeaderDelegate extends AbstractHeaderDelegate<Cookie>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<Cookie> support()
   {
      return Cookie.class;
   }

   /**
    * {@inheritDoc}
    */
   public Cookie fromString(String header)
   {
      if (header == null)
         throw new IllegalArgumentException();

      List<Cookie> l = HeaderHelper.parseCookies(header);
      if (l.size() > 0) // waiting for one cookie
         return l.get(0);

      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String toString(Cookie cookie)
   {
      StringBuilder sb = new StringBuilder();

      sb.append("$Version=").append(cookie.getVersion()).append(';');

      sb.append(cookie.getName()).append('=').append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getValue()));

      if (cookie.getDomain() != null)
         sb.append(';').append("$Domain=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getDomain()));

      if (cookie.getPath() != null)
         sb.append(';').append("$Path=").append(HeaderHelper.addQuotesIfHasWhitespace(cookie.getPath()));

      return sb.toString();
   }

}
