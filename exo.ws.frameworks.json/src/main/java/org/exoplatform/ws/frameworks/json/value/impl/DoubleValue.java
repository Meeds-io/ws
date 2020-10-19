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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: DoubleValue.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public class DoubleValue extends NumericValue
{

   /**
    * Value.
    */
   private final double value;

   /**
    * Constructs new DoubleValue.
    * @param value the value.
    */
   public DoubleValue(double value)
   {
      this.value = value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDouble()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getStringValue()
   {
      return Double.toString(value);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte getByteValue()
   {
      return (byte)value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public short getShortValue()
   {
      return (short)value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getIntValue()
   {
      return (int)value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long getLongValue()
   {
      return (long)value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public float getFloatValue()
   {
      return (float)value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public double getDoubleValue()
   {
      return value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getStringValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(JsonWriter writer) throws JsonException
   {
      writer.writeValue(value);
   }

}
