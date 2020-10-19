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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.commons.utils.ClassLoading;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * For injection JAXBContext from configuration at startup in
 * {@link JAXBContextResolver}.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JAXBContextComponentPlugin extends BaseComponentPlugin
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.JAXBContextComponentPlugin");

   /**
    * Set of classes that will be bounded.
    */
   private final Set<Class<?>> jcs = new HashSet<Class<?>>();

   /**
    * @param params initialize parameters
    * @see InitParams
    */
   public JAXBContextComponentPlugin(InitParams params)
   {
      if (params != null)
      {
         Iterator<ValueParam> i = params.getValueParamIterator();
         while (i.hasNext())
         {
            ValueParam v = i.next();
            try
            {
               jcs.add(ClassLoading.forName(v.getValue(), this));
            }
            catch (ClassNotFoundException e)
            {
               LOG.warn("Failed load class " + v.getValue(), e);
            }
         }
      }
   }

   /**
    * @return collection of classes to be bound
    */
   public Set<Class<?>> getJAXBContexts()
   {
      return jcs;
   }

}
