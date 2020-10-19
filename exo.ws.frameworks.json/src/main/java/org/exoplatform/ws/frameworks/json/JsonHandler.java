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
package org.exoplatform.ws.frameworks.json;

import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: JsonHandler.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public interface JsonHandler
{

   /**
    * This method will be called by JSONParser when '{' found.
    */
   void startObject();

   /**
    * This method will be called by JSONParser when '}' found.
    */
   void endObject();

   /**
    * This method will be called by JSONParser when '[' found.
    */
   void startArray();

   /**
    * This method will be called by JSONParser when ']' found.
    */
   void endArray();

   /**
    * The key name found in the input JSON stream.
    * @param key the key.
    */
   void key(String key);

   /**
    * Characters set found, it can be any characters. 
    * @param characters the array of characters.
    */
   void characters(char[] characters);

   /**
    * @return return Json Object.
    */
   JsonValue getJsonObject();

}
