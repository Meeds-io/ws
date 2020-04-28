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
package org.exoplatform.common.http.client;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by The eXo Platform SAS .<br> DOM - like (but lighter) property
 * representation
 *
 * @author Aymen Boughzela
 * @version $Id: $
 */
public class CookieTest extends TestCase
{
    public void testParseCookie() throws Exception
    {
        for (int i = 0; i < 243; i++)
        {
            boolean withExpires = false, withDomain = false, withPath = false, withSecure = false, withHttpOnly = false;
            StringBuilder sb = new StringBuilder();
            sb.append("Customer=WILE_E_COYOTE");
            int[] decomposition = decomposition(i);
            int b = decomposition[0];
            if (b > 0)
            {
                withExpires = true;
                sb.append("; expires=");
                if (b == 1)
                {
                    sb.append('"');
                }
                sb.append("Sat, 12 Aug 1995 13:30:00 GMT+0430");
                if (b == 1)
                {
                    sb.append('"');
                }
            }
            int c = decomposition[1];
            if (c > 0)
            {
                withDomain=true;
                sb.append("; Domain=www.google.com");
            }
            int d = decomposition[2];
            if (d > 0)
            {
                withPath=true;
                sb.append("; Path=/acme");
            }
            int e = decomposition[3];
            if (e == 1)
            {
                withSecure=true;
                sb.append("; secure");
            }
            if(e==2)
                continue;
            int f = decomposition[4];
            if (f == 1)
            {
                withHttpOnly=true;
                sb.append("; HttpOnly");
            }
            if (f==2)
                continue;
            testParseCookie(sb.toString(),withExpires, withDomain, withPath, withSecure, withHttpOnly);
        }
    }

    private void testParseCookie(String pattern, boolean withExpires, boolean withDomain, boolean withPath, boolean withSecure, boolean withHttpOnly) throws Exception
    {
        HTTPConnection con= new HTTPConnection("www.google.com",80);
        RoRequest req=new Request(con,"GET","/acme",null,null,null,false) ;
        System.out.println(pattern);
        Cookie[] resultCookies=Cookie.parse(pattern,req);
        assertNotNull(resultCookies);
        assertEquals(1,resultCookies.length);
        Cookie resultCookie= resultCookies[0] ;
        assertEquals("Customer",resultCookie.getName());
        assertEquals("WILE_E_COYOTE",resultCookie.getValue());
        if (withExpires)
        {
            assertEquals(new Date("Sat, 12 Aug 1995 13:30:00 GMT+0430"),resultCookie.expires());
        }
        if (withDomain)
        {
            assertEquals("www.google.com",resultCookie.getDomain());
        }
        if (withPath)
        {
            assertEquals("/acme",resultCookie.getPath());
        }
        if (withSecure)
        {
            assertTrue(resultCookie.isSecure());
        }
        if (withHttpOnly)
        {
            assertTrue(resultCookie.isHttpOnly());
        }
    }

    private static int[] decomposition(int value)
    {
        int[] result = new int[5];
        int base = 3;
        int power = base;
        int i = 0;
        while (value > 0)
        {
            int modulo = value % power;
            result[i] = ((base * modulo) / power);
            value -= modulo;
            power *= base;
            i++;
        }
        return result;
    }
}
