/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.httputils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

// TODO: Auto-generated Javadoc
/**
 * The Class MockHttpSession.
 * 
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: $
 */
public class MockHttpSession implements HttpSession
{

   /** The attributes map. */
   private Map attributes = new HashMap();

   /** The servlet context. */
   private ServletContext servletContext;

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
      return servletContext;
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
   public HttpSessionContext getSessionContext()
   {
      return null;
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
      return new Vector(attributes.keySet()).elements();
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
