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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import org.exoplatform.services.rest.PerRequestObjectFactory;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GroovyJaxrsPublisher
{

   /** Default character set name. */
   static final String DEFAULT_CHARSET_NAME = "UTF-8";

   /** Default character set. */
   static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

   protected final ResourceBinder binder;

   protected final GroovyScriptInstantiator instantiator;

   protected GroovyClassLoader gcl;

   /**
    * Create GroovyJaxrsPublisher which is able publish per-request and
    * singleton resources. Any required dependencies for per-request resource
    * injected by {@link PerRequestObjectFactory}, instance of singleton
    * resources will be created by {@link GroovyScriptInstantiator}.
    *
    * @param binder resource binder
    * @param instantiator instantiate java object from given groovy source
    */
   public GroovyJaxrsPublisher(ResourceBinder binder, GroovyScriptInstantiator instantiator)
   {
      this.binder = binder;
      this.instantiator = instantiator;
      ClassLoader cl = getClass().getClassLoader();
      this.gcl = new GroovyClassLoader(cl);
   }

   /**
    * Create GroovyJaxrsPublisher which is able public per-request resources
    * only. This is default behavior for RESTful services. Any required
    * dependencies for resource injected by {@link PerRequestObjectFactory} in
    * runtime.
    *
    * @param binder resource binder
    */
   public GroovyJaxrsPublisher(ResourceBinder binder)
   {
      ClassLoader cl = getClass().getClassLoader();
      this.gcl = new GroovyClassLoader(cl);
      this.binder = binder;
      this.instantiator = null;
   }

   /**
    * Parse given stream and publish result as per-request RESTful service.
    *
    * @param in stream which contains groovy source code of RESTful service
    * @param name name of resource
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    */
   public boolean publishPerRequest(InputStream in, String name)
   {
      return publishPerRequest(createCodeSource(in, name));
   }

   /**
    * Parse given <code>source</code> and publish result as per-request RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param name name of resource
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    */
   public boolean publishPerRequest(String source, String name)
   {
      byte[] bytes = source.getBytes(DEFAULT_CHARSET);
      return publishPerRequest(new ByteArrayInputStream(bytes), name);
   }

   /**
    * Parse given {@link GroovyCodeSource} and publish result as per-request
    * RESTful service.
    *
    * @param gcs groovy code source which contains source code of RESTful
    *        service
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    */
   public boolean publishPerRequest(GroovyCodeSource gcs)
   {
      Class<?> rc = gcl.parseClass(gcs);
      return binder.bind(rc);
   }

   /**
    * Parse given stream and publish result as singleton RESTful service.
    *
    * @param in stream which contains groovy source code of RESTful service
    * @param name name of resource
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    * @throws UnsupportedOperationException if publisher was created without
    *         support of singleton resource, see
    *         {@link #GroovyJaxrsPublisher(ResourceBinder)}
    */
   public boolean publishSingleton(InputStream in, String name)
   {
      if (instantiator == null)
         throw new UnsupportedOperationException(
            "Can't instantiate groovy script. GroovyScriptInstantiator is not set.");
      return publishSingleton(createCodeSource(in, name));
   }

   /**
    * Parse given <code>source</code> and publish result as singleton RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param name name of resource
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    * @throws UnsupportedOperationException if publisher was created without
    *         support of singleton resource, see
    *         {@link #GroovyJaxrsPublisher(ResourceBinder)}
    */
   public boolean publishSingleton(String source, String name)
   {
      if (instantiator == null)
         throw new UnsupportedOperationException(
            "Can't instantiate groovy script. GroovyScriptInstantiator is not set.");
      byte[] bytes = source.getBytes(DEFAULT_CHARSET);
      return publishSingleton(new ByteArrayInputStream(bytes), name);
   }

   /**
    * Parse given {@link GroovyCodeSource} and publish result as singleton
    * RESTful service.
    *
    * @param gcs groovy code source which contains source code of RESTful
    *        service
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise
    * @throws UnsupportedOperationException if publisher was created without
    *         support of singleton resource, see
    *         {@link #GroovyJaxrsPublisher(ResourceBinder)}
    */
   public boolean publishSingleton(GroovyCodeSource gcs)
   {
      if (instantiator == null)
         throw new UnsupportedOperationException(
            "Can't instantiate groovy script. GroovyScriptInstantiator is not set.");
      Object r = instantiator.instantiateScript(gcs, gcl);
      return binder.bind(r);
   }

   /**
    * Set groovy class loader.
    *
    * @param gcl groovy class loader
    * @throws NullPointerException if <code>gcl == null</code>
    */
   public void setGroovyClassLoader(GroovyClassLoader gcl)
   {
      if (gcl == null)
         throw new NullPointerException("GroovyClassLoader may not be null.");
      this.gcl = gcl;
   }

   /**
    * Create {@link GroovyCodeSource} from given stream and name. Code base
    * 'file:/groovy/script/jaxrs' will be used.
    *
    * @param in groovy source code stream
    * @param name code source name
    * @return GroovyCodeSource
    */
   protected GroovyCodeSource createCodeSource(InputStream in, String name)
   {
      GroovyCodeSource gcs =
         new GroovyCodeSource(in, name == null ? gcl.generateScriptName() : name, "/groovy/script/jaxrs");
      gcs.setCachable(false);
      return gcs;
   }
}
