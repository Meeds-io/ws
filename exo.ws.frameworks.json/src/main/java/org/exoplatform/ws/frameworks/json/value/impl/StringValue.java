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
package org.exoplatform.ws.frameworks.json.value.impl;

import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: StringValue.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public class StringValue extends JsonValue
{

   /**
    * Value.
    */
   private final String value;

   /**
    * Constructs new StringValue.
    * @param value the value.
    */
   public StringValue(String value)
   {
      this.value = value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isString()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getStringValue()
   {
      return value;
   }

   @Override
   public boolean getBooleanValue()
   {
      return Boolean.parseBoolean(value);
   }

   @Override
   public Number getNumberValue()
   {
      try
      {
         return Double.parseDouble(value);
      }
      catch (NumberFormatException e)
      {
         return 0;
      }
   }

   @Override
   public byte getByteValue()
   {
      return getNumberValue().byteValue();
   }

   @Override
   public short getShortValue()
   {
      return getNumberValue().shortValue();
   }

   @Override
   public int getIntValue()
   {
      return getNumberValue().intValue();
   }

   @Override
   public long getLongValue()
   {
      return getNumberValue().longValue();
   }

   @Override
   public float getFloatValue()
   {
      return getNumberValue().floatValue();
   }

   @Override
   public double getDoubleValue()
   {
      return getNumberValue().doubleValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return JsonUtils.getJsonString(value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(JsonWriter writer) throws JsonException
   {
      writer.writeString(value);
   }

}
