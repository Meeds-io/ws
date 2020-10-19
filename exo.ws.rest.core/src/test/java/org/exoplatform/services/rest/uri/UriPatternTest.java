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
package org.exoplatform.services.rest.uri;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UriPatternTest extends TestCase
{

   public void testUriComparator()
   {
      List<UriPattern> l = new ArrayList<UriPattern>();
      String[] as = {"/a", "a/b/c/d/{e}", "a/b/{c}/d/{e}", "a/{b}", "a/b/c/d/e"};

      for (String a : as)
         l.add(new UriPattern(a));

      Collections.sort(l, UriPattern.URIPATTERN_COMPARATOR);

      assertEquals("/a/b/c/d/e", l.get(0).getTemplate());
      assertEquals("/a/b/c/d/{e}", l.get(1).getTemplate());
      assertEquals("/a/b/{c}/d/{e}", l.get(2).getTemplate());
      assertEquals("/a/{b}", l.get(3).getTemplate());
      assertEquals("/a", l.get(4).getTemplate());
   }

   public void testRegex()
   {
      UriPattern p = new UriPattern("/");
      assertEquals("(/.*)?", p.getRegex());

      p = new UriPattern("/a");
      assertEquals("/a(/.*)?", p.getRegex());

      p = new UriPattern("a");
      assertEquals("/a(/.*)?", p.getRegex());

      p = new UriPattern("/a/");
      assertEquals("/a(/.*)?", p.getRegex());

      p = new UriPattern("/a/{x}");
      assertEquals("/a/([^/]+?)(/.*)?", p.getRegex());

      p = new UriPattern("/a/{x}/");
      assertEquals("/a/([^/]+?)(/.*)?", p.getRegex());

      p = new UriPattern("/a/{x:}/");
      assertEquals("/a/([^/]+?)(/.*)?", p.getRegex());

      p = new UriPattern("/a/{x  :   .*}/");
      assertEquals("/a/(.*)(/.*)?", p.getRegex());

      p = new UriPattern("/a/{x  :   .*}/");
      assertEquals("/a/(.*)(/.*)?", p.getRegex());
   }

   public void testMatch()
   {
      testMatch("/", "/a/b", new String[]{"/a/b"});
      testMatch("/", "/a/b/", new String[]{"/a/b/"});
      // leading slash
      testMatch("/a", "/a/b", new String[]{"/b"});
      testMatch("a", "/a/b", new String[]{"/b"});
      // final slash
      testMatch("/a/{x}", "/a/b", new String[]{"b", null});
      testMatch("/a/{x}", "/a/b/", new String[]{"b", "/"});

      // external pattern
      // final slash
      testMatch("/a/{x:.*}", "/a/b/", new String[]{"b/", null});
      testMatch("/a/{x:.*}", "/a/b", new String[]{"b", null});

      testMatch("/a/{x:.*}", "/a/b/c/d/e", new String[]{"b/c/d/e", null});
      testMatch("/a/{x:.*}", "/a/b/c/d/e/", new String[]{"b/c/d/e/", null});

      testMatch("/a{x:.*}", "/a/b/c/d/e", new String[]{"/b/c/d/e", null});
      testMatch("/a{x:.*}", "/a/b/c/d/e/", new String[]{"/b/c/d/e/", null});

      testMatch("/a/{x}{y:.*}", "/a/b/c/d/e", new String[]{"b", "/c/d/e", null});
      testMatch("/a/{x}/{y:.*}", "/a/b/c/d/e", new String[]{"b", "c/d/e", null});
      testMatch("/a/{x}/{y:.*}/{z}", "/a/b/c/d/e", new String[]{"b", "c/d", "e", null});
      testMatch("/{a}/{b}/{c}/{x:.*}/{e}/{f}/{g}/", "/a/b/c/1/2/3/4/5/e/f/g/", new String[]{"a", "b", "c", "1/2/3/4/5",
         "e", "f", "g", "/"});
      testMatch("/{a}/{b}/{c}/{x:.*}/{e}/{f}/{g}/", "/a/b/c/1/2/3/4/5/e/f/g", new String[]{"a", "b", "c", "1/2/3/4/5",
         "e", "f", "g", null});

      testMatch("/a /{x}{y:(/)?}", "/a%20/b/", new String[]{"b", "/", null});
      testMatch("/a/{x}{y:(/)?}", "/a/b", new String[]{"b", "", null});

      testMatch("/{x:\\d+}.{y:\\d+}", "/111.222", new String[]{"111", "222", null});
      testMatch("/{x:\\d+}.{y:\\d+}", "/111.222/", new String[]{"111", "222", "/"});

      testMatch("/a/b/{x}{y:(/)?}", "/a/b/c", new String[]{"c", "", null});
      testMatch("/a/b/{x}{y:(/)?}", "/a/b/c/", new String[]{"c", "/", null});

      testMatch("/a/b/{x}/{y}/{z}/{X:.*}", "/a/b/c/d/e/f/g/h", new String[]{"c", "d", "e", "f/g/h", null});
      testMatch("/a/b/{x}/{y}/{z}/{X:.*}", "/a/b/c/d/e/f/g/h/", new String[]{"c", "d", "e", "f/g/h/", null});
      testMatch("/a/b/{x}/{y}/{z}/{X:.*}{Y:[/]+?}", "/a/b/c/d/e/f/g/h/",
         new String[]{"c", "d", "e", "f/g/h", "/", null});
      testMatch("/a/b/{x}/{y}/{z}/{X:.*}{Y:/+?}", "/a/b/c/d/e/f/g/h/", new String[]{"c", "d", "e", "f/g/h", "/", null});

      testMatch("/a/b/{x}/{X:.*}/{y}/{z}", "/a/b/c/d/e/f/g/h", new String[]{"c", "d/e/f", "g", "h", null});
      testMatch("/a/b/{x}/{X:.*}/{y}", "/a/b/c/d/e/f/g/h", new String[]{"c", "d/e/f/g", "h", null});
      testMatch("/a/b/{x}/{X:.*}/{y}", "/a/b/c/d/e/f/g/h/", new String[]{"c", "d/e/f/g", "h", "/"});

   }

   private boolean log = false;

   private void testMatch(String pattern, String uri, String[] values)
   {
      UriPattern p = new UriPattern(pattern);
      if (log)
      {
         System.out.println("URI:        " + uri);
         System.out.println("REGEX:      " + p.getRegex());
         System.out.println("PATTERN:    " + pattern);
         System.out.println("TEMPLATE:   " + p.getTemplate());
      }

      List<String> l = new ArrayList<String>();
      assertTrue(p.match(uri, l));

      if (log)
      {
         System.out.println("PARAMETERS: " + p.getParameterNames());
         System.out.println("VARIABLES:  " + l);
         System.out.println();
      }
      assertEquals(values.length, l.size());
      int i = 0;
      for (String t : l)
         assertEquals(values[i++], t);
   }

}
