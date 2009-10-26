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
package org.exoplatform.services.rest.tools;

import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.GenericContainerResponse;

import java.io.IOException;

import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Mock object than can be used for any test when we don't care about response
 * entity at all.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DummyContainerResponseWriter implements ContainerResponseWriter
{

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void writeBody(GenericContainerResponse response, MessageBodyWriter entityWriter) throws IOException
   {
   }

   /**
    * {@inheritDoc}
    */
   public void writeHeaders(GenericContainerResponse response) throws IOException
   {
   }

}
