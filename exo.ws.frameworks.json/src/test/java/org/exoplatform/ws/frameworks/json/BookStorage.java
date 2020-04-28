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
package org.exoplatform.ws.frameworks.json;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: BookStorage.java 34417 2009-07-23 14:42:56Z dkatayev $
 */
public class BookStorage
{

   private List<Book> books;

   public void setBooks(List<Book> b)
   {
      books = b;
   }

   public List<Book> getBooks()
   {
      return books;
   }

   @Override
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      for (Book b : books)
         sb.append(b.toString());
      return sb.toString();
   }

}
