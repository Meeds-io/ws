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
package org.exoplatform.services.rest.impl;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.RequestHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * For injection {@link RequestFilter} in {@link RequestHandler} at startup.
 * 
 * @see RequestHandlerImpl
 * @see RequestFilter
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestFilterComponentPlugin extends BaseComponentPlugin
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.RequestFilterComponentPlugin");

   /**
    * See {@link RequestFilter}.
    */
   private Set<Class<? extends RequestFilter>> fs = new HashSet<Class<? extends RequestFilter>>();

   /**
    * @param params initialize parameters from configuration.
    * @see InitParams
    */
   @SuppressWarnings("unchecked")
   public RequestFilterComponentPlugin(InitParams params)
   {
      if (params != null)
      {
         Iterator<ValueParam> i = params.getValueParamIterator();
         while (i.hasNext())
         {
            ValueParam v = i.next();
            try
            {
               fs.add((Class<? extends RequestFilter>)Class.forName(v.getValue()));
            }
            catch (ClassNotFoundException e)
            {
               LOG.error("Failed load class " + v.getValue(), e);
            }
         }
      }
   }

   /**
    * @return Collection of classes RequestFilter supplied in configuration.
    */
   public Set<Class<? extends RequestFilter>> getFilters()
   {
      return fs;
   }

}
