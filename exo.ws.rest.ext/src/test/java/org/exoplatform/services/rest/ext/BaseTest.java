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

package org.exoplatform.services.rest.ext;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ContainerRequest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.InputHeadersMap;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.DummyContainerResponseWriter;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseTest extends TestCase
{
   protected StandaloneContainer container;

   protected ProviderBinder providers;

   protected ResourceBinder binder;

   protected RequestHandlerImpl requestHandler;

   protected GroovyJaxrsPublisher groovyPublisher;

   public void setUp() throws Exception
   {
      StandaloneContainer.setConfigurationPath("src/test/resources/conf/standalone/test-configuration.xml");
      container = StandaloneContainer.getInstance();
      binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
      requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      // reset providers to be sure it is clean
      ProviderBinder.setInstance(new ProviderBinder());
      providers = ProviderBinder.getInstance();
      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers));
      binder.clear();
      groovyPublisher = (GroovyJaxrsPublisher)container.getComponentInstanceOfType(GroovyJaxrsPublisher.class);
   }

   public void tearDown() throws Exception
   {
   }

   public ContainerResponse service(String method, String requestURI, String baseURI,
      Map<String, List<String>> headers, byte[] data, ContainerResponseWriter writer) throws Exception
   {

      if (headers == null)
         headers = new MultivaluedMapImpl();

      ByteArrayInputStream in = null;
      if (data != null)
         in = new ByteArrayInputStream(data);

      EnvironmentContext envctx = new EnvironmentContext();
      HttpServletRequest httpRequest =
         new MockHttpServletRequest(requestURI, in, in != null ? in.available() : 0, method, headers);
      envctx.put(HttpServletRequest.class, httpRequest);
      EnvironmentContext.setCurrent(envctx);
      ContainerRequest request =
         new ContainerRequest(method, new URI(requestURI), new URI(baseURI), in, new InputHeadersMap(headers));
      ContainerResponse response = new ContainerResponse(writer);
      requestHandler.handleRequest(request, response);
      return response;
   }

   public ContainerResponse service(String method, String requestURI, String baseURI,
      MultivaluedMap<String, String> headers, byte[] data) throws Exception
   {
      return service(method, requestURI, baseURI, headers, data, new DummyContainerResponseWriter());

   }

}
