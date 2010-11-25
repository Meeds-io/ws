/**
 * Copyright (C) 2010 eXo Platform SAS.
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
import org.exoplatform.services.rest.method.MethodInvokerFilter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ExceptionMapperComponentPlugin extends BaseComponentPlugin
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.ExceptionMapperComponentPlugin");

   /** List of {@link MethodInvokerFilter}. */
   private Set<Class<? extends ExceptionMapper<?>>> emaps = new HashSet<Class<? extends ExceptionMapper<?>>>();

   /**
    * @param params initialize parameters from configuration
    * @see InitParams
    */
   @SuppressWarnings("unchecked")
   public ExceptionMapperComponentPlugin(InitParams params)
   {
      if (params != null)
      {
         Iterator<ValueParam> i = params.getValueParamIterator();
         while (i.hasNext())
         {
            ValueParam v = i.next();
            try
            {
               emaps.add((Class<? extends ExceptionMapper<?>>)Class.forName(v.getValue()));
            }
            catch (ClassNotFoundException e)
            {
               LOG.error("Failed load class " + v.getValue(), e);
            }
         }
      }
   }

   /**
    * @return get collection of classes ExceptionMapper supplied from
    *         configuration
    */
   public Set<Class<? extends ExceptionMapper<?>>> getExceptionMappers()
   {
      return emaps;
   }
}
