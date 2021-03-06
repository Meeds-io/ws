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
package org.exoplatform.ws.frameworks.json.impl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: JsonUtilsTest.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public class JsonUtilsTest extends JsonTest
{

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
   }

   public void testGetJSONString()
   {
      assertEquals(JsonUtils.getJsonString("string"), "\"string\"");
      assertEquals(JsonUtils.getJsonString("s\ntring\n"), "\"s\\ntring\\n\"");
      assertEquals(JsonUtils.getJsonString("s\tring"), "\"s\\tring\"");
      assertEquals(JsonUtils.getJsonString("st\ring"), "\"st\\ring\"");
      assertEquals(JsonUtils.getJsonString("str\\ing"), "\"str\\\\ing\"");
      assertEquals(JsonUtils.getJsonString("stri\"ng"), "\"stri\\\"ng\"");
      assertEquals(JsonUtils.getJsonString("stri/ng"), "\"stri/ng\"");
      //      int i = 0;
      //      for (char c = '\u0000'; c < '\u0020'; c++, i++)
      //      {
      //         System.out.print(JsonUtils.getJsonString(c + "") + " ");
      //         if (i > 10)
      //         {
      //            System.out.println();
      //            i = 0;
      //         }
      //      }
      //      for (char c = '\u0080'; c < '\u00a0'; c++, i++)
      //      {
      //         System.out.print(JsonUtils.getJsonString(c + " "));
      //         if (i > 10)
      //         {
      //            System.out.println();
      //            i = 0;
      //         }
      //      }
      //      for (char c = '\u2000'; c < '\u2100'; c++, i++)
      //      {
      //         System.out.print(JsonUtils.getJsonString(c + " "));
      //         if (i > 10)
      //         {
      //            System.out.println();
      //            i = 0;
      //         }
      //      }
   }

}
