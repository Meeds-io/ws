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
package org.exoplatform.ws.frameworks.json.impl;

import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils.Types;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.exoplatform.ws.frameworks.json.value.impl.ArrayValue;
import org.exoplatform.ws.frameworks.json.value.impl.BooleanValue;
import org.exoplatform.ws.frameworks.json.value.impl.DoubleValue;
import org.exoplatform.ws.frameworks.json.value.impl.LongValue;
import org.exoplatform.ws.frameworks.json.value.impl.NullValue;
import org.exoplatform.ws.frameworks.json.value.impl.ObjectValue;
import org.exoplatform.ws.frameworks.json.value.impl.StringValue;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: JsonGeneratorImpl.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public class JsonGeneratorImpl implements JsonGenerator
{

   static final Collection<String> SKIP_METHODS = new HashSet<String>();

   static
   {
      // Prevent discovering of Java class.
      SKIP_METHODS.add("getClass");
      // Since we need support for Groovy must skip this.
      // All "Groovy Objects" implements interface groovy.lang.GroovyObject
      // and has method getMetaClass. Not need to discover it.
      SKIP_METHODS.add("getMetaClass");
   }

   /**
    * Create JSON array from specified collection.
    *
    * @param collection source collection
    * @return JSON representation of collection
    * @throws JsonException if collection can't be transformed in JSON
    *         representation
    */
   public JsonValue createJsonArray(Collection<?> collection) throws JsonException
   {
      if (collection == null)
         return new NullValue();

      JsonValue jsonArray = new ArrayValue();
      for (Object o : collection)
      {
         // If :
         // 1. Known types (primitive, String, array of primitive or String)
         // 2. Array of any object (expect for Java Bean)
         // 3. Collection<?>
         // 4. Map<String, ?>
         if (JsonUtils.getType(o) != null)
            jsonArray.addElement(createJsonValue(o));
         else
            jsonArray.addElement(createJsonObject(o));
      }
      return jsonArray;
   }

   /**
    * Create JSON array from specified object. Parameter <code>array</code> must
    * be array.
    *
    * @param array source array
    * @return JSON representation of array
    * @throws JsonException if array can't be transformed in JSON representation
    */
   public JsonValue createJsonArray(Object array) throws JsonException
   {
      if (array == null)
         return new NullValue();

      Types t = JsonUtils.getType(array);
      JsonValue jsonArray = new ArrayValue();
      int length = Array.getLength(array);
      if (t == Types.ARRAY_BOOLEAN)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new BooleanValue(Array.getBoolean(array, i)));
      }
      else if (t == Types.ARRAY_BYTE)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new LongValue(Array.getByte(array, i)));
      }
      else if (t == Types.ARRAY_SHORT)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new LongValue(Array.getShort(array, i)));
      }
      else if (t == Types.ARRAY_INT)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new LongValue(Array.getInt(array, i)));
      }
      else if (t == Types.ARRAY_LONG)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new LongValue(Array.getLong(array, i)));
      }
      else if (t == Types.ARRAY_FLOAT)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new DoubleValue(Array.getFloat(array, i)));
      }
      else if (t == Types.ARRAY_DOUBLE)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new DoubleValue(Array.getDouble(array, i)));
      }
      else if (t == Types.ARRAY_CHAR)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new StringValue(Character.toString(Array.getChar(array, i))));
      }
      else if (t == Types.ARRAY_STRING)
      {
         for (int i = 0; i < length; i++)
            jsonArray.addElement(new StringValue((String)Array.get(array, i)));
      }
      else if (t == Types.ARRAY_OBJECT)
      {
         for (int i = 0; i < length; i++)
         {
            Object el = Array.get(array, i);
            // If :
            // 1. Known types (primitive, String, array of primitive or String)
            // 2. Array of any object (expect for Java Bean)
            // 3. Collection<?>
            // 4. Map<String, ?>
            if (JsonUtils.getType(el) != null)
               jsonArray.addElement(createJsonValue(el));
            else
               jsonArray.addElement(createJsonObject(el));
         }
      }
      else
      {
         throw new JsonException("Invalid argument, must be array.");
      }
      return jsonArray;
   }

   /**
    * Create JSON object from specified map.
    *
    * @param map source map
    * @return JSON representation of map
    * @throws JsonException if map can't be transformed in JSON representation
    */
   public JsonValue createJsonObject(Map<String, Object> map) throws JsonException
   {
      if (map == null)
         return new NullValue();

      JsonValue jsonObject = new ObjectValue();
      Set<String> keys = map.keySet();
      for (String k : keys)
      {
         Object o = map.get(k);
         // If :
         // 1. Known types (primitive, String, array of primitive or String)
         // 2. Array of any object (expect for Java Bean)
         // 3. Collection<?>
         // 4. Map<String, ?>
         if (JsonUtils.getType(o) != null)
            jsonObject.addElement(k, createJsonValue(o));
         else
            jsonObject.addElement(k, createJsonObject(o));
      }
      return jsonObject;
   }

   /**
    * {@inheritDoc}
    */
   public JsonValue createJsonObject(Object object) throws JsonException
   {
      Method[] methods = object.getClass().getMethods();

      List<String> transientFields = getTransientFields(object.getClass());

      JsonValue jsonRootValue = new ObjectValue();

      for (Method method : methods)
      {
         String methodName = method.getName();

         /*
          * Method must be as follow:
          * 1. Name starts from "get" plus at least one character or starts from
          * "is" plus at least one more character and return boolean type
          * 2. Must be without parameters
          * 3. Must not be in list of skipped methods
          */

         String key = null;
         if (!SKIP_METHODS.contains(methodName) && method.getParameterTypes().length == 0)
         {
            if (methodName.startsWith("get") && methodName.length() > 3)
            {
               key = methodName.substring(3);
            }
            else if (methodName.startsWith("is") && methodName.length() > 2
               && (method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class))
            {
               key = methodName.substring(2);
            }
         }

         if (key != null)
         {
            // First letter of key to lower case.
            key = (key.length() > 1) ? Character.toLowerCase(key.charAt(0)) + key.substring(1) : key.toLowerCase();
            // Check is this field in list of transient field.
            if (!transientFields.contains(key))
            {
               try
               {
                  // Get result of invoke method get...
                  Object invokeResult = method.invoke(object, new Object[0]);

                  // If :
                  // 1. Known types (primitive, String, array of primitive or String)
                  // 2. Array of any object (expect for Java Bean)
                  // 3. Collection<?>
                  // 4. Map<String, ?>
                  if (JsonUtils.getType(invokeResult) != null)
                  {
                     jsonRootValue.addElement(key, createJsonValue(invokeResult));
                  }
                  else
                  {
                     jsonRootValue.addElement(key, createJsonObject(invokeResult));
                  }

               }
               catch (InvocationTargetException e)
               {
                  throw new JsonException(e);
               }
               catch (IllegalAccessException e)
               {
                  throw new JsonException(e);
               }
            }
         }
      }
      return jsonRootValue;
   }

   /**
    * Create JsonValue corresponding to Java object.
    *
    * @param object source object.
    * @return JsonValue.
    * @throws JsonException if any errors occurs.
    */
   @SuppressWarnings("unchecked")
   private JsonValue createJsonValue(Object object) throws JsonException
   {
      Types t = JsonUtils.getType(object);
      switch (t)
      {
         case NULL :
            return new NullValue();
         case BOOLEAN :
            return new BooleanValue((Boolean)object);
         case BYTE :
            return new LongValue((Byte)object);
         case SHORT :
            return new LongValue((Short)object);
         case INT :
            return new LongValue((Integer)object);
         case LONG :
            return new LongValue((Long)object);
         case FLOAT :
            return new DoubleValue((Float)object);
         case DOUBLE :
            return new DoubleValue((Double)object);
         case CHAR :
            return new StringValue(Character.toString((Character)object));
         case STRING :
            return new StringValue((String)object);
         case ENUM :
            return new StringValue(((Enum)object).name());
         case ARRAY_BOOLEAN : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new BooleanValue(Array.getBoolean(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_BYTE : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new LongValue(Array.getByte(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_SHORT : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new LongValue(Array.getShort(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_INT : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new LongValue(Array.getInt(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_LONG : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new LongValue(Array.getLong(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_FLOAT : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new DoubleValue(Array.getFloat(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_DOUBLE : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new DoubleValue(Array.getDouble(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_CHAR : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new StringValue(Character.toString(Array.getChar(object, i))));
            }
            return jsonArray;
         }
         case ARRAY_STRING : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               jsonArray.addElement(new StringValue((String)Array.get(object, i)));
            }
            return jsonArray;
         }
         case ARRAY_OBJECT : {
            JsonValue jsonArray = new ArrayValue();
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++)
            {
               Object el = Array.get(object, i);
               if (JsonUtils.getType(el) != null)
               {
                  jsonArray.addElement(createJsonValue(el));
               }
               else
               {
                  jsonArray.addElement(createJsonObject(el));
               }
            }

            return jsonArray;
         }
         case COLLECTION : {
            JsonValue jsonArray = new ArrayValue();
            List<Object> list = new ArrayList<Object>((Collection<?>)object);
            for (Object o : list)
            {
               if (JsonUtils.getType(o) != null)
               {
                  jsonArray.addElement(createJsonValue(o));
               }
               else
               {
                  jsonArray.addElement(createJsonObject(o));
               }
            }

            return jsonArray;
         }
         case MAP :
            JsonValue jsonObject = new ObjectValue();
            Map<String, Object> map = new HashMap<String, Object>((Map<String, Object>)object);
            Set<String> keys = map.keySet();
            for (String k : keys)
            {
               Object o = map.get(k);
               if (JsonUtils.getType(o) != null)
               {
                  jsonObject.addElement(k, createJsonValue(o));
               }
               else
               {
                  jsonObject.addElement(k, createJsonObject(o));
               }
            }

            return jsonObject;
         default :
            // Must not be here!
            return null;
      }

   }

   /**
    * Check fields in class which marked as 'transient'. Transient fields will
    * be not serialized in JSON representation.
    *
    * @param clazz the class.
    * @return list of fields which must be skiped.
    */
   private List<String> getTransientFields(Class<?> clazz)
   {
      List<String> l = new ArrayList<String>();
      Field[] fields = clazz.getDeclaredFields();
      for (Field f : fields)
      {
         if (Modifier.isTransient(f.getModifiers()))
            l.add(f.getName());
      }
      return l;
   }

}
