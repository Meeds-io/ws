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
package org.exoplatform.services.rest.ext.provider;

import junit.framework.TestCase;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.services.rest.ext.provider.HierarchicalPropertyEntityProvider;

import java.io.ByteArrayInputStream;

public class HierarchicalPropertyEntityProviderTest extends TestCase
{
   public void testRequestBodyXMLParsing() throws Exception
   {
      String s = "\n<root>\n   <l1>\n\t<l2>hel\nlo</l2>\n  </l1>  \n</root>\n";
      //System.out.println(s);
      HierarchicalProperty hp =
         new HierarchicalPropertyEntityProvider().readFrom(HierarchicalProperty.class, null, null, null, null,
            new ByteArrayInputStream(s.getBytes()));
      assertTrue(hp.getChild(0).getChild(0).getValue().equals("hel\nlo"));
   }
}