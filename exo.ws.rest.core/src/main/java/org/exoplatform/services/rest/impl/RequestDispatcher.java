/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.services.rest.impl;

import org.exoplatform.container.spi.DefinitionByType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.SingletonObjectFactory;
import org.exoplatform.services.rest.impl.header.HeaderHelper;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.method.MethodInvokerFactory;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.impl.resource.ApplicationResource;
import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodMap;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorMap;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodMap;
import org.exoplatform.services.rest.uri.UriPattern;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Lookup resource which can serve request.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DefinitionByType
public class RequestDispatcher
{

   /**
    * Set of providers which respects providers specified by JAX-RS
    * Applications. Default (embedded) providers will be used only if
    * application does not provide own providers with the same purposes.
    */
   private class ProvidersAdapter extends ProviderBinder
   {
      private ProviderBinder applicationProviders;

      private ProviderBinder defaultProviders;

      private ProvidersAdapter(ProviderBinder applicationProviders, ProviderBinder defaultProviders)
      {
         this.applicationProviders = applicationProviders;
         this.defaultProviders = defaultProviders;
      }

      @Override
      protected void init()
      {
         // Do not add default providers here.
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addContextResolver(Class<? extends ContextResolver> clazz, ContextResolver instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addContextResolver(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addContextResolver");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addContextResolver(Class<? extends ContextResolver> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addContextResolver(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addContextResolver");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addContextResolver(ContextResolver instance)
      {
         if (applicationProviders != null)
            applicationProviders.addContextResolver(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addContextResolver");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addExceptionMapper(Class<? extends ExceptionMapper> clazz, ExceptionMapper instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addExceptionMapper(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addExceptionMapper");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addExceptionMapper(Class<? extends ExceptionMapper> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addExceptionMapper(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addExceptionMapper");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addExceptionMapper(ExceptionMapper instance)
      {
         if (applicationProviders != null)
            applicationProviders.addExceptionMapper(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addExceptionMapper");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyReader(Class<? extends MessageBodyReader> clazz, MessageBodyReader instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyReader(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyReader");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyReader(Class<? extends MessageBodyReader> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyReader(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyReader");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyReader(MessageBodyReader instance)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyReader(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyReader");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyWriter(Class<? extends MessageBodyWriter> clazz, MessageBodyWriter instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyWriter(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyWriter");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyWriter(Class<? extends MessageBodyWriter> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyWriter(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyWriter");
      }

      @SuppressWarnings("unchecked")
      @Override
      public void addMessageBodyWriter(MessageBodyWriter instance)
      {
         if (applicationProviders != null)
            applicationProviders.addMessageBodyWriter(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMessageBodyWriter");
      }

      @Override
      public void addMethodInvokerFilter(Class<? extends MethodInvokerFilter> clazz, MethodInvokerFilter instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addMethodInvokerFilter(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMethodInvokerFilter");
      }

      @Override
      public void addMethodInvokerFilter(Class<? extends MethodInvokerFilter> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addMethodInvokerFilter(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMethodInvokerFilter");
      }

      @Override
      public void addMethodInvokerFilter(MethodInvokerFilter instance)
      {
         if (applicationProviders != null)
            applicationProviders.addMethodInvokerFilter(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addMethodInvokerFilter");
      }

      @Override
      public void addRequestFilter(Class<? extends RequestFilter> clazz, RequestFilter instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addRequestFilter(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addRequestFilter");
      }

      @Override
      public void addRequestFilter(Class<? extends RequestFilter> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addRequestFilter(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addRequestFilter");
      }

      @Override
      public void addRequestFilter(RequestFilter instance)
      {
         if (applicationProviders != null)
            applicationProviders.addRequestFilter(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addRequestFilter");
      }

      @Override
      public void addResponseFilter(Class<? extends ResponseFilter> clazz, ResponseFilter instance,
         ComponentLifecycleScope scope)
      {
         if (applicationProviders != null)
            applicationProviders.addResponseFilter(clazz, instance, scope);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addResponseFilter");
      }

      @Override
      public void addResponseFilter(Class<? extends ResponseFilter> clazz)
      {
         if (applicationProviders != null)
            applicationProviders.addResponseFilter(clazz);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addResponseFilter");
      }

      @Override
      public void addResponseFilter(ResponseFilter instance)
      {
         if (applicationProviders != null)
            applicationProviders.addResponseFilter(instance);
         else
            // Keep default set of providers untouched.
            throw new UnsupportedOperationException("addResponseFilter");
      }

      @Override
      public List<MediaType> getAcceptableWriterMediaTypes(Class<?> type, Type genericType, Annotation[] annotations)
      {
         List<MediaType> mediaTypes = null;
         if (applicationProviders != null)
            mediaTypes = applicationProviders.getAcceptableWriterMediaTypes(type, genericType, annotations);
         if (mediaTypes != null)
         {
            mediaTypes.addAll(defaultProviders.getAcceptableWriterMediaTypes(type, genericType, annotations));
            return mediaTypes;
         }
         return defaultProviders.getAcceptableWriterMediaTypes(type, genericType, annotations);
      }

      @Override
      public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
      {
         ContextResolver<T> resolver = null;
         if (applicationProviders != null)
            resolver = applicationProviders.getContextResolver(contextType, mediaType);
         if (resolver == null)
            resolver = defaultProviders.getContextResolver(contextType, mediaType);
         return resolver;
      }

      @Override
      public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
      {
         ExceptionMapper<T> mapper = null;
         if (applicationProviders != null)
            mapper = applicationProviders.getExceptionMapper(type);
         if (mapper == null)
            mapper = defaultProviders.getExceptionMapper(type);
         return mapper;
      }

      @Override
      public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
      {
         MessageBodyReader<T> reader = null;
         if (applicationProviders != null)
            reader = applicationProviders.getMessageBodyReader(type, genericType, annotations, mediaType);
         if (reader == null)
            reader = defaultProviders.getMessageBodyReader(type, genericType, annotations, mediaType);
         return reader;
      }

      @Override
      public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
      {
         MessageBodyWriter<T> writer = null;
         if (applicationProviders != null)
            writer = applicationProviders.getMessageBodyWriter(type, genericType, annotations, mediaType);
         if (writer == null)
            writer = defaultProviders.getMessageBodyWriter(type, genericType, annotations, mediaType);
         return writer;
      }

      @Override
      public List<ObjectFactory<FilterDescriptor>> getMethodInvokerFilters(String path)
      {
         List<ObjectFactory<FilterDescriptor>> filters = defaultProviders.getMethodInvokerFilters(path);
         if (applicationProviders != null)
            filters.addAll(applicationProviders.getMethodInvokerFilters(path));
         return filters;
      }

      @Override
      public List<ObjectFactory<FilterDescriptor>> getRequestFilters(String path)
      {
         // NOTE!!! Return only application specific filters. Default filters
         // should be already applied to request.
         if (applicationProviders == null)
            return Collections.emptyList();
         return applicationProviders.getRequestFilters(path);
      }

      @Override
      public List<ObjectFactory<FilterDescriptor>> getResponseFilters(String path)
      {
         // NOTE!!! Return only application specific filters. Default filters
         // should be applied to response later.
         if (applicationProviders == null)
            return Collections.emptyList();
         return applicationProviders.getResponseFilters(path);
      }

   }

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.RequestDispatcher");

   /** See {@link ResourceBinder}. */
   protected final ResourceBinder resourceBinder;

   protected final MethodInvokerFactory invokerFactory;

   protected final ProvidersRegistry providersRegistry;

   public RequestDispatcher(ResourceBinder resourceBinder, ProvidersRegistry providersRegistry,
      MethodInvokerFactory invokerFactory)
   {
      this.resourceBinder = resourceBinder;
      this.providersRegistry = providersRegistry;
      this.invokerFactory = invokerFactory;
   }

   public RequestDispatcher(ResourceBinder resourceBinder, ProvidersRegistry providers)
   {
      this(resourceBinder, providers, null);
   }

   @Deprecated
   public RequestDispatcher(ResourceBinder resourceBinder, MethodInvokerFactory invokerFactory)
   {
      this(resourceBinder, null, invokerFactory);
   }

   @Deprecated
   public RequestDispatcher(ResourceBinder resourceBinder)
   {
      this(resourceBinder, null, null);
   }

   /**
    * Dispatch {@link ContainerRequest} to resource which can serve request.
    * 
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    */
   public void dispatch(GenericContainerRequest request, GenericContainerResponse response)
   {
      ApplicationContext context = ApplicationContextImpl.getCurrent();
      String requestPath = context.getPath(false);
      List<String> parameterValues = context.getParameterValues();

      // Get root resource
      ObjectFactory<AbstractResourceDescriptor> resourceFactory = getRootResourse(parameterValues, requestPath);
      AbstractResourceDescriptor resourceDescriptor = resourceFactory.getObjectModel();

      if (providersRegistry != null)
      {
         // Be sure instance of ProvidersRegistry injected if this class is extended.
         String applicationId = null;
         if (resourceDescriptor instanceof ApplicationResource)
         {
            // If resource delivered with subclass of javax.ws.rs.core.Application
            // it must be instance of ApplicationResource which provide application's identifier.
            applicationId = ((ApplicationResource)resourceDescriptor).getApplication();
         }
         ProviderBinder applicationProviders = providersRegistry.getProviders(applicationId);
         ((ApplicationContextImpl)context).setProviders(new ProvidersAdapter(applicationProviders, ProviderBinder
            .getInstance()));
      }
      else
      {
         LOG.warn("ProvidersRegistry must set. ");
      }

      // Apply application specific request filters if any
      for (ObjectFactory<FilterDescriptor> factory : context.getProviders().getRequestFilters(context.getPath()))
      {
         RequestFilter f = (RequestFilter)factory.getInstance(context);
         f.doFilter(request);
      }

      // Take the tail of the request path, the tail will be requested path
      // for lower resources, e. g. ResourceClass -> Sub-resource method/locator
      String newRequestPath = getPathTail(parameterValues);
      // save the resource class URI in hierarchy
      context.addMatchedURI(requestPath.substring(0, requestPath.lastIndexOf(newRequestPath)));
      context.setParameterNames(resourceFactory.getObjectModel().getUriPattern().getParameterNames());

      // may thrown WebApplicationException
      Object resource = resourceFactory.getInstance(context);
      dispatch(request, response, context, resourceFactory, resource, newRequestPath);

      // Apply application specific response filters if any
      for (ObjectFactory<FilterDescriptor> factory : context.getProviders().getResponseFilters(context.getPath()))
      {
         ResponseFilter f = (ResponseFilter)factory.getInstance(context);
         f.doFilter(response);
      }
   }

   /**
    * Get last element from path parameters. This element will be used as
    * request path for child resources.
    * 
    * @param parameterValues See
    *           {@link ApplicationContextImpl#getParameterValues()}
    * @return last element from given list or empty string if last element is
    *         null
    */
   private static String getPathTail(List<String> parameterValues)
   {
      int i = parameterValues.size() - 1;
      return parameterValues.get(i) != null ? parameterValues.get(i) : "";
   }

   /**
    * Process resource methods, sub-resource methods and sub-resource locators
    * to find the best one for serve request.
    * 
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @param context See {@link ApplicationContextImpl}
    * @param resourceFactory the root resource factory or resource factory which
    *           was created by previous sub-resource locator
    * @param resource instance of resource class
    * @param requestPath request path, it is relative path to the base URI or
    *           other resource which was called before (one of sub-resource
    *           locators)
    */
   private void dispatch(GenericContainerRequest request, GenericContainerResponse response,
      ApplicationContext context, ObjectFactory<AbstractResourceDescriptor> resourceFactory, Object resource,
      String requestPath)
   {
      List<String> parameterValues = context.getParameterValues();
      int len = parameterValues.size();
      // resource method or sub-resource method or sub-resource locator
      ResourceMethodMap<ResourceMethodDescriptor> rmm = resourceFactory.getObjectModel().getResourceMethods();
      SubResourceMethodMap srmm = resourceFactory.getObjectModel().getSubResourceMethods();
      SubResourceLocatorMap srlm = resourceFactory.getObjectModel().getSubResourceLocators();
      if ((parameterValues.get(len - 1) == null || "/".equals(parameterValues.get(len - 1))) && rmm.size() > 0)
      {
         // resource method, then process HTTP method and consume/produce media types
         List<ResourceMethodDescriptor> methods = new ArrayList<ResourceMethodDescriptor>();
         boolean match = processResourceMethod(rmm, request, response, methods);
         if (!match)
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Not found resource method for method " + request.getMethod());
            return; // Error Response is preset
         }
         invokeResourceMethod(methods.get(0), resource, context, request, response);
      }
      else
      { // sub-resource method/locator
         List<SubResourceMethodDescriptor> methods = new ArrayList<SubResourceMethodDescriptor>();
         // check sub-resource methods
         boolean match = processSubResourceMethod(srmm, requestPath, request, response, parameterValues, methods);
         // check sub-resource locators
         List<SubResourceLocatorDescriptor> locators = new ArrayList<SubResourceLocatorDescriptor>();
         boolean hasAcceptableLocator = processSubResourceLocator(srlm, requestPath, parameterValues, locators);

         // Sub-resource method or sub-resource locator should be found,
         // otherwise error response with corresponding status.
         // If sub-resource locator not found status must be Not Found (404).
         // If sub-resource method not found then can be few statuses to
         // return, in this case don't care about locators, just return status
         // for sub-resource method. If no one method found then status will
         // Not Found (404) anyway.
         if (!match && !hasAcceptableLocator)
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Not found sub-resource methods nor sub-resource locators for path " + requestPath
                  + " and method " + request.getMethod());
            return; // Error Response is preset
         }

         // Sub-resource method, sub-resource locator or both acceptable.
         // If both, sub-resource method and sub-resource then do next:
         // Check number of characters and number of variables in URI pattern, if
         // the same then sub-resource method has higher priority, otherwise
         // sub-resource with 'higher' URI pattern selected.
         if ((!hasAcceptableLocator && match)
            || (hasAcceptableLocator && match && compareSubResources(methods.get(0), locators.get(0)) < 0))
         {
            // sub-resource method
            invokeSubResourceMethod(requestPath, methods.get(0), resource, context, request, response);
         }
         else if ((hasAcceptableLocator && !match)
            || (hasAcceptableLocator && match && compareSubResources(methods.get(0), locators.get(0)) > 0))
         {
            // sub-resource locator
            invokeSuResourceLocator(requestPath, locators.get(0), resource, context, request, response);
         }
      }
   }

   /**
    * Invoke resource methods.
    * 
    * @param rmd See {@link ResourceMethodDescriptor}
    * @param resource instance of resource class
    * @param context See {@link ApplicationContextImpl}
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @see ResourceMethodDescriptor
    */
   private void invokeResourceMethod(ResourceMethodDescriptor rmd, Object resource, ApplicationContext context,
      GenericContainerRequest request, GenericContainerResponse response)
   {
      // save resource in hierarchy
      context.addMatchedResource(resource);
      Class<?> returnType = rmd.getResponseType();
      MethodInvoker invoker = rmd.getMethodInvoker();
      Object o = invoker.invokeMethod(resource, rmd, context);
      processResponse(o, returnType, request, response, rmd.produces());
   }

   /**
    * Invoke sub-resource methods.
    * 
    * @param requestPath request path
    * @param srmd See {@link SubResourceMethodDescriptor}
    * @param resource instance of resource class
    * @param context See {@link ApplicationContextImpl}
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @see SubResourceMethodDescriptor
    */
   private void invokeSubResourceMethod(String requestPath, SubResourceMethodDescriptor srmd, Object resource,
      ApplicationContext context, GenericContainerRequest request, GenericContainerResponse response)
   {
      // save resource in hierarchy
      context.addMatchedResource(resource);
      // save the sub-resource method URI in hierarchy
      context.addMatchedURI(requestPath);
      // save parameters values, actually parameters was save before, now just map parameter's names to values
      context.setParameterNames(srmd.getUriPattern().getParameterNames());

      Class<?> returnType = srmd.getResponseType();
      MethodInvoker invoker = srmd.getMethodInvoker();
      Object o = invoker.invokeMethod(resource, srmd, context);
      processResponse(o, returnType, request, response, srmd.produces());
   }

   /**
    * Invoke sub-resource locators.
    * 
    * @param requestPath request path
    * @param srld See {@link SubResourceLocatorDescriptor}
    * @param resource instance of resource class
    * @param context See {@link ApplicationContextImpl}
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @see SubResourceLocatorDescriptor
    */
   private void invokeSuResourceLocator(String requestPath, SubResourceLocatorDescriptor srld, Object resource,
      ApplicationContext context, GenericContainerRequest request, GenericContainerResponse response)
   {
      context.addMatchedResource(resource);
      // take the tail of the request path, the tail will be new request path for lower resources
      String newRequestPath = getPathTail(context.getParameterValues());
      // save the resource class URI in hierarchy
      context.addMatchedURI(requestPath.substring(0, requestPath.lastIndexOf(newRequestPath)));
      // save parameters values, actually parameters was save before, now just map parameter's names to values
      context.setParameterNames(srld.getUriPattern().getParameterNames());

      // NOTE Locators can't accept entity
      MethodInvoker invoker = srld.getMethodInvoker();
      resource = invoker.invokeMethod(resource, srld, context);

      AbstractResourceDescriptor descriptor = new AbstractResourceDescriptorImpl(resource, invokerFactory);
      SingletonObjectFactory<AbstractResourceDescriptor> locResource =
         new SingletonObjectFactory<AbstractResourceDescriptor>(descriptor, resource);

      // dispatch again newly created resource
      dispatch(request, response, context, locResource, resource, newRequestPath);
   }

   /**
    * Compare two sub-resources. One of it is
    * {@link SubResourceMethodDescriptor} and other one id
    * {@link SubResourceLocatorDescriptor}. First compare UriPattern, see
    * {@link UriPattern#URIPATTERN_COMPARATOR}. NOTE URI comparator compare
    * UriPattrens for descending sorting. So it it return negative integer then
    * it minds SubResourceMethodDescriptor has higher priority by UriPattern
    * comparison. If comparator return positive integer then
    * SubResourceLocatorDescriptor has higher priority. And finally if zero was
    * returned then UriPattern is equals, in this case
    * SubResourceMethodDescriptor must be selected.
    * 
    * @param srmd See {@link SubResourceMethodDescriptor}
    * @param srld See {@link SubResourceLocatorDescriptor}
    * @return result of comparison sub-resources
    */
   private int compareSubResources(SubResourceMethodDescriptor srmd, SubResourceLocatorDescriptor srld)
   {
      int r = UriPattern.URIPATTERN_COMPARATOR.compare(srmd.getUriPattern(), srld.getUriPattern());
      // NOTE If patterns are the same sub-resource method has priority
      if (r == 0)
      {
         return -1;
      }
      return r;
   }

   /**
    * Process result of invoked method, and set {@link Response} parameters
    * dependent of returned object.
    * 
    * @param o result of invoked method
    * @param returnType type of returned object
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @param produces list of method produces media types
    * @see ResourceMethodDescriptor
    * @see SubResourceMethodDescriptor
    * @see SubResourceLocatorDescriptor
    */
   private static void processResponse(Object o, Class<?> returnType, GenericContainerRequest request,
      GenericContainerResponse response, List<MediaType> produces)
   {
      // get most acceptable media type for response
      MediaType contentType = request.getAcceptableMediaType(produces);

      if (returnType == void.class || o == null)
      {
         response.setResponse(Response.noContent().build());
      }
      else if (Response.class.isAssignableFrom(returnType))
      {
         Response r = (Response)o;
         // If content-type is not set then add it
         if (r.getMetadata().getFirst(HttpHeaders.CONTENT_TYPE) == null && r.getEntity() != null)
         {
            r.getMetadata().putSingle(HttpHeaders.CONTENT_TYPE, contentType);
         }
         response.setResponse(r);
      }
      else if (GenericEntity.class.isAssignableFrom(returnType))
      {
         response.setResponse(Response.ok(o, contentType).build());
      }
      else
      {
         response.setResponse(Response.ok(o, contentType).build());
      }
   }

   /**
    * Process resource methods.
    * 
    * @param <T> ResourceMethodDescriptor extension
    * @param rmm See {@link ResourceMethodMap}
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @param methods list for method resources
    * @return true if at least one resource method found false otherwise
    */
   private static <T extends ResourceMethodDescriptor> boolean processResourceMethod(ResourceMethodMap<T> rmm,
      GenericContainerRequest request, GenericContainerResponse response, List<T> methods)
   {
      String method = request.getMethod();
      List<T> rmds = rmm.getList(method);
      if (rmds == null || rmds.size() == 0)
      {
         response.setResponse(Response.status(405).header("Allow", HeaderHelper.convertToString(rmm.getAllow()))
            .entity(method + " method is not allowed for resource " + ApplicationContextImpl.getCurrent().getPath())
            .type(MediaType.TEXT_PLAIN).build());
         return false;
      }
      MediaType contentType = request.getMediaType();
      if (contentType == null)
      {
         methods.addAll(rmds);
      }
      else
      {
         for (T rmd : rmds)
         {
            if (MediaTypeHelper.isConsume(rmd.consumes(), contentType))
            {
               methods.add(rmd);
            }
         }
      }

      if (methods.isEmpty())
      {
         response.setResponse(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
            .entity("Media type " + contentType + " is not supported.").type(MediaType.TEXT_PLAIN).build());
         return false;
      }

      List<MediaType> acceptable = request.getAcceptableMediaTypes();
      float previousQValue = 0.0F;
      int n = 0, p = 0;
      for (ListIterator<T> i = methods.listIterator(); i.hasNext();)
      {
         n = i.nextIndex();
         ResourceMethodDescriptor rmd = i.next();
         float qValue = MediaTypeHelper.processQuality(acceptable, rmd.produces());
         if (qValue > previousQValue)
         {
            previousQValue = qValue;
            p = n; // position of the best resource at the moment
         }
         else
         {
            i.remove(); // qValue is less then previous one
         }
      }

      if (!methods.isEmpty())
      {
         // remove all with lower q value
         if (methods.size() > 1)
         {
            n = 0;
            for (Iterator<T> i = methods.listIterator(); i.hasNext(); i.remove(), n++)
            {
               i.next();
               if (n == p)
               {
                  break; // get index p in list then stop removing
               }
            }
         }
         return true;
      }

      response.setResponse(Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not Acceptable")
         .type(MediaType.TEXT_PLAIN).build());

      return false;
   }

   /**
    * Process sub-resource methods.
    * 
    * @param srmm See {@link SubResourceLocatorMap}
    * @param requestedPath part of requested path
    * @param request See {@link GenericContainerRequest}
    * @param response See {@link GenericContainerResponse}
    * @param capturingValues the list for keeping template values. See
    *           {@link javax.ws.rs.core.UriInfo#getPathParameters()}
    * @param methods list for method resources
    * @return true if at least one sub-resource method found false otherwise
    */
   @SuppressWarnings("unchecked")
   private static boolean processSubResourceMethod(SubResourceMethodMap srmm, String requestedPath,
      GenericContainerRequest request, GenericContainerResponse response, List<String> capturingValues,
      List<SubResourceMethodDescriptor> methods)
   {
      ResourceMethodMap<SubResourceMethodDescriptor> rmm = null;
      for (Entry<UriPattern, ResourceMethodMap<SubResourceMethodDescriptor>> e : srmm.entrySet())
      {
         if (e.getKey().match(requestedPath, capturingValues))
         {
            int len = capturingValues.size();
            if (capturingValues.get(len - 1) != null && !"/".equals(capturingValues.get(len - 1)))
            {
               continue;
            }

            rmm = e.getValue();
            break;
         }
      }

      if (rmm == null)
      {
         response.setResponse(Response.status(Status.NOT_FOUND)
            .entity("There is no any resources matched to request path " + requestedPath).type(MediaType.TEXT_PLAIN)
            .build());
         return false;
      }

      List<SubResourceMethodDescriptor> l = new ArrayList<SubResourceMethodDescriptor>();
      boolean match = processResourceMethod(rmm, request, response, l);

      if (match)
      {
         // for cast, Iterator contains SubResourceMethodDescriptor
         Iterator i = l.iterator();
         while (i.hasNext())
         {
            methods.add((SubResourceMethodDescriptor)i.next());
         }
      }

      return match;
   }

   /**
    * Process sub-resource locators.
    * 
    * @param srlm See {@link SubResourceLocatorMap}
    * @param requestedPath part of requested path
    * @param capturingValues the list for keeping template values
    * @param locators list for sub-resource locators
    * @return true if at least one SubResourceLocatorDescriptor found false
    *         otherwise
    */
   private static boolean processSubResourceLocator(SubResourceLocatorMap srlm, String requestedPath,
      List<String> capturingValues, List<SubResourceLocatorDescriptor> locators)
   {
      for (Map.Entry<UriPattern, SubResourceLocatorDescriptor> e : srlm.entrySet())
      {
         if (e.getKey().match(requestedPath, capturingValues))
         {
            locators.add(e.getValue());
         }
      }
      return !locators.isEmpty();
   }

   /**
    * Get root resource.
    * 
    * @param parameterValues is taken from context
    * @param requestPath is taken from context
    * @return root resource
    * @throws WebApplicationException if there is no matched root resources.
    *            Exception with prepared error response with 'Not Found' status
    */
   protected ObjectFactory<AbstractResourceDescriptor> getRootResourse(List<String> parameterValues, String requestPath)
   {
      ObjectFactory<AbstractResourceDescriptor> resourceFactory =
         resourceBinder.getMatchedResource(requestPath, parameterValues);
      if (resourceFactory == null)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Root resource not found for " + requestPath);
         // Stop here, there is no matched root resource
         throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
            .entity("There is no any resources matched to request path " + requestPath).type(MediaType.TEXT_PLAIN)
            .build());
      }
      return resourceFactory;
   }
}
