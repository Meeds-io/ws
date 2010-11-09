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

import org.exoplatform.commons.utils.PrivilegedFileHelper;
import org.exoplatform.commons.utils.PrivilegedSystemHelper;
import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.impl.method.MethodInvokerFilterComponentPlugin;
import org.exoplatform.services.rest.impl.provider.EntityProviderComponentPlugin;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.picocontainer.Startable;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RequestHandlerImpl implements RequestHandler, Startable
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.RequestHandlerImpl");

   /**
    * Application properties. Properties from this map will be copied to
    * ApplicationContext and may be accessible via method
    * {@link ApplicationContextImpl#getProperties()}.
    */
   private static final Map<String, String> properties = new HashMap<String, String>();

   /**
    * See {@link RequestDispatcher}.
    */
   private final RequestDispatcher dispatcher;

   public static final String getProperty(String name)
   {
      return properties.get(name);
   }

   public static final void setProperty(String name, String value)
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         properties.put(name, value);
      }
   }

   /**
    * Constructs new instance of {@link RequestHandler}.
    *
    * @param dispatcher See {@link RequestDispatcher}
    * @param params init parameters
    */
   public RequestHandlerImpl(RequestDispatcher dispatcher, InitParams params)
   {
      if (params != null)
      {
         for (Iterator<ValueParam> i = params.getValueParamIterator(); i.hasNext();)
         {
            ValueParam vp = i.next();
            properties.put(vp.getName(), vp.getValue());
         }
      }
      this.dispatcher = dispatcher;
   }

   // RequestHandler

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void handleRequest(GenericContainerRequest request, GenericContainerResponse response) throws Exception
   {
      try
      {
         ProviderBinder defaultProviders = ProviderBinder.getInstance();
         ApplicationContextImpl context = new ApplicationContextImpl(request, response, defaultProviders);
         context.getProperties().putAll(properties);
         ApplicationContextImpl.setCurrent(context);

         // Apply default filters only.
         for (ObjectFactory<FilterDescriptor> factory : defaultProviders.getRequestFilters(context.getPath()))
         {
            RequestFilter f = (RequestFilter)factory.getInstance(context);
            f.doFilter(request);
         }

         try
         {
            dispatcher.dispatch(request, response);
            if (response.getHttpHeaders().getFirst(ExtHttpHeaders.JAXRS_BODY_PROVIDED) == null)
            {
               String jaxrsHeader = getJaxrsHeader(response.getStatus());
               if (jaxrsHeader != null)
               {
                  response.getHttpHeaders().putSingle(ExtHttpHeaders.JAXRS_BODY_PROVIDED, jaxrsHeader);
               }
            }
         }
         catch (Exception e)
         {
            if (e instanceof WebApplicationException)
            {
               Response errorResponse = ((WebApplicationException)e).getResponse();
               ExceptionMapper excmap = context.getProviders().getExceptionMapper(WebApplicationException.class);
               int errorStatus = errorResponse.getStatus();
               // should be some of 4xx status
               if (errorStatus < 500)
               {
                  // Warn about error in debug mode only.
                  if (LOG.isDebugEnabled() && e.getCause() != null)
                  {
                     LOG.warn("WebApplication exception occurs.", e.getCause());
                  }
               }
               else
               {
                  if (e.getCause() != null)
                  {
                     LOG.warn("WebApplication exception occurs.", e.getCause());
                  }
               }
               if (errorResponse.getEntity() == null)
               {
                  if (excmap != null)
                  {
                     errorResponse = excmap.toResponse(e);
                  }
                  else
                  {
                     if (e.getMessage() != null)
                     {
                        errorResponse = createErrorResponse(errorStatus, e.getMessage());
                     }
                  }
               }
               else
               {
                  if (errorResponse.getMetadata().getFirst(ExtHttpHeaders.JAXRS_BODY_PROVIDED) == null)
                  {
                     String jaxrsHeader = getJaxrsHeader(errorStatus);
                     if (jaxrsHeader != null)
                     {
                        errorResponse.getMetadata().putSingle(ExtHttpHeaders.JAXRS_BODY_PROVIDED, jaxrsHeader);
                     }
                  }
               }
               response.setResponse(errorResponse);
            }
            else if (e instanceof InternalException)
            {
               Throwable cause = e.getCause();
               Class causeClazz = cause.getClass();
               ExceptionMapper excmap = context.getProviders().getExceptionMapper(causeClazz);
               while (causeClazz != null && excmap == null)
               {
                  excmap = context.getProviders().getExceptionMapper(causeClazz);
                  if (excmap == null)
                  {
                     causeClazz = causeClazz.getSuperclass();
                  }
               }
               if (excmap != null)
               {
                  if (LOG.isDebugEnabled())
                  {
                     // Hide error message if exception mapper exists.
                     LOG.warn("Internal error occurs.", cause);
                  }
                  response.setResponse(excmap.toResponse(e.getCause()));
               }
               else
               {
                  LOG.error("Internal error occurs.", cause);
                  throw new UnhandledException(e.getCause());
               }
            }
            else
            {
               throw new UnhandledException(e);
            }
         }

         // Apply default filters only.
         for (ObjectFactory<FilterDescriptor> factory : defaultProviders.getResponseFilters(context.getPath()))
         {
            ResponseFilter f = (ResponseFilter)factory.getInstance(context);
            f.doFilter(response);
         }

         response.writeResponse();
      }
      finally
      {
         // reset application context
         ApplicationContextImpl.setCurrent(null);
      }
   }

   /**
    * Create error response with specified status and body message.
    *
    * @param status response status
    * @param message response message
    * @return response
    */
   private Response createErrorResponse(int status, String message)
   {

      ResponseBuilder responseBuilder = Response.status(status);
      responseBuilder.entity(message).type(MediaType.TEXT_PLAIN);
      String jaxrsHeader = getJaxrsHeader(status);
      if (jaxrsHeader != null)
         responseBuilder.header(ExtHttpHeaders.JAXRS_BODY_PROVIDED, jaxrsHeader);

      return responseBuilder.build();
   }

   /**
    * Get JAXR header for response status.
    *
    * @param status response status
    * @return JAXRS header or null.
    */
   private String getJaxrsHeader(int status)
   {
      if (status >= 400)
      {
         return "Error-Message";
      }
      // Add required behavior here.
      return null;
   }

   //

   /**
    * For writing error message.
    */
   static class ErrorStreaming implements StreamingOutput
   {

      /**
       * Exception which should send to client.
       */
      private final Exception e;

      /**
       * @param e Exception for serialization
       */
      ErrorStreaming(Exception e)
      {
         this.e = e;
      }

      /**
       * {@inheritDoc}
       */
      public void write(OutputStream output)
      {
         PrintWriter pw = new PrintWriter(output);
         e.printStackTrace(pw);
         pw.flush();
      }

   }

   // Startable

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      init();
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
   }

   //

   /**
    * Startup initialization.
    */
   public void init()
   {
      // Directory for temporary files
      final File tmpDir;
      String tmpDirName = properties.get(WS_RS_TMP_DIR);
      if (tmpDirName == null)
      {
         tmpDir = new File(PrivilegedSystemHelper.getProperty("java.io.tmpdir") + File.separator + "ws_jaxrs");
         properties.put(WS_RS_TMP_DIR, tmpDir.getPath());
      }
      else
      {
         tmpDir = new File(tmpDirName);
      }

      if (!PrivilegedFileHelper.exists(tmpDir))
      {
         PrivilegedFileHelper.mkdirs(tmpDir);
      }

      // Register Shutdown Hook for cleaning temporary files.
      SecurityHelper.doPriviledgedAction(new PrivilegedAction<Void>()
      {
         public Void run()
         {
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
               @Override
               public void run()
               {
                  File[] files = PrivilegedFileHelper.listFiles(tmpDir);
                  for (File file : files)
                  {
                     if (PrivilegedFileHelper.exists(file))
                     {
                        PrivilegedFileHelper.delete(file);
                     }
                  }
               }
            });

            return null;
         }
      });
   }

   /**
    * Processing {@link ComponentPlugin} for injection external components.
    *
    * @param plugin See {@link ComponentPlugin}
    */
   @SuppressWarnings("unchecked")
   public void addPlugin(ComponentPlugin plugin)
   {
      // NOTE!!! ProviderBinder should be already initialized by ResourceBinder
      ProviderBinder providers = ProviderBinder.getInstance();
      if (MethodInvokerFilterComponentPlugin.class.isAssignableFrom(plugin.getClass()))
      {
         // add method invoker filter
         for (Class<? extends MethodInvokerFilter> filter : ((MethodInvokerFilterComponentPlugin)plugin).getFilters())
            providers.addMethodInvokerFilter(filter);
      }
      else if (EntityProviderComponentPlugin.class.isAssignableFrom(plugin.getClass()))
      {
         // add external entity providers
         Set<Class<? extends EntityProvider>> eps = ((EntityProviderComponentPlugin)plugin).getEntityProviders();
         for (Class<? extends EntityProvider> ep : eps)
         {
            providers.addMessageBodyReader(ep);
            providers.addMessageBodyWriter(ep);
         }
      }
      else if (RequestFilterComponentPlugin.class.isAssignableFrom(plugin.getClass()))
      {
         Set<Class<? extends RequestFilter>> filters = ((RequestFilterComponentPlugin)plugin).getFilters();
         for (Class<? extends RequestFilter> filter : filters)
            providers.addRequestFilter(filter);
      }
      else if (ResponseFilterComponentPlugin.class.isAssignableFrom(plugin.getClass()))
      {
         Set<Class<? extends ResponseFilter>> filters = ((ResponseFilterComponentPlugin)plugin).getFilters();
         for (Class<? extends ResponseFilter> filter : filters)
            providers.addResponseFilter(filter);
      }
   }

}
