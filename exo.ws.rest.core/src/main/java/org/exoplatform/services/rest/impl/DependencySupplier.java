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

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
            injectAnnotationClass = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Class>()
            {
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
               return getComponent(parameter.getParameterClass(), parameter.getGenericType());
         return null;
      }
      // Annotation required for fields only.
      return getComponent(parameter.getParameterClass(), parameter.getGenericType());
   }

   @SuppressWarnings({"rawtypes"})
   protected Object getComponent(Class<?> parameterClass, Type genericType)
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      List injectionProviders = container.getComponentInstancesOfType(javax.inject.Provider.class);
      if (injectionProviders != null && injectionProviders.size() > 0)
      {
         for (Iterator i = injectionProviders.iterator(); i.hasNext();)
         {
            javax.inject.Provider provider = (javax.inject.Provider)i.next();
            Type injectedType = resolveInjectedType(provider.getClass());
            if (injectedType != null)
            {
               if (javax.inject.Provider.class == parameterClass)
               {
                  if (genericType instanceof ParameterizedType)
                  {
                     Type[] parameterActualTypes = ((ParameterizedType)genericType).getActualTypeArguments();
                     if (parameterActualTypes.length > 0)
                     {
                        if (parameterActualTypes[0] instanceof Class<?>)
                        {
                           Class<?> actualType = (Class<?>)parameterActualTypes[0];
                           if (actualType == injectedType) //NOSONAR
                           {
                              return provider;
                           }
                        }
                        else if (parameterActualTypes[0] instanceof ParameterizedType)
                        {
                           ParameterizedType actualType = (ParameterizedType)parameterActualTypes[0];
                           if (actualType.equals(injectedType))
                              return provider;
                        }
                     }
                  }
               }
               else
               {
                  if (injectedType instanceof Class<?>)
                  {
                     if (parameterClass.isAssignableFrom((Class<?>)injectedType))
                        return provider.get();
                  }
                  else if (injectedType instanceof ParameterizedType)
                  {
                     ParameterizedType pType = (ParameterizedType)injectedType;
                     Type rawType = pType.getRawType();
                     if (rawType instanceof Class<?>)
                     {
                        if (parameterClass.isAssignableFrom((Class<?>)rawType))
                           return provider.get();
                     }
                  }
               }
            }
         }
      }
      // Directly look up component in container by class if it is not javax.inject.Provider.
      if (!javax.inject.Provider.class.isAssignableFrom(parameterClass))
         return container.getComponentInstanceOfType(parameterClass);
      return null;
   }

   private Type resolveInjectedType(final Class<?> providerClass)
   {
      Method get = null;
      Type injectedType = null;
      try
      {
         get = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Method>()
         {
            public Method run() throws NoSuchMethodException
            {
               return providerClass.getMethod("get");
            }
         });
      }
      catch (PrivilegedActionException pe)
      {
         NoSuchMethodException c = (NoSuchMethodException)pe.getCause();
         // Should never happen since class implements javax.inject.Provider.
         throw new RuntimeException(c.getMessage());
      }

      if (get != null)
         injectedType = get.getGenericReturnType();

      return injectedType;
   }
}
