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
package org.exoplatform.services.rest.impl.provider;

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Provide cache for {@link JAXBContext}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML})
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML, MediaTypeHelper.WADL})
public class JAXBContextResolver implements ContextResolver<JAXBContextResolver>
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.JAXBContextResolver");

   /**
    * JAXBContext cache.
    */
   @SuppressWarnings("unchecked")
   private final ConcurrentHashMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

   /**
    * {@inheritDoc}
    */
   public JAXBContextResolver getContext(Class<?> type)
   {
      return this;
   }

   /**
    * Return JAXBContext according to supplied type. If no one context found then
    * try create new context and save it in cache.
    *
    * @param classes classes to be bound
    * @return JAXBContext
    * @throws JAXBException if JAXBContext creation failed
    */
   public JAXBContext getJAXBContext(final Class<?> clazz) throws JAXBException
   {
      JAXBContext jaxbctx = jaxbContexts.get(clazz);
      if (jaxbctx == null)
      {
         try
         {
            jaxbctx = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<JAXBContext>()
            {
               public JAXBContext run() throws Exception
               {
                  return JAXBContext.newInstance(clazz);
               }
            });
         }
         catch (PrivilegedActionException pae)
         {
            Throwable cause = pae.getCause();
            if (cause instanceof JAXBException)
            {
               throw (JAXBException)cause;
            }
            else if (cause instanceof RuntimeException)
            {
               throw (RuntimeException)cause;
            }
            else
            {
               throw new RuntimeException(cause);
            }
         }

         jaxbContexts.put(clazz, jaxbctx);
      }
      return jaxbctx;
   }

   /**
    * Create and add in cache JAXBContext for supplied set of classes.
    *
    * @param classes set of java classes to be bound
    * @return JAXBContext
    * @throws JAXBException if JAXBContext for supplied classes can't be created
    *           in any reasons
    */
   public JAXBContext createJAXBContext(final Class<?> clazz) throws JAXBException
   {
      JAXBContext jaxbctx;

      try
      {
         jaxbctx = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<JAXBContext>()
         {
            public JAXBContext run() throws Exception
            {
               return JAXBContext.newInstance(clazz);
            }
         });
      }
      catch (PrivilegedActionException pae)
      {
         Throwable cause = pae.getCause();
         if (cause instanceof JAXBException)
         {
            throw (JAXBException)cause;
         }
         else if (cause instanceof RuntimeException)
         {
            throw (RuntimeException)cause;
         }
         else
         {
            throw new RuntimeException(cause);
         }
      }

      addJAXBContext(jaxbctx, clazz);
      return jaxbctx;
   }

   /**
    * Add prepared JAXBContext that will be mapped to set of class. In this case
    * this class works as cache for JAXBContexts.
    *
    * @param jaxbctx JAXBContext
    * @param classes set of java classes to be bound
    */
   public void addJAXBContext(JAXBContext jaxbctx, Class<?> clazz)
   {
      jaxbContexts.put(clazz, jaxbctx);
   }

   /**
    * @param plugin for injection prepared JAXBContext at startup
    */
   public void addPlugin(ComponentPlugin plugin)
   {
      if (plugin instanceof JAXBContextComponentPlugin)
      {
         for (Iterator<Class<?>> i = ((JAXBContextComponentPlugin)plugin).getJAXBContexts().iterator(); i.hasNext();)
         {
            Class<?> c = i.next();
            try
            {
               createJAXBContext(c);
               //System.out.printf("\nContext for class: {%s}\n\n ", c);
            }
            catch (JAXBException e)
            {
               LOG.error("Failed add JAXBContext for class " + c.getName(), e);
            }
         }
      }
   }

}
