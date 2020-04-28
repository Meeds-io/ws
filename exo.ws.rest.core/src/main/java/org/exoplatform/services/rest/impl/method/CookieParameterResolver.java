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

import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CookieParameterResolver extends ParameterResolver<CookieParam>
{

   /**
    * See {@link CookieParam}.
    */
   private final CookieParam cookieParam;

   /**
    * @param cookieParam CookieParam
    */
   CookieParameterResolver(CookieParam cookieParam)
   {
      this.cookieParam = cookieParam;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object resolve(org.exoplatform.services.rest.Parameter parameter, ApplicationContext context)
      throws Exception
   {
      String param = this.cookieParam.value();
      Object c = context.getHttpHeaders().getCookies().get(param);
      if (c != null)
         return c;

      if (parameter.getDefaultValue() != null)
         return Cookie.valueOf(parameter.getDefaultValue());

      return null;
   }

}
