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

import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;


/**
 * Created by The eXo Platform SARL Author : Mestrallet Benjamin
 * benjmestrallet@users.sourceforge.net
 */
public class MockServletResponse extends HttpServletResponseWrapper
{

   /** The tmp writer. */
   private PrintWriter tmpWriter;

   /** The output. */
   private ByteArrayOutputStream output;

   /** The servlet output. */
   private ByteArrayServletOutputStream servletOutput;

   /** The locale. */
   private Locale locale_;

   /**
    * Instantiates a new mock servlet response.
    * 
    * @param httpServletResponse the http servlet response
    */
   public MockServletResponse(HttpServletResponse httpServletResponse)
   {
      super(httpServletResponse);
      output = new ByteArrayOutputStream();
      tmpWriter = new PrintWriter(output);
      servletOutput = new ByteArrayServletOutputStream(output);
   }

   /**
    * {@inheritDoc}
    */
   public void finalize() throws Throwable
   {
      super.finalize();
      servletOutput.close();
      output.close();
      tmpWriter.close();
   }

   /**
    * Gets the portlet content.
    * 
    * @return the portlet content
    */
   public String getPortletContent()
   {
      String s = output.toString();
      reset();
      return s;
   }

   /**
    * Converts output to byte array.
    * 
    * @return the byte[]
    */
   public byte[] toByteArray()
   {
      return output.toByteArray();
   }

   /**
    * Gets the output content.
    * 
    * @return the output content
    */
   public String getOutputContent()
   {
      return new String(output.toByteArray());
   }

   /**
    * {@inheritDoc}
    */
   public void flushBuffer() throws IOException
   {
      tmpWriter.flush();
      servletOutput.flush();
   }

   /**
    * {@inheritDoc}
    */
   public void reset()
   {
      output.reset();
   }

   /**
    * Close.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public void close() throws IOException
   {
      tmpWriter.close();
   }

   /**
    * The Class ByteArrayServletOutputStream.
    */
   private static class ByteArrayServletOutputStream extends ServletOutputStream
   {
      
      /** The baos. */
      ByteArrayOutputStream baos;

      /**
       * Instantiates a new byte array servlet output stream.
       * 
       * @param baos the baos
       */
      public ByteArrayServletOutputStream(ByteArrayOutputStream baos)
      {
         this.baos = baos;
      }

      /**
       * {@inheritDoc}
       */
      public void write(int i) throws IOException
      {
         baos.write(i);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Locale getLocale()
   {
      return locale_;
   }

   /**
    * {@inheritDoc}
    */
   public void setLocale(java.util.Locale loc)
   {
      locale_ = loc;
   }

}
