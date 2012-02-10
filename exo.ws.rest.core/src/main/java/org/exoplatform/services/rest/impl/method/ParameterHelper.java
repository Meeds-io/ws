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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.services.rest.Property;
import org.exoplatform.services.rest.method.TypeProducer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ParameterHelper
{

   /**
    * Collections of annotation that allowed to be used on fields on any type of
    * Provider.
    * 
    * @see javax.ws.rs.ext.Provider
    * @see javax.ws.rs.ext.Providers
    */
   public static final List<String> PROVIDER_FIELDS_ANNOTATIONS;

   /**
    * Collections of annotation than allowed to be used on constructor's
    * parameters of any type of Provider.
    * 
    * @see javax.ws.rs.ext.Provider
    * @see javax.ws.rs.ext.Providers
    */
   public static final List<String> PROVIDER_CONSTRUCTOR_PARAMETER_ANNOTATIONS;

   /**
    * Collections of annotation that allowed to be used on fields of resource
    * class.
    */
   public static final List<String> RESOURCE_FIELDS_ANNOTATIONS;

   /**
    * Collections of annotation than allowed to be used on constructor's
    * parameters of resource class.
    */
   public static final List<String> RESOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS;

   /**
    * Collections of annotation than allowed to be used on method's parameters of
    * resource class.
    */
   public static final List<String> RESOURCE_METHOD_PARAMETER_ANNOTATIONS;

   static
   {
      PROVIDER_FIELDS_ANNOTATIONS =
         Collections.unmodifiableList(Arrays.asList(Context.class.getName(), Property.class.getName()));
      
      PROVIDER_CONSTRUCTOR_PARAMETER_ANNOTATIONS =
         Collections.unmodifiableList(Arrays.asList(Context.class.getName(), Property.class.getName()));
      
      List<String> tmp1 = new ArrayList<String>(7);
      tmp1.add(CookieParam.class.getName());
      tmp1.add(Context.class.getName());
      tmp1.add(HeaderParam.class.getName());
      tmp1.add(MatrixParam.class.getName());
      tmp1.add(PathParam.class.getName());
      tmp1.add(QueryParam.class.getName());
      tmp1.add(Property.class.getName());
      RESOURCE_FIELDS_ANNOTATIONS = Collections.unmodifiableList(tmp1);
      RESOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS = Collections.unmodifiableList(tmp1);

      List<String> tmp2 = new ArrayList<String>(tmp1);
      tmp2.add(FormParam.class.getName());
      RESOURCE_METHOD_PARAMETER_ANNOTATIONS = Collections.unmodifiableList(tmp2);
   }

   /**
    * @param parameterClass method parameter class
    * @param parameterType method parameter type
    * @return TypeProducer
    * @see TypeProducer
    * @see Method#getParameterTypes()
    * @see Method#getGenericParameterTypes()
    */
   static TypeProducer createTypeProducer(Class<?> parameterClass, Type parameterType)
   {

      if (parameterClass == List.class || parameterClass == Set.class || parameterClass == SortedSet.class)
      {
         // parameter is collection

         Class<?> clazz = null;
         if (parameterType != null)
            clazz = getGenericType(parameterType);
         Method methodValueOf = null;
         Constructor<?> constructor = null;

         // if not parameterized then by default collection of Strings.
         if (clazz == String.class || clazz == null)
         {
            // String

            return new CollectionStringProducer(parameterClass);

         }
         else if ((methodValueOf = getStringValueOfMethod(clazz)) != null)
         {
            // static method valueOf

            return new CollectionStringValueOfProducer(parameterClass, methodValueOf);

         }
         else if ((constructor = getStringConstructor(clazz)) != null)
         {
            // constructor with String

            return new CollectionStringConstructorProducer(parameterClass, constructor);

         }

      }
      else
      {
         // parameters is not collection
         Method methodValueOf = null;
         Constructor<?> constructor = null;

         if (parameterClass.isPrimitive())
         {
            // primitive type

            return new PrimitiveTypeProducer(parameterClass);

         }
         else if (parameterClass == String.class)
         {
            // String

            return new StringProducer();

         }
         else if ((methodValueOf = getStringValueOfMethod(parameterClass)) != null)
         {
            // static valueOf method

            return new StringValueOfProducer(methodValueOf);

         }
         else if ((constructor = getStringConstructor(parameterClass)) != null)
         {
            // constructor with String

            return new StringConstructorProducer(constructor);

         }
      }

      return null;
   }

   /**
    * The type <code>T</code> of the annotated parameter, field or property must
    * either:
    * <ol>
    * <li>Be a primitive type</li>
    * <li>Have a constructor that accepts a single <code>String</code> argument</li>
    * <li>Have a static method named <code>valueOf</code> that accepts a single
    * <code>String</code> argument (see, for example,
    * {@link Integer#valueOf(String)})</li>
    * <li>Be <code>List&lt;T&gt;</code>, <code>Set&lt;T&gt;</code> or
    * <code>SortedSet&lt;T&gt;</code>, where <code>T</code> satisfies 2 or 3
    * above. The resulting collection is read-only.</li>
    * </ol>
    * 
    * @param parameterClass the parameter class
    * @param parameterType the parameter type
    * @param annotation parameter annotation
    * @return true it parameter is valid, false otherwise
    */
   boolean isValidAnnotatedParameter(Class<?> parameterClass, Type parameterType, Annotation annotation)
   {
      if (parameterClass == List.class || parameterClass == Set.class || parameterClass == SortedSet.class)
      {

         // PathParam cann't be used on collection
         // if (annotation.annotationType() == PathParam.class)
         // return false;

         Class<?> clazz = getGenericType(parameterType);

         if (clazz == null || clazz == String.class || getStringValueOfMethod(clazz) != null
            || getStringConstructor(clazz) != null)
         {

            // parameter is collection (List, Set or SortedSet)
            return true;

         }
         else
         {

            // if primitive type
            if (parameterClass.isPrimitive() && PrimitiveTypeProducer.PRIMITIVE_TYPES_MAP.get(parameterClass) != null)
               return true;

            if (parameterClass == String.class || getStringValueOfMethod(parameterClass) != null
               || getStringConstructor(parameterClass) != null)
               return true;

         }
      }

      // not valid parameter.
      return false;
   }

   /**
    * Get static {@link Method} with single string argument and name 'valueOf'
    * for supplied class.
    * 
    * @param clazz class for discovering to have public static method with name
    *          'valueOf' and single string argument
    * @return valueOf method or null if class has not it
    */
   static Method getStringValueOfMethod(final Class<?> clazz)
   {
      try
      {
         Method method = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Method>()
         {
            public Method run() throws Exception
            {
               return clazz.getDeclaredMethod("valueOf", String.class);
            }
         });

         return Modifier.isStatic(method.getModifiers()) ? method : null;
      }
      catch (PrivilegedActionException e)
      {
         return null;
      }
   }

   /**
    * Get constructor with single string argument for supplied class.
    * 
    * @param clazz class for discovering to have constructor with single string
    *          argument
    * @return constructor or null if class has not constructor with single string
    *         argument
    */
   static Constructor<?> getStringConstructor(Class<?> clazz)
   {
      try
      {
         return clazz.getConstructor(String.class);
      }
      catch (SecurityException e)
      {
         return null;
      }
      catch (NoSuchMethodException e)
      {
         return null;
      }

   }

   /**
    * Get generic type for supplied type.
    * 
    * @param type See {@link Type}
    * @return generic type if type is {@link ParameterizedType}, null otherwise
    */
   static Class<?> getGenericType(Type type)
   {
      if (type instanceof ParameterizedType)
      {

         ParameterizedType pt = (ParameterizedType)type;
         Type[] genericTypes = pt.getActualTypeArguments();
         if (genericTypes.length == 1)
         {
            try
            {
               // if can't be cast to java.lang.Class thrown Exception
               return (Class<?>)genericTypes[0];
            }
            catch (ClassCastException e)
            {
               throw new RuntimeException("Unsupported type");
            }
         }
      }
      // not parameterized type
      return null;
   }

}
