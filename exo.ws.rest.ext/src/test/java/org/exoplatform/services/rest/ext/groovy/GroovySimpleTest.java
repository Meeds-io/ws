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

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GroovySimpleTest extends BaseTest
{

   public void testPerRequest() throws Exception
   {
      publicationTest(false, "g1");
   }

   public void testSingleton() throws Exception
   {
      publicationTest(true, "g2");
   }

   private void publicationTest(boolean singleton, String name) throws Exception
   {
      String script = //
         "@javax.ws.rs.Path(\"a\")" //
            + "class GroovyResource {" //
            + "@javax.ws.rs.GET @javax.ws.rs.Path(\"{who}\")" //
            + "def m0(@javax.ws.rs.PathParam(\"who\") String who) { return (\"hello \" + who)}" //
            + "}";

      assertEquals(0, binder.getSize());

      if (singleton)
         groovyPublisher.publishSingleton(new ByteArrayInputStream(script.getBytes()), name);
      else
         groovyPublisher.publishPerRequest(new ByteArrayInputStream(script.getBytes()), name);

      assertEquals(1, binder.getSize());

      String cs =
         binder.getResources().get(0).getObjectModel().getObjectClass().getProtectionDomain().getCodeSource()
            .getLocation().toString();
      assertEquals("file:/groovy/script/jaxrs", cs);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", "/a/groovy", "", null, null, writer);
      assertEquals(200, resp.getStatus());
      assertEquals("hello groovy", new String(writer.getBody()));
   }

}
