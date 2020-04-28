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
package org.exoplatform.services.rest;

import java.io.IOException;

import javax.ws.rs.ext.MessageBodyWriter;

/**
 * All implementation of this interface should be able to write data in
 * container response, e. g. servlet response.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ContainerResponseWriter
{

   /**
    * Write HTTP status and headers in HTTP response.
    * 
    * @param response container response
    * @throws IOException if any i/o error occurs
    */
   void writeHeaders(GenericContainerResponse response) throws IOException;

   /**
    * Write entity body in output stream.
    * 
    * @param response container response
    * @param entityWriter See {@link MessageBodyWriter}
    * @throws IOException if any i/o error occurs
    */
   @SuppressWarnings("unchecked")
   void writeBody(GenericContainerResponse response, MessageBodyWriter entityWriter) throws IOException;

}
