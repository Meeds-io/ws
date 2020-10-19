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
package org.exoplatform.services.rest.impl.header;

import org.exoplatform.services.rest.BaseTest;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AcceptTokenTest extends BaseTest
{

   public void testListAcceptCharset()
   {
      String cs = null;
      List<AcceptToken> l = HeaderHelper.createAcceptedCharsetList(cs);
      assertEquals(1, l.size());
      assertEquals(l.get(0).getToken(), "*");
      assertEquals(l.get(0).getQvalue(), 1.0F);

      cs = "";
      l = HeaderHelper.createAcceptedCharsetList(cs);
      assertEquals(1, l.size());
      assertEquals(l.get(0).getToken(), "*");
      assertEquals(l.get(0).getQvalue(), 1.0F);

      cs = "Windows-1251,utf-8; q   =0.9,*;q=0.7";
      l = HeaderHelper.createAcceptedCharsetList(cs);
      assertEquals(3, l.size());
      assertEquals(l.get(0).getToken(), "windows-1251");
      assertEquals(l.get(0).getQvalue(), 1.0F);
      assertEquals(l.get(1).getToken(), "utf-8");
      assertEquals(l.get(1).getQvalue(), 0.9F);
      assertEquals(l.get(2).getToken(), "*");
      assertEquals(l.get(2).getQvalue(), 0.7F);
   }

   public void testListAcceptEncoding()
   {
      String en = null;
      List<AcceptToken> l = HeaderHelper.createAcceptedEncodingList(en);
      assertEquals(1, l.size());
      assertEquals(l.get(0).getToken(), "*");
      assertEquals(l.get(0).getQvalue(), 1.0F);

      en = "";
      l = HeaderHelper.createAcceptedEncodingList(en);
      assertEquals(1, l.size());
      assertEquals(l.get(0).getToken(), "*");
      assertEquals(l.get(0).getQvalue(), 1.0F);

      en = "compress;q=0.5, gzip;q=1.0";
      l = HeaderHelper.createAcceptedCharsetList(en);
      assertEquals(2, l.size());
      assertEquals(l.get(0).getToken(), "gzip");
      assertEquals(l.get(0).getQvalue(), 1.0F);
      assertEquals(l.get(1).getToken(), "compress");
      assertEquals(l.get(1).getQvalue(), 0.5F);
   }

}
