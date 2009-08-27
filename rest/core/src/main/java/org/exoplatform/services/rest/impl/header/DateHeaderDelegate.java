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
package org.exoplatform.services.rest.impl.header;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DateHeaderDelegate extends AbstractHeaderDelegate<Date>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<Date> support()
   {
      return Date.class;
   }

   /**
    * Parse date header, header string must be in one of HTTP-date format see
    * {@link <a
    * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.3.1"
    * >HTTP/1.1 documentation</a>} otherwise IllegalArgumentException will be
    * thrown. {@inheritDoc}
    */
   public Date fromString(String header)
   {
      return HeaderHelper.parseDateHeader(header);
   }

   /**
    * Represents {@link Date} as String in format of RFC 1123 {@inheritDoc} .
    */
   public String toString(Date date)
   {
      return HeaderHelper.getDateFormats().get(0).format(date);
   }

}
