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
package org.exoplatform.services.rest.ext.method.filter;

import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;

import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import org.exoplatform.commons.api.security.ExoCSRFProtection;


/**
 * Contract of this class thats constrains access to the resource method that
 * use JSR-250 security common annotations. See also https://jsr250.dev.java.net
 * .
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CsrfAccessFilter implements MethodInvokerFilter
{

   /**
    * Check does <tt>method</tt> contains csrf security annotations
    * CrsfExoProtection
    *
    * @see ExoCSRFProtection
    *
    */
   public void accept(GenericMethodResource method) throws WebApplicationException
   {
      for (Annotation a : method.getMethod().getAnnotations())
      {
         Class<?> ac = a.annotationType();

         if (ac == ExoCSRFProtection.class)
         {

            //Check CSRF token

            //if ok => let continue

            //if not =>
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(
                    "You do not have access rights to this resource, please contact your administrator. ").type(
                    MediaType.TEXT_PLAIN).build());


         }
      }
   }

}
