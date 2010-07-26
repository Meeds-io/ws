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

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: JsonUtils.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public final class JsonUtils
{

   /** Must not be created. */
   private JsonUtils()
   {
   }

   static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

   public enum Types {

      /** Byte. */
      BYTE,

      /** Short. */
      SHORT,

      /** Integer. */
      INT,

      /** Long. */
      LONG,

      /** Float. */
      FLOAT,

      /** Double. */
      DOUBLE,

      /** Boolean. */
      BOOLEAN,

      /** Char. */
      CHAR,

      /** String. */
      STRING,

      /** Corresponding to null value. */
      NULL,

      /** Array of bytes. */
      ARRAY_BYTE,

      /** Array of shorts. */
      ARRAY_SHORT,

      /** Array of ints. */
      ARRAY_INT,

      /** Array of longs. */
      ARRAY_LONG,

      /** Array of floats. */
      ARRAY_FLOAT,

      /** Array of doubles. */
      ARRAY_DOUBLE,

      /** Array of booleans. */
      ARRAY_BOOLEAN,

      /** Array of chars. */
      ARRAY_CHAR,

      /** Array of strings. */
      ARRAY_STRING,

      /** Array of Java Objects (beans). */
      ARRAY_OBJECT,

      /** Collection. */
      COLLECTION,

      /** Map. */
      MAP,

      /**
       * Enum.
       */
      ENUM
   }

   /**
    * Types of Json tokens.
    */
   public enum JsonToken {
      /** JSON object, "key":{value1, ... } . */
      object,

      /** JSON array "key":[value1, ... ] . */
      array,

      /** Key. */
      key,

      /** Value. */
      value
   }

   /**
    * Map of known types.
    */
   private static final Map<String, Types> KNOWN_TYPES = new HashMap<String, Types>();

   static
   {
      // wrappers for primitive types
      KNOWN_TYPES.put(Boolean.class.getName(), Types.BOOLEAN);

      KNOWN_TYPES.put(Byte.class.getName(), Types.BYTE);
      KNOWN_TYPES.put(Short.class.getName(), Types.SHORT);
      KNOWN_TYPES.put(Integer.class.getName(), Types.INT);
      KNOWN_TYPES.put(Long.class.getName(), Types.LONG);
      KNOWN_TYPES.put(Float.class.getName(), Types.FLOAT);
      KNOWN_TYPES.put(Double.class.getName(), Types.DOUBLE);

      KNOWN_TYPES.put(Character.class.getName(), Types.CHAR);
      KNOWN_TYPES.put(String.class.getName(), Types.STRING);

      // primitive types
      KNOWN_TYPES.put("boolean", Types.BOOLEAN);

      KNOWN_TYPES.put("byte", Types.BYTE);
      KNOWN_TYPES.put("short", Types.SHORT);
      KNOWN_TYPES.put("int", Types.INT);
      KNOWN_TYPES.put("long", Types.LONG);
      KNOWN_TYPES.put("float", Types.FLOAT);
      KNOWN_TYPES.put("double", Types.DOUBLE);

      KNOWN_TYPES.put("char", Types.CHAR);

      KNOWN_TYPES.put("null", Types.NULL);

      // arrays
      KNOWN_TYPES.put(new boolean[0].getClass().getName(), Types.ARRAY_BOOLEAN);

      KNOWN_TYPES.put(new byte[0].getClass().getName(), Types.ARRAY_BYTE);
      KNOWN_TYPES.put(new short[0].getClass().getName(), Types.ARRAY_SHORT);
      KNOWN_TYPES.put(new int[0].getClass().getName(), Types.ARRAY_INT);
      KNOWN_TYPES.put(new long[0].getClass().getName(), Types.ARRAY_LONG);
      KNOWN_TYPES.put(new double[0].getClass().getName(), Types.ARRAY_DOUBLE);
      KNOWN_TYPES.put(new float[0].getClass().getName(), Types.ARRAY_FLOAT);

      KNOWN_TYPES.put(new char[0].getClass().getName(), Types.ARRAY_CHAR);
      KNOWN_TYPES.put(new String[0].getClass().getName(), Types.ARRAY_STRING);

   }

   /**
    * Transform Java String to JSON string.
    * 
    * @param string source String.
    * @return result.
    */
   public static String getJsonString(String string)
   {
      if (string == null || string.length() == 0)
         return "\"\"";
      StringBuffer sb = new StringBuffer();
      sb.append("\"");
      char[] charArray = string.toCharArray();
      for (char c : charArray)
      {
         switch (c)
         {
            case '\n' :
               sb.append("\\n");
               break;
            case '\r' :
               sb.append("\\r");
               break;
            case '\t' :
               sb.append("\\t");
               break;
            case '\b' :
               sb.append("\\b");
               break;
            case '\f' :
               sb.append("\\f");
               break;
            case '\\' :
               sb.append("\\\\");
               break;
            case '"' :
               sb.append("\\\"");
               break;
            default :
               if (c < '\u0010')
                  sb.append("\\u000" + Integer.toHexString(c));
               else if ((c < '\u0020' && c > '\u0009') || (c >= '\u0080' && c < '\u00a0'))
                  sb.append("\\u00" + Integer.toHexString(c));
               else if (c >= '\u2000' && c < '\u2100')
                  sb.append("\\u" + Integer.toHexString(c));
               else
                  sb.append(c);
               break;
         }
      }
      sb.append("\"");
      return sb.toString();
   }

   /**
    * Check is given object has on of types:
    * <ol>
    * <li><code>null</code></li>
    * <li>Primitive type</li>
    * <li>Primitive type wrapper</li>
    * <li>String</li>
    * <li>Array of T where T satisfies 2 or 3 or 4</>
    * </ol>
    * 
    * @param o Object
    * @return <code>true</code> if Object has one of described above type,
    * <code>false</code> otherwise
    */
   public static boolean isKnownType(Object o)
   {
      return o == null || isKnownType(o.getClass());
   }

   /**
    * Check is given class object represents:
    * <ol>
    * <li>Primitive type</li>
    * <li>Primitive type wrapper</li>
    * <li>String</li>
    * <li>Array of T where T satisfies 1 or 2 or 3</>
    * </ol>
    * 
    * @param clazz class.
    * @return <code>true</code> if class object represent one of described
    * above, <code>false</code> otherwise
    */
   public static boolean isKnownType(Class<?> clazz)
   {
      return KNOWN_TYPES.get(clazz.getName()) != null;
   }

   /**
    * Get corresponding {@link Types} for specified Object. If object is NOT :
    * <ol>
    * <li>Known type (primitive, String, array of primitive or String)</li>
    * <li>Array</li>
    * <li>Collection&lt;?&gt;</li>
    * <li>Map&lt;String, ?&gt;</li>
    * </ol>
    * then <code>null</null> will be returned
    * 
    * @param o Object.
    * @return {@link Types} or <code>null</code> (see above)
    * @see {@link KNOWN_TYPES}.
    */
   public static Types getType(Object o)
   {
      if (o == null)
         return Types.NULL;
      if (KNOWN_TYPES.get(o.getClass().getName()) != null)
         return KNOWN_TYPES.get(o.getClass().getName());
      if (o instanceof Enum)
         return Types.ENUM;
      if (o instanceof Object[])
         return Types.ARRAY_OBJECT;
      if (o instanceof Collection)
         return Types.COLLECTION;
      if (o instanceof Map)
         return Types.MAP;
      return null;
   }

   /**
    * Get corresponding {@link Types} for specified class. If class object is
    * NOT :
    * <ol>
    * <li>Known type (primitive, String, array of primitive or String)</li>
    * <li>Array</li>
    * <li>Collection</li>
    * <li>Map</li>
    * </ol>
    * then <code>null</null> will be returned
    * 
    * @param o Object.
    * @return {@link Types} or <code>null</code> (see above)
    * @see {@link KNOWN_TYPES}.
    */
   public static Types getType(Class<?> clazz)
   {
      if (KNOWN_TYPES.get(clazz.getName()) != null)
         return KNOWN_TYPES.get(clazz.getName());
      if (Enum.class.isAssignableFrom(clazz))
         return Types.ENUM;
      if (clazz.isArray())
         return Types.ARRAY_OBJECT;
      if (Collection.class.isAssignableFrom(clazz))
         return Types.COLLECTION;
      if (Map.class.isAssignableFrom(clazz))
         return Types.MAP;
      return null;
   }
}
