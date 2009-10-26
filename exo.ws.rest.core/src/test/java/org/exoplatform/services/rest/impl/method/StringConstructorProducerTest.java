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

import junit.framework.TestCase;

import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.method.TypeProducer;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StringConstructorProducerTest extends TestCase
{

   public void testByte() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Byte.class.getConstructor(String.class));
      assertEquals(new Byte("127"), StringConstructorProducer.createValue("127"));
   }

   public void testShort() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Short.class.getConstructor(String.class));
      assertEquals(new Short("32767"), StringConstructorProducer.createValue("32767"));
   }

   public void testInteger() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Integer.class.getConstructor(String.class));
      assertEquals(new Integer("2147483647"), StringConstructorProducer.createValue("2147483647"));
   }

   public void testLong() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Long.class.getConstructor(String.class));
      assertEquals(new Long("9223372036854775807"), StringConstructorProducer.createValue("9223372036854775807"));
   }

   public void testFloat() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Float.class.getConstructor(String.class));
      assertEquals(new Float("1.23456789"), StringConstructorProducer.createValue("1.23456789"));
   }

   public void testDouble() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Double.class.getConstructor(String.class));
      assertEquals(new Double("1.234567898765432"), StringConstructorProducer.createValue("1.234567898765432"));
   }

   public void testBoolean() throws Exception
   {
      StringConstructorProducer StringConstructorProducer =
         new StringConstructorProducer(Boolean.class.getConstructor(String.class));
      assertEquals(new Boolean("true"), StringConstructorProducer.createValue("true"));
   }

   public void testCuctomTypeStringConstructor() throws Exception
   {
      TypeProducer t = ParameterHelper.createTypeProducer(StringConstructor.class, null);
      MultivaluedMap<String, String> values = new MultivaluedMapImpl();
      values.putSingle("key1", "string constructor test");
      StringConstructor o1 = (StringConstructor)t.createValue("key1", values, null);
      assertEquals("string constructor test", o1.getValue());
      values.clear();
      o1 = (StringConstructor)t.createValue("key1", values, "default value");
      assertEquals("default value", o1.getValue());
   }

   public static class StringConstructor
   {
      private String value;

      public StringConstructor(String value)
      {
         this.value = value;
      }

      public String getValue()
      {
         return value;
      }
   }

}
