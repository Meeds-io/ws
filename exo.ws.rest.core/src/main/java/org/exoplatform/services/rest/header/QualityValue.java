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
package org.exoplatform.services.rest.header;

/**
 * Implementation of this interface is useful for sort accepted media type and
 * languages by quality factor. For example see
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP/1.1 documentation</a>
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface QualityValue
{

   /**
    * Default quality value. It should be used if quality value is not specified
    * in accept token.
    */
   public static final float DEFAULT_QUALITY_VALUE = 1.0F;

   /**
    * Quality value.
    */
   public static final String QVALUE = "q";

   /**
    * @return value of quality parameter
    */
   float getQvalue();

}
