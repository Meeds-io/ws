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

import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;
import org.exoplatform.services.rest.servlet.ServletContainerRequest;
import org.exoplatform.web.security.csrf.CSRFTokenUtil;
import org.exoplatform.web.security.csrf.ExoCSRFProtection;

import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;



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

            //get token in context
            ServletContainerRequest request = (ServletContainerRequest) ApplicationContextImpl.getCurrent().getContainerRequest();
            HttpSession session = request.getServletRequest().getSession();

            if (!CSRFTokenUtil.check(request.getServletRequest())) {
               throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity(
                       "You do not have access rights to this resource, please contact your administrator. ").type(
                       MediaType.TEXT_PLAIN).build());
            }

         }
      }
   }

}
