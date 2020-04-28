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
public class BookBean
{
   
   String author
   
   String title
   
   double price
   
   long isdn
   
   int pages
   
   boolean availability
   
   boolean delivery
   
   String toString()
   {
      StringBuffer sb = new StringBuffer()
      sb.append("Book:{").append("Author: ").append(author).append(" ").append("Title: ").append(title).append(" ")
      .append("Pages: ").append(pages).append(" ").append("Price: ").append(price).append(" ").append("ISDN: ")
      .append(isdn).append("Availability: ").append(availability).append(" ").append("Delivery: ").append(delivery)
      .append(" ").append("} ")
      sb.toString()
   }
   
   boolean equals(Object other)
   {
      return other != null && other instanceof BookBean && other.author == author && other.title == title && other.isdn == isdn && other.pages == pages && other.price == price && other.availability == availability && other.delivery == delivery
   }
}