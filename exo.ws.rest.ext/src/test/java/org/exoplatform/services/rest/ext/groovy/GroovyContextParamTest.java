/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.services.rest.ext.groovy;

import org.exoplatform.services.rest.ext.BaseTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.InputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GroovyContextParamTest extends BaseTest
{

   private InputStream script;

   public void setUp() throws Exception
   {
      super.setUp();
      script = Thread.currentThread().getContextClassLoader().getResourceAsStream("groovy1.groovy");
      assertNotNull(script);
   }

   public void testPerRequest() throws Exception
   {
      assertEquals(0, binder.getSize());
      groovyPublisher.publishPerRequest(script, "g1");
      assertEquals(1, binder.getSize());
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp =
         service("GET", "http://localhost:8080/context/a/b", "http://localhost:8080/context", null, null, writer);
      assertEquals(200, resp.getStatus());
      assertEquals("GET\n/context/a/b", new String(writer.getBody()));
   }

}
