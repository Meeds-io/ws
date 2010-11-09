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

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.PerRequestObjectFactory;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.impl.ResourcePublicationException;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.uri.UriPattern;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Manage via {@link ResourceBinder} Groovy based RESTful services.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GroovyJaxrsPublisher
{

   /** Default character set name. */
   protected static final String DEFAULT_CHARSET_NAME = "UTF-8";

   /** Default character set. */
   protected static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

   protected final ResourceBinder binder;

   protected final GroovyScriptInstantiator instantiator;

   protected GroovyClassLoader gcl;

   protected final Map<ResourceId, String> resources = Collections.synchronizedMap(new HashMap<ResourceId, String>());

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
      final ClassLoader cl = getClass().getClassLoader();
      this.gcl = SecurityHelper.doPriviledgedAction(new PrivilegedAction<GroovyClassLoader>()
      {
         public GroovyClassLoader run()
         {
            return new GroovyClassLoader(cl);
         }
      });
   }

   /**
    * @return get underling groovy class loader
    */
   public GroovyClassLoader getGroovyClassLoader()
   {
      return gcl;
   }

   /**
    * Get resource corresponded to specified id <code>resourceId</code> .
    *
    * @param resourceId resource id
    * @return resource or <code>null</code>
    */
   public ObjectFactory<AbstractResourceDescriptor> getResource(ResourceId resourceId)
   {
      String path = resources.get(resourceId);
      if (path == null)
         return null;

      UriPattern pattern = new UriPattern(path);
      List<ObjectFactory<AbstractResourceDescriptor>> rootResources = binder.getResources();
      synchronized (rootResources)
      {
         for (ObjectFactory<AbstractResourceDescriptor> res : rootResources)
         {
            if (res.getObjectModel().getUriPattern().equals(pattern))
               return res;
         }
      }
      // If resource not exists any more but still in mapping.
      resources.remove(resourceId);
      return null;
   }

   /**
    * Check is groovy resource with specified id is published or not
    *
    * @param resourceId id of resource to be checked
    * @return <code>true</code> if resource is published and <code>false</code>
    *         otherwise
    */
   public boolean isPublished(ResourceId resourceId)
   {
      return null != getResource(resourceId);
   }

   /**
    * Parse given stream and publish result as per-request RESTful service.
    *
    * @param in stream which contains groovy source code of RESTful service
    * @param resourceId id to be assigned to resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Class, MultivaluedMap)}
    */
   public void publishPerRequest(final InputStream in, final ResourceId resourceId,
      MultivaluedMap<String, String> properties)
   {
      Class<?> rc = SecurityHelper.doPriviledgedAction(new PrivilegedAction<Class<?>>()
      {
         public Class<?> run()
         {
            return gcl.parseClass(createCodeSource(in, resourceId.getId()));
         }
      });

      binder.addResource(rc, properties);
      resources.put(resourceId, rc.getAnnotation(Path.class).value());
   }

   /**
    * Parse given <code>source</code> and publish result as per-request RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param resourceId id to be assigned to resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Class, MultivaluedMap)}
    */
   public final void publishPerRequest(String source, ResourceId resourceId, MultivaluedMap<String, String> properties)
   {
      publishPerRequest(source, DEFAULT_CHARSET, resourceId, properties);
   }

   /**
    * Parse given <code>source</code> and publish result as per-request RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param charset source string charset. May be <code>null</code> than
    *        default charset will be in use
    * @param resourceId id to be assigned to resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>.
    * @throws UnsupportedCharsetException if <code>charset</code> is unsupported
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Class, MultivaluedMap)}
    */
   public final void publishPerRequest(String source, String charset, ResourceId resourceId,
      MultivaluedMap<String, String> properties)
   {
      publishPerRequest(source, charset == null ? DEFAULT_CHARSET : Charset.forName(charset), resourceId, properties);
   }

   /**
    * Parse given stream and publish result as singleton RESTful service.
    *
    * @param in stream which contains groovy source code of RESTful service
    * @param resourceId id to be assigned to resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Object, MultivaluedMap)}
    */
   public void publishSingleton(InputStream in, ResourceId resourceId, MultivaluedMap<String, String> properties)
   {
      Object r = instantiator.instantiateScript(createCodeSource(in, resourceId.getId()), gcl);
      binder.addResource(r, properties);
      resources.put(resourceId, r.getClass().getAnnotation(Path.class).value());
   }

   /**
    * Parse given <code>source</code> and publish result as singleton RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param resourceId name of resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>.
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Object, MultivaluedMap)}
    */
   public final void publishSingleton(String source, ResourceId resourceId, MultivaluedMap<String, String> properties)
   {
      publishSingleton(source, DEFAULT_CHARSET, resourceId, properties);
   }

   /**
    * Parse given <code>source</code> and publish result as singleton RESTful
    * service.
    *
    * @param source groovy source code of RESTful service
    * @param charset source string charset. May be <code>null</code> than
    *        default charset will be in use
    * @param resourceId name of resource
    * @param properties optional resource properties. This parameter may be
    *        <code>null</code>.
    * @throws UnsupportedCharsetException if <code>charset</code> is unsupported
    * @throws NullPointerException if <code>resourceId == null</code>
    * @throws ResourcePublicationException see
    *         {@link ResourceBinder#addResource(Object, MultivaluedMap)}
    */
   public final void publishSingleton(String source, String charset, ResourceId resourceId,
      MultivaluedMap<String, String> properties)
   {
      publishSingleton(source, charset == null ? DEFAULT_CHARSET : Charset.forName(charset), resourceId, properties);
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
    * Unpublish resource with specified id.
    *
    * @param resourceId id of resource to be unpublished
    * @return <code>true</code> if resource was published and <code>false</code>
    *         otherwise, e.g. because there is not resource corresponded to
    *         supplied <code>resourceId</code>
    */
   public ObjectFactory<AbstractResourceDescriptor> unpublishResource(ResourceId resourceId)
   {
      String path = resources.get(resourceId);
      if (path == null)
      {
         return null;
      }
      ObjectFactory<AbstractResourceDescriptor> resource = binder.removeResource(path);
      if (resource != null)
      {
         resources.remove(resourceId);
      }
      return resource;
   }

   private void publishPerRequest(String source, Charset charset, ResourceId resourceId,
      MultivaluedMap<String, String> properties)
   {
      byte[] bytes = source.getBytes(charset);
      publishPerRequest(new ByteArrayInputStream(bytes), resourceId, properties);
   }

   private void publishSingleton(String source, Charset charset, ResourceId resourceId,
      MultivaluedMap<String, String> properties)
   {
      byte[] bytes = source.getBytes(charset);
      publishSingleton(new ByteArrayInputStream(bytes), resourceId, properties);
   }

   /**
    * Create {@link GroovyCodeSource} from given stream and name. Code base
    * 'file:/groovy/script/jaxrs' will be used.
    *
    * @param in groovy source code stream
    * @param name code source name
    * @return GroovyCodeSource
    */
   protected GroovyCodeSource createCodeSource(final InputStream in, final String name)
   {
      GroovyCodeSource gcs = SecurityHelper.doPriviledgedAction(new PrivilegedAction<GroovyCodeSource>()
      {
         public GroovyCodeSource run()
         {
            return new GroovyCodeSource(in, name, "/groovy/script/jaxrs");
         }
      });

      gcs.setCachable(false);
      return gcs;
   }
}
