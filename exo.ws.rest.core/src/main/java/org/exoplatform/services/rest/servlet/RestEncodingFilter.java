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
package org.exoplatform.services.rest.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author <a href="mailto:volodymyr.krasnikov@exoplatform.com.ua">Volodymyr Krasnikov</a>
 */
public class RestEncodingFilter implements Filter
{

   /**
    * Request encoding.
    */
   private String requestEncodings;

   /**
    * {@inheritDoc}
    */
   public void destroy()
   {
      // nothing to do.
   }

   /**
    * Set character encoding.
    * {@inheritDoc}
    */
   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException,
      ServletException
   {
      if (req.getCharacterEncoding() == null && requestEncodings != null)
         req.setCharacterEncoding(requestEncodings);
      filterChain.doFilter(req, resp);
   }

   /**
    * {@inheritDoc}
    */
   public void init(FilterConfig config) throws ServletException
   {
      requestEncodings = config.getInitParameter("REQUEST_ENCODING");
   }

}
