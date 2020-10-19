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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.commons.utils.ClassLoading;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.method.MethodInvokerFilter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * For injection {@link MethodInvokerFilter} in {@link RequestHandler} at
 * startup.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MethodInvokerFilterComponentPlugin extends BaseComponentPlugin
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.MethodInvokerFilterComponentPlugin");

   /**
    * List of {@link MethodInvokerFilter}.
    */
   private Set<Class<? extends MethodInvokerFilter>> mifs = new HashSet<Class<? extends MethodInvokerFilter>>();

   /**
    * @param params initialize parameters from configuration
    * @see InitParams
    */
   @SuppressWarnings("unchecked")
   public MethodInvokerFilterComponentPlugin(InitParams params)
   {
      if (params != null)
      {
         Iterator<ValueParam> i = params.getValueParamIterator();
         while (i.hasNext())
         {
            ValueParam v = i.next();
            try
            {
               mifs.add((Class<? extends MethodInvokerFilter>)ClassLoading.forName(v.getValue(), this));
            }
            catch (ClassNotFoundException e)
            {
               LOG.error("Failed load class " + v.getValue(), e);
            }
         }
      }
   }

   /**
    * @return get collection of classes MethodInvokerFilter supplied from
    *         configuration
    */
   public Set<Class<? extends MethodInvokerFilter>> getFilters()
   {
      return mifs;
   }

}
