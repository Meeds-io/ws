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
package org.exoplatform.services.rest.impl.method;

import junit.framework.TestCase;

import org.exoplatform.services.rest.impl.MultivaluedMapImpl;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CollectionStringConstructorProducerTest extends TestCase
{

   @SuppressWarnings("unchecked")
   public void testList() throws Exception
   {
      CollectionStringConstructorProducer collectionStringConstructorProducer =
         new CollectionStringConstructorProducer(List.class, Integer.class.getConstructor(String.class));
      MultivaluedMap<String, String> multivaluedMap = new MultivaluedMapImpl();
      multivaluedMap.putSingle("number", "2147483647");
      List<String> l1 = (List<String>)collectionStringConstructorProducer.createValue("number", multivaluedMap, null);
      assertEquals(1, l1.size());
      assertEquals(2147483647, l1.get(0));
      // test with default value
      List<String> l2 =
         (List<String>)collectionStringConstructorProducer.createValue("_number_", multivaluedMap, "-2147483647");
      assertEquals(1, l2.size());
      assertEquals(-2147483647, l2.get(0));
   }

   @SuppressWarnings("unchecked")
   public void testSet() throws Exception
   {
      CollectionStringConstructorProducer collectionStringConstructorProducer =
         new CollectionStringConstructorProducer(Set.class, Integer.class.getConstructor(String.class));
      MultivaluedMap<String, String> multivaluedMap = new MultivaluedMapImpl();
      multivaluedMap.putSingle("number", "2147483647");
      Set<String> s1 = (Set<String>)collectionStringConstructorProducer.createValue("number", multivaluedMap, null);
      assertEquals(1, s1.size());
      assertEquals(2147483647, s1.iterator().next());
      // test with default value
      Set<String> s2 =
         (Set<String>)collectionStringConstructorProducer.createValue("_number_", multivaluedMap, "-2147483647");
      assertEquals(1, s2.size());
      assertEquals(-2147483647, s2.iterator().next());
   }

   @SuppressWarnings("unchecked")
   public void testSortedSet() throws Exception
   {
      CollectionStringConstructorProducer collectionStringConstructorProducer =
         new CollectionStringConstructorProducer(SortedSet.class, Integer.class.getConstructor(String.class));
      MultivaluedMap<String, String> multivaluedMap = new MultivaluedMapImpl();
      multivaluedMap.putSingle("number", "2147483647");
      SortedSet<String> ss1 =
         (SortedSet<String>)collectionStringConstructorProducer.createValue("number", multivaluedMap, null);
      assertEquals(1, ss1.size());
      assertEquals(2147483647, ss1.iterator().next());
      // test with default value
      SortedSet<String> ss2 =
         (SortedSet<String>)collectionStringConstructorProducer.createValue("_number_", multivaluedMap, "-2147483647");
      assertEquals(1, ss2.size());
      assertEquals(-2147483647, ss2.iterator().next());
   }
}
