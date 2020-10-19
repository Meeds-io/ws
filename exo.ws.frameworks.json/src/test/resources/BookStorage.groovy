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

public class BookStorage
{
   
   List<BookBean> books = new ArrayList<BookBean>()
   
   public BookStorage()
   {
   }
   
   void initStorage()
   {
      
      BookBean b1 = new BookBean(
      author:'Vincent Masson',
      title:'JUnit in Action',
      pages:386,
      price:19.37,
      isdn:93011099534534L,
      availability:true,
      delivery:true)
      
      BookBean b2 = new BookBean(
                        author:'Christian Gross',
                        title:'Beginning C# 2008 from novice to professional',
                        pages:511,
                        price:23.56,
                        isdn:9781590598696L,
                        availability:true,
                        delivery:true)
      
      BookBean b3 = new BookBean(
                        author:'Chuck Easttom',
                        title:'Advanced JavaScript, Third Edition',
                        pages:617,
                        price:25.99,
                        isdn:9781598220339L,
                        availability:false,
                        delivery:false)
      
      books.add(b1)
      books.add(b2)
      books.add(b3)
   }
   
   String toString()
   {
      books.toString()
   }
   
}