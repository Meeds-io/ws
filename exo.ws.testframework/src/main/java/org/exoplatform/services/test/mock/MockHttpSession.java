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
package org.exoplatform.services.test.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * The Class MockHttpSession.
 * 
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: $
 */
public class MockHttpSession implements HttpSession
{

   /** The attributes map. */
   private Map<String, Object> attributes = new HashMap<String, Object>();

   /** The is valid. */
   private boolean isValid = true;

   /**
    * {@inheritDoc}
    */
   public long getCreationTime()
   {
      return 0L;
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return "MockSessionId";
   }

   /**
    * {@inheritDoc}
    */
   public long getLastAccessedTime()
   {
      return 0L;
   }

   /**
    * {@inheritDoc}
    */
   public ServletContext getServletContext()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void setMaxInactiveInterval(int i)
   {
   }

   /**
    * {@inheritDoc}
    */
   public int getMaxInactiveInterval()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public Object getAttribute(String s)
   {

      if (!isValid)
      {
         throw new IllegalStateException("Cannot call getAttribute() on invalidated session");
      }
      return attributes.get(s);
   }

   /**
    * {@inheritDoc}
    */
   public Object getValue(String s)
   {
      return getAttribute(s);
   }

   /**
    * {@inheritDoc}
    */
   public Enumeration getAttributeNames()
   {
      if (!isValid)
      {
         throw new IllegalStateException("Cannot call getAttribute() on invalidated session");
      }
      return new Vector<String>(attributes.keySet()).elements();
   }

   /**
    * {@inheritDoc}
    */
   public String[] getValueNames()
   {
      if (!isValid)
      {
         throw new IllegalStateException("Cannot call getAttribute() on invalidated session");
      }
      String results[] = new String[0];
      return ((String[])attributes.keySet().toArray(results));
   }

   /**
    * {@inheritDoc}
    */
   public void setAttribute(String s, Object o)
   {
      attributes.put(s, o);
   }

   /**
    * {@inheritDoc}
    */
   public void putValue(String s, Object o)
   {
      setAttribute(s, o);
   }

   /**
    * {@inheritDoc}
    */
   public void removeAttribute(String s)
   {
      attributes.remove(s);
   }

   /**
    * {@inheritDoc}
    */
   public void removeValue(String s)
   {
      removeAttribute(s);
   }

   /**
    * {@inheritDoc}
    */
   public void invalidate()
   {
      if (!isValid)
      {
         throw new IllegalStateException("Cannot call invalidate() on invalidated session");
      }
      this.isValid = false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isNew()
   {
      return false;
   }

   /**
    * Checks if is valid.
    * 
    * @return true, if is valid
    */
   public boolean isValid()
   {
      return this.isValid;
   }

   /**
    * Sets the valid.
    * 
    * @param isValid the new valid
    */
   public void setValid(boolean isValid)
   {
      this.isValid = isValid;
   }
}
