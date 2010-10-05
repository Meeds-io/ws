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
package org.exoplatform.ws.frameworks.json.impl.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import junit.framework.TestCase;

import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.exoplatform.ws.frameworks.json.value.impl.ObjectValue;
import org.exoplatform.ws.frameworks.json.value.impl.StringValue;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

public class GroovyBeanTest extends TestCase
{

   @SuppressWarnings("unchecked")
   public void testRestoreGroovyBean() throws Exception
   {
      GroovyClassLoader cl = new GroovyClassLoader();
      Class c = cl.parseClass(Thread.currentThread().getContextClassLoader().getResourceAsStream("SimpleBean.groovy"));
      JsonValue ov = new ObjectValue();
      StringValue sv = new StringValue("test restore groovy bean");
      ov.addElement("value", sv);
      assertEquals("test restore groovy bean", ObjectBuilder.createObject(c, ov).toString());
   }

   @SuppressWarnings("unchecked")
   public void testSerializeGroovyBean() throws Exception
   {
      GroovyClassLoader cl = new GroovyClassLoader();
      Class c = cl.parseClass(Thread.currentThread().getContextClassLoader().getResourceAsStream("SimpleBean.groovy"));
      GroovyObject groovyObject = (GroovyObject)c.newInstance();
      groovyObject.invokeMethod("setValue", new Object[]{"test serialize groovy bean"});
      assertEquals("{\"value\":\"test serialize groovy bean\"}", new JsonGeneratorImpl().createJsonObject(groovyObject)
         .toString());
   }

   @SuppressWarnings("unchecked")
   public void testSerializeGroovyBean1() throws Exception
   {
      GroovyClassLoader cl = new GroovyClassLoader();
      Class c = cl.parseClass(Thread.currentThread().getContextClassLoader().getResourceAsStream("BookStorage.groovy"));
      GroovyObject groovyObject = (GroovyObject)c.newInstance();
      groovyObject.invokeMethod("initStorage", new Object[]{});

      JsonValue jsonValue = new JsonGeneratorImpl().createJsonObject(groovyObject);
      //System.out.println(jsonValue);
      assertTrue(jsonValue.isObject());
      Iterator<JsonValue> iterator = jsonValue.getElement("books").getElements();
      assertEquals("JUnit in Action", iterator.next().getElement("title").getStringValue());
      assertEquals("Beginning C# 2008 from novice to professional", iterator.next().getElement("title")
         .getStringValue());
      assertEquals("Advanced JavaScript, Third Edition", iterator.next().getElement("title").getStringValue());
      assertFalse(iterator.hasNext());
   }

   @SuppressWarnings("unchecked")
   public void testRestoreGroovyBean1() throws Exception
   {
      GroovyClassLoader cl = new GroovyClassLoader();
      Class c = cl.parseClass(Thread.currentThread().getContextClassLoader().getResourceAsStream("BookStorage.groovy"));
      JsonParserImpl jsonParser = new JsonParserImpl();
      JsonHandler jsonHandler = new JsonDefaultHandler();
      jsonParser.parse(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(
         "BookStorage.txt")), jsonHandler);
      JsonValue jv = jsonHandler.getJsonObject();
      GroovyObject o = (GroovyObject)ObjectBuilder.createObject(c, jv);
      //System.out.println(o);
      List<GroovyObject> books = (List<GroovyObject>)o.getProperty("books");
      assertEquals(3, books.size());
      assertEquals(books.get(0).getProperty("title"), "JUnit in Action");
      assertEquals(books.get(1).getProperty("title"), "Beginning C# 2008 from novice to professional");
      assertEquals(books.get(2).getProperty("title"), "Advanced JavaScript. Third Edition");
   }

}
