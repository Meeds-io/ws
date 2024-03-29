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

package org.exoplatform.services.rest.impl;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Transform {@link java.lang.Exception} to JAX-RS response.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DefaultExceptionMapper implements ExceptionMapper<Exception>
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger(DefaultExceptionMapper.class);

   /**
    * {@inheritDoc}
    */
   public Response toResponse(Exception exception)
   {
      if (!(exception instanceof WebApplicationException || exception.getCause() instanceof WebApplicationException)) {
        LOG.warn("Uncaught REST Service Exception", exception);
      }

      String message = exception.getMessage();
      return Response.status(500).entity(message == null ? exception.getClass().getName() : message).type(
                                                                                                          MediaType.TEXT_PLAIN)
                     .build();
   }

}
