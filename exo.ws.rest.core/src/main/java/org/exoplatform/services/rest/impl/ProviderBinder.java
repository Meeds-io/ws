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
package org.exoplatform.services.rest.impl;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.ContainerObjectFactory;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.PerRequestObjectFactory;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.SingletonObjectFactory;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.provider.ByteEntityProvider;
import org.exoplatform.services.rest.impl.provider.DOMSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.DataSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.FileEntityProvider;
import org.exoplatform.services.rest.impl.provider.InputStreamEntityProvider;
import org.exoplatform.services.rest.impl.provider.JAXBContextResolver;
import org.exoplatform.services.rest.impl.provider.JAXBElementEntityProvider;
import org.exoplatform.services.rest.impl.provider.JAXBObjectEntityProvider;
import org.exoplatform.services.rest.impl.provider.JsonEntityProvider;
import org.exoplatform.services.rest.impl.provider.JsonpEntityProvider;
import org.exoplatform.services.rest.impl.provider.MultipartFormDataEntityProvider;
import org.exoplatform.services.rest.impl.provider.MultivaluedMapEntityProvider;
import org.exoplatform.services.rest.impl.provider.ProviderDescriptorImpl;
import org.exoplatform.services.rest.impl.provider.ReaderEntityProvider;
import org.exoplatform.services.rest.impl.provider.SAXSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamOutputEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StringEntityProvider;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorValidator;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.ExtendedProviders;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.uri.UriPattern;
import org.exoplatform.services.rest.util.MediaTypeMap;
import org.exoplatform.services.rest.util.MediaTypeMultivaluedMap;
import org.exoplatform.services.rest.util.UriPatternMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Prepared set of providers. Users of JAX-RS implementation are not expected to
 * use this class directly. &#64;Context annotation should be used to obtain
 * actual set of providers in RESTful services. As alternative method :
 * <pre>
 * ApplicationContext context = ApplicationContextImpl.getCurrent();
 * Providers providers = context.getProviders();
 * </pre>
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderBinder implements ExtendedProviders
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ProviderBinder.class);

   /** Default providers. */
   private static AtomicReference<ProviderBinder> ainst = new AtomicReference<ProviderBinder>();

   /**
    * Get actual set of providers. Users of JAX-RS implementation are not
    * expected to call this method.
    *
    * @return actual set of providers
    */
   public static ProviderBinder getInstance()
   {
      ProviderBinder t = ainst.get();
      if (t == null)
      {
         ainst.compareAndSet(null, new ProviderBinder());
         t = ainst.get();
      }
      return t;
   }

   /**
    * Set actual set of providers. Users of JAX-RS implementation are not
    * expected to call this method.
    *
    * @param inst actual set of providers
    */
   public static void setInstance(ProviderBinder inst)
   {
      ainst.set(inst);
   }

   public ProviderBinder()
   {
      init();
   }

   /**
    * Add prepared providers.
    */
   protected void init()
   {
      ByteEntityProvider baep = new ByteEntityProvider();
      addMessageBodyReader(baep);
      addMessageBodyWriter(baep);

      DataSourceEntityProvider dsep = new DataSourceEntityProvider();
      addMessageBodyReader(dsep);
      addMessageBodyWriter(dsep);

      DOMSourceEntityProvider domsep = new DOMSourceEntityProvider();
      addMessageBodyReader(domsep);
      addMessageBodyWriter(domsep);

      FileEntityProvider fep = new FileEntityProvider();
      addMessageBodyReader(fep);
      addMessageBodyWriter(fep);

      MultivaluedMapEntityProvider mvep = new MultivaluedMapEntityProvider();
      addMessageBodyReader(mvep);
      addMessageBodyWriter(mvep);

      InputStreamEntityProvider isep = new InputStreamEntityProvider();
      addMessageBodyReader(isep);
      addMessageBodyWriter(isep);

      ReaderEntityProvider rep = new ReaderEntityProvider();
      addMessageBodyReader(rep);
      addMessageBodyWriter(rep);

      SAXSourceEntityProvider saxep = new SAXSourceEntityProvider();
      addMessageBodyReader(saxep);
      addMessageBodyWriter(saxep);

      StreamSourceEntityProvider ssep = new StreamSourceEntityProvider();
      addMessageBodyReader(ssep);
      addMessageBodyWriter(ssep);

      StringEntityProvider sep = new StringEntityProvider();
      addMessageBodyReader(sep);
      addMessageBodyWriter(sep);

      StreamOutputEntityProvider soep = new StreamOutputEntityProvider();
      addMessageBodyReader(soep);
      addMessageBodyWriter(soep);

      JsonEntityProvider jsep = new JsonEntityProvider();
      addMessageBodyReader(jsep);
      addMessageBodyWriter(jsep);

      addMessageBodyWriter(new JsonpEntityProvider());

      // per-request mode , Providers should be injected
      addMessageBodyReader(JAXBElementEntityProvider.class);
      addMessageBodyWriter(JAXBElementEntityProvider.class);

      addMessageBodyReader(JAXBObjectEntityProvider.class);
      addMessageBodyWriter(JAXBObjectEntityProvider.class);

      // per-request mode , HttpServletRequest should be injected in provider
      addMessageBodyReader(MultipartFormDataEntityProvider.class);

      // JAXB context
      addContextResolver(JAXBContextResolver.class, null, ComponentLifecycleScope.CONTAINER);

      addExceptionMapper(new DefaultExceptionMapper());
   }

   /**
    * Read message body providers.
    *
    * @see MediaTypeMultivaluedMap .
    */
   protected final MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>> writeProviders =
      new MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>();

   /**
    * Read message body providers.
    *
    * @see MediaTypeMultivaluedMap .
    */
   protected final MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>> readProviders =
      new MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>();

   /**
    * Exception mappers.
    *
    * @see ExceptionMapper .
    */
   protected final Map<Class<? extends Throwable>, ObjectFactory<ProviderDescriptor>> exceptionMappers =
      new HashMap<Class<? extends Throwable>, ObjectFactory<ProviderDescriptor>>();

   /**
    * Context resolvers.
    *
    * @see ContextResolver .
    */
   protected final Map<Class<?>, MediaTypeMap<ObjectFactory<ProviderDescriptor>>> contextResolvers =
      new HashMap<Class<?>, MediaTypeMap<ObjectFactory<ProviderDescriptor>>>();

   /**
    * Request filters.
    *
    * @see RequestFilter .
    */
   protected final UriPatternMap<ObjectFactory<FilterDescriptor>> requestFilters =
      new UriPatternMap<ObjectFactory<FilterDescriptor>>();

   /**
    * Response filters.
    *
    * @see ResponseFilter .
    */
   protected final UriPatternMap<ObjectFactory<FilterDescriptor>> responseFilters =
      new UriPatternMap<ObjectFactory<FilterDescriptor>>();

   /**
    * Method invoking filters.
    *
    * @see MethodInvokerFilter .
    */
   protected final UriPatternMap<ObjectFactory<FilterDescriptor>> invokerFilters =
      new UriPatternMap<ObjectFactory<FilterDescriptor>>();

   /** Validator. */
   protected final ResourceDescriptorVisitor rdv = ResourceDescriptorValidator.getInstance();

   //

   /**
    * Add per-request ContextResolver.
    *
    * @param clazz class of implementation ContextResolver
    */
   @SuppressWarnings("unchecked")
   public void addContextResolver(Class<? extends ContextResolver> clazz)
   {
      try
      {
         addContextResolver(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ContextResolver " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton ContextResolver.
    *
    * @param instance ContextResolver instance
    */
   @SuppressWarnings("unchecked")
   public void addContextResolver(ContextResolver instance)
   {
      Class<? extends ContextResolver> clazz = instance.getClass();
      try
      {
         addContextResolver(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ContextResolver " + clazz.getName(), e);
      }
   }

   /**
    * Add per-request ExceptionMapper.
    *
    * @param clazz class of implementation ExceptionMapper
    */
   @SuppressWarnings("unchecked")
   public void addExceptionMapper(Class<? extends ExceptionMapper> clazz)
   {
      try
      {
         addExceptionMapper(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ExceptionMapper " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton ExceptionMapper.
    *
    * @param instance ExceptionMapper instance
    */
   @SuppressWarnings("unchecked")
   public void addExceptionMapper(ExceptionMapper instance)
   {
      Class<? extends ExceptionMapper> clazz = instance.getClass();
      try
      {
         addExceptionMapper(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ExceptionMapper " + clazz.getName(), e);
      }
   }

   /**
    * Add per-request MessageBodyReader.
    *
    * @param clazz class of implementation MessageBodyReader
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyReader(Class<? extends MessageBodyReader> clazz)
   {
      try
      {
         addMessageBodyReader(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MessageBodyReader " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton MessageBodyReader.
    *
    * @param instance MessageBodyReader instance
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyReader(MessageBodyReader instance)
   {
      Class<? extends MessageBodyReader> clazz = instance.getClass();
      try
      {
         addMessageBodyReader(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MessageBodyReader " + clazz.getName(), e);
      }
   }

   /**
    * Add per-request MessageBodyWriter.
    *
    * @param clazz class of implementation MessageBodyWriter
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> clazz)
   {
      try
      {
         addMessageBodyWriter(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MessageBodyWriter " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton MessageBodyWriter.
    *
    * @param instance MessageBodyWriter instance
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyWriter(MessageBodyWriter instance)
   {
      Class<? extends MessageBodyWriter> clazz = instance.getClass();
      try
      {
         addMessageBodyWriter(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MessageBodyWriter ", e);
      }
   }

   /**
    * Get list of most acceptable writer's media type for specified type.
    *
    * @param type type
    * @param genericType generic type
    * @param annotations annotations
    * @return sorted acceptable media type collection
    * @see MediaTypeHelper#MEDIA_TYPE_COMPARATOR
    */
   @SuppressWarnings("unchecked")
   public List<MediaType> getAcceptableWriterMediaTypes(Class<?> type, Type genericType, Annotation[] annotations)
   {
      List<MediaType> l = new ArrayList<MediaType>();
      for (Map.Entry<MediaType, List<ObjectFactory<ProviderDescriptor>>> e : writeProviders.entrySet())
      {
         MediaType mime = e.getKey();
         for (ObjectFactory pf : e.getValue())
         {
            MessageBodyWriter writer = (MessageBodyWriter)pf.getInstance(ApplicationContextImpl.getCurrent());
            if (writer.isWriteable(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE))
            {
               l.add(mime);
            }
         }
      }
      Collections.sort(l, MediaTypeHelper.MEDIA_TYPE_COMPARATOR);
      return l;
   }

   /**
    * {@inheritDoc}
    */
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm = contextResolvers.get(contextType);
      ContextResolver<T> resolver = null;
      if (pm != null)
      {
         if (mediaType == null)
         {
            return doGetContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);
         }
         resolver = doGetContextResolver(pm, contextType, mediaType);
         if (resolver == null && !mediaType.isWildcardSubtype())
         {
            resolver =
               doGetContextResolver(pm, contextType, new MediaType(mediaType.getType(), MediaType.MEDIA_TYPE_WILDCARD));
         }
         if (resolver == null && !mediaType.isWildcardType())
         {
            resolver = doGetContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);
         }
      }
      return resolver;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      ObjectFactory pf = exceptionMappers.get(type);
      if (pf != null)
      {
         return (ExceptionMapper<T>)pf.getInstance(ApplicationContextImpl.getCurrent());
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType)
   {
      if (mediaType == null)
      {
         return doGetMessageBodyReader(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
      }
      MessageBodyReader<T> reader = doGetMessageBodyReader(type, genericType, annotations, mediaType);
      if (reader == null && !mediaType.isWildcardSubtype())
      {
         reader =
            doGetMessageBodyReader(type, genericType, annotations, new MediaType(mediaType.getType(),
               MediaType.MEDIA_TYPE_WILDCARD));
      }
      if (reader == null && !mediaType.isWildcardType())
      {
         reader = doGetMessageBodyReader(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
      }
      return reader;
   }

   /**
    * {@inheritDoc}
    */
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType)
   {
      if (mediaType == null)
      {
         return doGetMessageBodyWriter(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
      }
      MessageBodyWriter<T> writer = doGetMessageBodyWriter(type, genericType, annotations, mediaType);
      if (writer == null && !mediaType.isWildcardSubtype())
      {
         writer =
            doGetMessageBodyWriter(type, genericType, annotations, new MediaType(mediaType.getType(),
               MediaType.MEDIA_TYPE_WILDCARD));
      }
      if (writer == null && !mediaType.isWildcardType())
      {
         writer = doGetMessageBodyWriter(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
      }
      return writer;
   }

   /**
    * Add per-request MethodInvokerFilter.
    *
    * @param clazz class of implementation MethodInvokerFilter
    */
   public void addMethodInvokerFilter(Class<? extends MethodInvokerFilter> clazz)
   {
      try
      {
         addMethodInvokerFilter(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MethodInvokerFilter " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton MethodInvokerFilter.
    *
    * @param instance MethodInvokerFilter instance
    */
   public void addMethodInvokerFilter(MethodInvokerFilter instance)
   {
      Class<? extends MethodInvokerFilter> clazz = instance.getClass();
      try
      {
         addMethodInvokerFilter(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add RequestFilter " + clazz.getName(), e);
      }
   }

   /**
    * Add per-request RequestFilter.
    *
    * @param clazz class of implementation RequestFilter
    */
   public void addRequestFilter(Class<? extends RequestFilter> clazz)
   {
      try
      {
         addRequestFilter(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add MethodInvokerFilter " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton RequestFilter.
    *
    * @param instance RequestFilter instance
    */
   public void addRequestFilter(RequestFilter instance)
   {
      Class<? extends RequestFilter> clazz = instance.getClass();
      try
      {
         addRequestFilter(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add RequestFilter " + clazz.getName(), e);
      }
   }

   /**
    * Add per-request ResponseFilter.
    *
    * @param clazz class of implementation ResponseFilter
    */
   public void addResponseFilter(Class<? extends ResponseFilter> clazz)
   {
      try
      {
         addResponseFilter(clazz, null, ComponentLifecycleScope.PER_REQUEST);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ResponseFilter " + clazz.getName(), e);
      }
   }

   /**
    * Add singleton ResponseFilter.
    *
    * @param instance ResponseFilter instance
    */
   public void addResponseFilter(ResponseFilter instance)
   {
      Class<? extends ResponseFilter> clazz = instance.getClass();
      try
      {
         addResponseFilter(clazz, instance, ComponentLifecycleScope.SINGLETON);
      }
      catch (Exception e)
      {
         LOG.error("Failed add ResponseFilter " + clazz.getName(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<ObjectFactory<FilterDescriptor>> getMethodInvokerFilters(String path)
   {
      return getMatchedFilters(path, invokerFilters);
   }

   /**
    * {@inheritDoc}
    */
   public List<ObjectFactory<FilterDescriptor>> getRequestFilters(String path)
   {
      return getMatchedFilters(path, requestFilters);
   }

   /**
    * {@inheritDoc}
    */
   public List<ObjectFactory<FilterDescriptor>> getResponseFilters(String path)
   {
      return getMatchedFilters(path, responseFilters);
   }

   /**
    * @param path request path
    * @param m filter map
    * @return acceptable filter
    * @see #getMethodInvokerFilters(String)
    * @see #getRequestFilters(String)
    * @see #getResponseFilters(String)
    */
   protected List<ObjectFactory<FilterDescriptor>> getMatchedFilters(String path,
      UriPatternMap<ObjectFactory<FilterDescriptor>> m)
   {
      List<ObjectFactory<FilterDescriptor>> l = new ArrayList<ObjectFactory<FilterDescriptor>>();
      List<String> capturingValues = new ArrayList<String>();
      for (Map.Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : m.entrySet())
      {
         UriPattern uriPattern = e.getKey();
         if (uriPattern != null)
         {
            if (e.getKey().match(path, capturingValues))
            {
               int len = capturingValues.size();
               if (capturingValues.get(len - 1) != null && !"/".equals(capturingValues.get(len - 1)))
               {
                  continue; // not matched
               }
            }
            else
            {
               continue; // not matched
            }
         }
         // if matched or UriPattern is null
         l.addAll(e.getValue());
      }
      return l;
   }

   /**
    * @param <T> context resolver actual type argument
    * @param pm MediaTypeMap that contains ProviderFactories that may produce
    *        objects that are instance of T
    * @param contextType context type
    * @param mediaType media type that can be used to restrict context resolver
    *        choose
    * @return ContextResolver or null if nothing was found
    */
   @SuppressWarnings("unchecked")
   protected <T> ContextResolver<T> doGetContextResolver(MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm,
      Class<T> contextType, MediaType mediaType)
   {
      for (Map.Entry<MediaType, ObjectFactory<ProviderDescriptor>> e : pm.entrySet())
      {
         if (mediaType.isCompatible(e.getKey()))
         {
            return (ContextResolver<T>)e.getValue().getInstance(ApplicationContextImpl.getCurrent());
         }
      }
      return null;
   }

   /**
    * Looking for message body reader according to supplied entity class, entity
    * generic type, annotations and content type.
    *
    * @param <T> message body reader actual type argument
    * @param type entity type
    * @param genericType entity generic type
    * @param annotations annotations
    * @param mediaType entity content type
    * @return message body reader or null if no one was found.
    */
   @SuppressWarnings("unchecked")
   protected <T> MessageBodyReader<T> doGetMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType)
   {
      for (ObjectFactory pf : readProviders.getList(mediaType))
      {
         MessageBodyReader reader = (MessageBodyReader)pf.getInstance(ApplicationContextImpl.getCurrent());
         if (reader.isReadable(type, genericType, annotations, mediaType))
         {
            return reader;
         }
      }
      return null;
   }

   /**
    * Looking for message body writer according to supplied entity class, entity
    * generic type, annotations and content type.
    *
    * @param <T> message body writer actual type argument
    * @param type entity type
    * @param genericType entity generic type
    * @param annotations annotations
    * @param mediaType content type in which entity should be represented
    * @return message body writer or null if no one was found.
    */
   @SuppressWarnings("unchecked")
   protected <T> MessageBodyWriter<T> doGetMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType)
   {
      for (ObjectFactory pf : writeProviders.getList(mediaType))
      {
         MessageBodyWriter writer = (MessageBodyWriter)pf.getInstance(ApplicationContextImpl.getCurrent());
         if (writer.isWriteable(type, genericType, annotations, mediaType))
         {
            return writer;
         }
      }
      return null;
   }

   /**
    * @param clazz ContextResolver class
    * @param instance ContextResolver instance, may be null if not singleton
    *        instance
    * @param scope ComponentLifecycleScope
    */
   @SuppressWarnings("unchecked")
   public void addContextResolver(Class<? extends ContextResolver> clazz, ContextResolver instance,
      ComponentLifecycleScope scope)
   {
      for (Type type : clazz.getGenericInterfaces())
      {
         if (type instanceof ParameterizedType)
         {
            ParameterizedType pt = (ParameterizedType)type;
            if (ContextResolver.class == pt.getRawType())
            {
               Type[] atypes = pt.getActualTypeArguments();
               if (atypes.length > 1)
               {
                  throw new RuntimeException("Unable strong determine actual type argument, more then one type found.");
               }

               Class<?> aclazz = (Class<?>)atypes[0];

               MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm = contextResolvers.get(aclazz);

               if (pm == null)
               {
                  pm = new MediaTypeMap<ObjectFactory<ProviderDescriptor>>();
                  contextResolvers.put(aclazz, pm);
               }

               ProviderDescriptor descriptor = new ProviderDescriptorImpl(clazz);
               descriptor.accept(rdv);

               ObjectFactory<ProviderDescriptor> factory = null;
               switch (scope)
               {
                  case PER_REQUEST :
                     factory = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
                     break;
                  case SINGLETON :
                     if (instance == null)
                     {
                        throw new IllegalArgumentException("ContextResolver instance is null.");
                     }
                     factory = new SingletonObjectFactory<ProviderDescriptor>(descriptor, instance);
                     break;
                  case CONTAINER :
                     factory = new ContainerObjectFactory<ProviderDescriptor>(descriptor);
                     break;
               }

               if (factory == null)
                  throw new RuntimeException("No ObjectFactory could be found");
               for (MediaType mime : factory.getObjectModel().produces())
               {
                  if (pm.get(mime) != null)
                  {
                     String msg =
                        "ContextResolver for " + aclazz.getName() + " and media type " + mime + " already registered.";
                     throw new RuntimeException(msg);
                  }
                  else
                  {
                     pm.put(mime, factory);
                  }
               }
            }
         }
      }
   }

   /**
    * @param clazz MessageBodyreader class
    * @param instance MessageBodyReader, may be null if not singleton instance
    * @param scope ComponentLifecycleScope
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyReader(Class<? extends MessageBodyReader> clazz, MessageBodyReader instance,
      ComponentLifecycleScope scope)
   {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(clazz);
      descriptor.accept(rdv);

      ObjectFactory<ProviderDescriptor> factory = null;
      switch (scope)
      {
         case PER_REQUEST :
            factory = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
            break;
         case SINGLETON :
            if (instance == null)
            {
               throw new IllegalArgumentException("MessageBodyReader instance is null.");
            }
            factory = new SingletonObjectFactory<ProviderDescriptor>(descriptor, instance);
            break;
         case CONTAINER :
            factory = new ContainerObjectFactory<ProviderDescriptor>(descriptor);
            break;
      }
      if (factory == null)
         throw new RuntimeException("No ObjectFactory could be found");
      // MessageBodyReader is smart component and can determine which type it
      // supports, see method MessageBodyReader.isReadable. So here does not
      // check is reader for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : factory.getObjectModel().consumes())
      {
         readProviders.getList(mime).add(factory);
      }
   }

   /**
    * @param clazz MessageBodyWriter class
    * @param instance MessageBodyWriter, may be null if not singleton instance
    * @param scope ComponentLifecycleScope
    */
   @SuppressWarnings("unchecked")
   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> clazz, MessageBodyWriter instance,
      ComponentLifecycleScope scope)
   {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(clazz);
      descriptor.accept(rdv);

      ObjectFactory<ProviderDescriptor> factory = null;
      switch (scope)
      {
         case PER_REQUEST :
            factory = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
            break;
         case SINGLETON :
            if (instance == null)
            {
               throw new IllegalArgumentException("MessageBodyWriter instance is null.");
            }
            factory = new SingletonObjectFactory<ProviderDescriptor>(descriptor, instance);
            break;
         case CONTAINER :
            factory = new ContainerObjectFactory<ProviderDescriptor>(descriptor);
            break;
      }
      if (factory == null)
         throw new RuntimeException("No ObjectFactory could be found");
      // MessageBodyWriter is smart component and can determine which type it
      // supports, see method MessageBodyWriter.isWriteable. So here does not
      // check is writer for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : factory.getObjectModel().produces())
      {
         writeProviders.getList(mime).add(factory);
      }
   }

   /**
    * @param clazz ExceptionMapper class
    * @param instance ExceptionMapper instance, may be null if not singleton
    *        instance
    * @param scope ComponentLifecycleScope
    */
   @SuppressWarnings("unchecked")
   public void addExceptionMapper(Class<? extends ExceptionMapper> clazz, ExceptionMapper instance,
      ComponentLifecycleScope scope)
   {
      for (Type type : clazz.getGenericInterfaces())
      {
         if (type instanceof ParameterizedType)
         {
            ParameterizedType pt = (ParameterizedType)type;
            if (ExceptionMapper.class == pt.getRawType())
            {
               Type[] atypes = pt.getActualTypeArguments();
               if (atypes.length > 1)
                  throw new RuntimeException("Unable strong determine actual type argument, more then one type found.");

               Class<? extends Throwable> exc = (Class<? extends Throwable>)atypes[0];

               if (exceptionMappers.get(exc) != null)
               {
                  String msg = "ExceptionMapper for exception " + exc + " already registered.";
                  throw new RuntimeException(msg);
               }

               ProviderDescriptor descriptor = new ProviderDescriptorImpl(clazz);
               descriptor.accept(rdv);
               ObjectFactory<ProviderDescriptor> factory = null;

               switch (scope)
               {
                  case PER_REQUEST :
                     factory = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
                     break;
                  case SINGLETON :
                     if (instance == null)
                     {
                        throw new IllegalArgumentException("ExceptionMapper instance is null.");
                     }
                     factory = new SingletonObjectFactory<ProviderDescriptor>(descriptor, instance);
                     break;
                  case CONTAINER :
                     factory = new ContainerObjectFactory<ProviderDescriptor>(descriptor);
                     break;
               }
               exceptionMappers.put(exc, factory);
            }
         }
      }
   }

   /**
    * @param clazz RequestFilter class
    * @param instance RequestFilter instance, may be null if not singleton
    *        instance
    * @param scope ComponentLifecycleScope
    */
   public void addRequestFilter(Class<? extends RequestFilter> clazz, RequestFilter instance,
      ComponentLifecycleScope scope)
   {
      FilterDescriptor descriptor = new FilterDescriptorImpl(clazz);
      descriptor.accept(rdv);

      ObjectFactory<FilterDescriptor> factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      switch (scope)
      {
         case PER_REQUEST :
            factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
            break;
         case SINGLETON :
            if (instance == null)
            {
               throw new IllegalArgumentException("RequestFilter instance is null.");
            }
            factory = new SingletonObjectFactory<FilterDescriptor>(descriptor, instance);
            break;
         case CONTAINER :
            factory = new ContainerObjectFactory<FilterDescriptor>(descriptor);
            break;
      }
      requestFilters.getList(descriptor.getUriPattern()).add(factory);
   }

   /**
    * @param clazz ResponseFilter class
    * @param instance ResponseFilter instance, may be null if not singleton
    *        instance
    * @param scope ComponentLifecycleScope
    */
   public void addResponseFilter(Class<? extends ResponseFilter> clazz, ResponseFilter instance,
      ComponentLifecycleScope scope)
   {
      FilterDescriptor descriptor = new FilterDescriptorImpl(clazz);
      descriptor.accept(rdv);

      ObjectFactory<FilterDescriptor> factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      switch (scope)
      {
         case PER_REQUEST :
            factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
            break;
         case SINGLETON :
            if (instance == null)
            {
               throw new IllegalArgumentException("ResponseFilter instance is null.");
            }
            factory = new SingletonObjectFactory<FilterDescriptor>(descriptor, instance);
            break;
         case CONTAINER :
            factory = new ContainerObjectFactory<FilterDescriptor>(descriptor);
            break;
      }
      responseFilters.getList(descriptor.getUriPattern()).add(factory);
   }

   /**
    * @param clazz MethodInvokerFilter class
    * @param instance MethodInvokerFilter instance, may be null if not singleton
    *        instance
    * @param scope ComponentLifecycleScope
    */
   public void addMethodInvokerFilter(Class<? extends MethodInvokerFilter> clazz, MethodInvokerFilter instance,
      ComponentLifecycleScope scope)
   {
      FilterDescriptor descriptor = new FilterDescriptorImpl(clazz);
      descriptor.accept(rdv);

      ObjectFactory<FilterDescriptor> factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      switch (scope)
      {
         case PER_REQUEST :
            factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
            break;
         case SINGLETON :
            if (instance == null)
            {
               throw new IllegalArgumentException("MethodInvokerFilter instance is null.");
            }
            factory = new SingletonObjectFactory<FilterDescriptor>(descriptor, instance);
            break;
         case CONTAINER :
            factory = new ContainerObjectFactory<FilterDescriptor>(descriptor);
            break;
      }
      invokerFilters.getList(descriptor.getUriPattern()).add(factory);
   }

}
