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

import org.exoplatform.services.rest.header.QualityValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AcceptToken extends Token implements QualityValue
{

   /**
    * Quality value factor.
    */
   private final float qValue;

   /**
    * Create AcceptToken with default quality value 1.0 .
    * 
    * @param token a token
    */
   public AcceptToken(String token)
   {
      super(token);
      qValue = DEFAULT_QUALITY_VALUE;
   }

   /**
    * Create AcceptToken with specified quality value.
    * @param token a token
    * @param qValue a quality value
    */
   public AcceptToken(String token, float qValue)
   {
      super(token);
      this.qValue = qValue;
   }

   // QualityValue

   /**
    * {@inheritDoc}
    */
   public float getQvalue()
   {
      return qValue;
   }

}
