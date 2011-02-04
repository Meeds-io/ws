/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.Parameter;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;

/**
 * Provide objects that required for constructors or fields of Resource or
 * Provider.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DependencySupplier
{
   protected final Class<? extends Annotation> injectAnnotationClass;

   protected DependencySupplier(Class<? extends Annotation> injectAnnotationClass)
   {
      this.injectAnnotationClass = injectAnnotationClass;
   }

   // Default.
   public DependencySupplier()
   {
      this(javax.inject.Inject.class);
   }

   // For eXo Container usage.
   public DependencySupplier(InitParams params)
   {
      this(findInjectAnnotationClass(params));
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private static Class<? extends Annotation> findInjectAnnotationClass(InitParams params)
   {
      Class<? extends Annotation> injectAnnotationClass = null;
      if (params != null)
      {
         final ValueParam injectAnnotationParameter = params.getValueParam("inject.annotation.class");
         try
         {
            injectAnnotationClass = AccessController.doPrivileged(new PrivilegedExceptionAction<Class>() {
               public Class run() throws ClassNotFoundException
               {
                  return Thread.currentThread().getContextClassLoader().loadClass(injectAnnotationParameter.getValue());
               }
            });
         }
         catch (PrivilegedActionException pe)
         {
            ClassNotFoundException c = (ClassNotFoundException)pe.getCause();
            throw new RuntimeException(c.getMessage());
         }
      }
      if (injectAnnotationClass == null)
         injectAnnotationClass = javax.inject.Inject.class;
      return injectAnnotationClass;
   }

   /**
    * Instance for initialization <code>parameter</code>.
    * 
    * @param parameter parameter
    * @return instance or <code>null</code> if required instance can't be
    *         provided
    */
   public final Object getComponent(Parameter parameter)
   {
      if (parameter instanceof FieldInjector)
      {
         for (Annotation a : parameter.getAnnotations())
            if (injectAnnotationClass.isInstance(a))
               return getComponent(parameter.getParameterClass());
         return null;
      }
      // Annotation required for fields only.
      return getComponent(parameter.getParameterClass());
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   protected Object getComponent(Class<?> parameterClass)
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      List injectionProviders = container.getComponentInstancesOfType(InjectionProvider.class);
      if (injectionProviders != null && injectionProviders.size() > 0)
      {
         for (Iterator i = injectionProviders.iterator(); i.hasNext();)
         {
            InjectionProvider provider = (InjectionProvider)i.next();
            if (provider.isSupported(parameterClass))
               return javax.inject.Provider.class.isAssignableFrom(parameterClass) ? provider : provider.get();
         }
      }
      // Directly look up component in container by class,
      return container.getComponentInstanceOfType(parameterClass);
   }
}
