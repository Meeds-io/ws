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

import org.exoplatform.ws.frameworks.json.impl.JsonException;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: JsonParser.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public interface JsonParser
{

   /**
    * Parse given character stream and build object.  
    * @param reader the Stream Reader.
    * @param handler JsonHandler, @see {@link JsonHandler}.
    * @throws JsonException if any error occurs during parsing.
    */
   void parse(Reader reader, JsonHandler handler) throws JsonException;

   /**
    * Parse given character stream and build object.  
    * @param in the Input Stream.
    * @param handler JsonHandler, @see {@link JsonHandler}.
    * @throws JsonException if any error occurs during parsing.
    */
   void parse(InputStream in, JsonHandler handler) throws JsonException;

}
