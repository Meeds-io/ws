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

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.CacheControl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CacheControlHeaderDelegate extends AbstractHeaderDelegate<CacheControl>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<CacheControl> support()
   {
      return CacheControl.class;
   }

   /**
    * {@inheritDoc}
    */
   public CacheControl fromString(String header)
   {
      throw new UnsupportedOperationException("CacheControl used only for response headers.");
   }

   /**
    * {@inheritDoc}
    */
   public String toString(CacheControl header)
   {
      StringBuffer buff = new StringBuffer();
      if (!header.isPrivate())
      {
         appendString(buff, "public");
      }
      if (header.isPrivate())
      {
         appendWithParameters(buff, "private", header.getPrivateFields());
      }
      if (header.isNoCache())
      {
         appendWithParameters(buff, "no-cache", header.getNoCacheFields());
      }
      if (header.isNoStore())
      {
         appendString(buff, "no-store");
      }
      if (header.isNoTransform())
      {
         appendString(buff, "no-transform");
      }
      if (header.isMustRevalidate())
      {
         appendString(buff, "must-revalidate");
      }
      if (header.isProxyRevalidate())
      {
         appendString(buff, "proxy-revalidate");
      }
      if (header.getMaxAge() >= 0)
      {
         appendString(buff, header.getMaxAge() + "");
      }
      if (header.getSMaxAge() >= 0)
      {
         appendString(buff, header.getSMaxAge() + "");
      }
      for (Map.Entry<String, String> entry : header.getCacheExtension().entrySet())
      {
         appendWithSingleParameter(buff, entry.getKey(), entry.getValue());
      }
      return buff.toString();
   }

   /**
    * Add single <code>String</code> to <code>StringBuffer</code> .
    * 
    * @param buff the StringBuffer
    * @param s single String
    */
   private static void appendString(StringBuffer buff, String s)
   {
      if (buff.length() > 0)
         buff.append(',').append(' ');

      buff.append(s);
   }

   /**
    * Add single pair key=value to <code>StringBuffer</code> . If value contains
    * whitespace then quotes will be added.
    * 
    * @param buff the StringBuffer
    * @param key the key
    * @param value the value
    */
   private static void appendWithSingleParameter(StringBuffer buff, String key, String value)
   {
      StringBuffer localBuff = new StringBuffer();
      localBuff.append(key);

      if (value != null && value.length() > 0)
         localBuff.append('=').append(HeaderHelper.addQuotesIfHasWhitespace(value));

      appendString(buff, localBuff.toString());
   }

   /**
    * Add to pair key="value1, value2" to <code>StringBuffer</code> .
    * 
    * @param buff the StringBuffer
    * @param key the key
    * @param values the collection of values
    */
   private static void appendWithParameters(StringBuffer buff, String key, List<String> values)
   {
      appendString(buff, key);
      if (values.size() > 0)
      {
         StringBuffer localBuff = new StringBuffer();
         buff.append('=');
         buff.append('"');

         for (String t : values)
            appendString(localBuff, t);

         buff.append(localBuff.toString());
         buff.append('"');
      }
   }

}
