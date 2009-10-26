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
package org.exoplatform.services.rest.impl.header;

import org.exoplatform.services.rest.BaseTest;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AcceptLanguageTest extends BaseTest
{

   public void testValueOf()
   {
      String al = "en-gb;q=0.8";
      AcceptLanguage acceptedLanguage = AcceptLanguage.valueOf(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("gb", acceptedLanguage.getSubTag());
      assertEquals(0.8F, acceptedLanguage.getQvalue());

      al = "en;q=0.8";
      acceptedLanguage = AcceptLanguage.valueOf(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("", acceptedLanguage.getSubTag());
      assertEquals(0.8F, acceptedLanguage.getQvalue());

      al = "en";
      acceptedLanguage = AcceptLanguage.valueOf(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("", acceptedLanguage.getSubTag());
      assertEquals(1F, acceptedLanguage.getQvalue());

      al = "en-GB";
      acceptedLanguage = AcceptLanguage.valueOf(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("gb", acceptedLanguage.getSubTag());
      assertEquals(1F, acceptedLanguage.getQvalue());
   }

   public void testFromString()
   {
      AcceptLanguageHeaderDelegate hd = new AcceptLanguageHeaderDelegate();
      String al = "en-gb;q=0.8";
      AcceptLanguage acceptedLanguage = hd.fromString(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("gb", acceptedLanguage.getSubTag());
      assertEquals(0.8F, acceptedLanguage.getQvalue());

      al = "en;q=0.8";
      acceptedLanguage = hd.fromString(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("", acceptedLanguage.getSubTag());
      assertEquals(0.8F, acceptedLanguage.getQvalue());

      al = "en";
      acceptedLanguage = hd.fromString(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("", acceptedLanguage.getSubTag());
      assertEquals(1F, acceptedLanguage.getQvalue());

      al = "en-GB";
      acceptedLanguage = hd.fromString(al);
      assertEquals("en", acceptedLanguage.getPrimaryTag());
      assertEquals("gb", acceptedLanguage.getSubTag());
      assertEquals(1F, acceptedLanguage.getQvalue());

   }

   public void testListProducer()
   {
      List<AcceptLanguage> l = HeaderHelper.createAcceptedLanguageList(null);
      assertEquals(1, l.size());
      l = HeaderHelper.createAcceptedLanguageList("");
      assertEquals(1, l.size());

      String ln = "da;q=0.825,   en-GB,  en;q=0.8";
      l = HeaderHelper.createAcceptedLanguageList(ln);
      assertEquals(3, l.size());

      assertEquals("en", l.get(0).getPrimaryTag());
      assertEquals("gb", l.get(0).getSubTag());
      assertEquals(1.0F, l.get(0).getQvalue());

      assertEquals("da", l.get(1).getPrimaryTag());
      assertEquals("", l.get(1).getSubTag());
      assertEquals(0.825F, l.get(1).getQvalue());

      assertEquals("en", l.get(2).getPrimaryTag());
      assertEquals("", l.get(2).getSubTag());
      assertEquals(0.8F, l.get(2).getQvalue());
   }

}
