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

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
   private static final Log LOG = ExoLogger.getLogger(RequestHandlerImpl.class.getName());
   
   /**
    * Application properties.
    */
   private static final MultivaluedMap<String, String> properties = new MultivaluedMapImpl();
   
   /**
    * See {@link RequestDispatcher}.
    */
   private final RequestDispatcher dispatcher;

   //  /**
   //   * See {@link ProviderBinder}.
   //   */
   //  private final ProviderBinder      providers;

   /**
    * Application properties.
    */
   private final Map<String, Object> applicationProperties = new HashMap<String, Object>();

   /**
    * Constructs new instance of {@link RequestHandler}.
    * 
    * @param dispatcher See {@link RequestDispatcher}
    * @param params init parameters
    */
   @SuppressWarnings("unchecked")
   public RequestHandlerImpl(RequestDispatcher dispatcher, InitParams params)
   {
      if (params != null)
      {
         for (Iterator<ValueParam> i = params.getValueParamIterator(); i.hasNext();)
         {
            ValueParam vp = i.next();
            String name = vp.getName();
            String value = vp.getValue();
            if (name.equals(WS_RS_BUFFER_SIZE))
               applicationProperties.put(name, Integer.parseInt(value));
            else if (name.equals(WS_RS_TMP_DIR))
               applicationProperties.put(name, new File(value));
            else
               applicationProperties.put(name, value);
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
         ApplicationContextImpl context = new ApplicationContextImpl(request, response, ProviderBinder.getInstance());
         context.getAttributes().putAll(applicationProperties);
         ApplicationContextImpl.setCurrent(context);

         for (ObjectFactory<FilterDescriptor> factory : ProviderBinder.getInstance().getRequestFilters(
            context.getPath()))
         {
            RequestFilter f = (RequestFilter)factory.getInstance(context);
            f.doFilter(request);
         }

         try
         {

            dispatcher.dispatch(request, response);

         }
         catch (Exception e)
         {
            if (e instanceof WebApplicationException)
            {

               Response errorResponse = ((WebApplicationException)e).getResponse();
               ExceptionMapper excmap = ProviderBinder.getInstance().getExceptionMapper(WebApplicationException.class);

               // should be some of 4xx status
               if (errorResponse.getStatus() < 500)
               {
                  if (LOG.isDebugEnabled() && e.getCause() != null)
                  {
                     LOG.warn("WedApplication exception occurs.", e.getCause());
                  }
                  if (errorResponse.getEntity() == null)
                  {
                     if (excmap != null)
                     {
                        errorResponse = excmap.toResponse(e);
                     }
                  }
                  response.setResponse(errorResponse);
               }
               else
               {

                  if (errorResponse.getEntity() == null)
                  {
                     if (excmap != null)
                     {
                        if (LOG.isDebugEnabled() && e.getCause() != null)
                        {
                           // Hide error message if exception mapper exists.
                           LOG.warn("WedApplication exception occurs.", e.getCause());
                        }

                        errorResponse = excmap.toResponse(e);
                     }
                     else
                     {
                        if (e.getCause() != null)
                        {
                           LOG.warn("WedApplication exception occurs.", e.getCause());
                        }

                        // add stack trace as message body
                        errorResponse =
                           Response.status(errorResponse.getStatus()).entity(new ErrorStreaming(e)).type(
                              MediaType.TEXT_PLAIN).build();
                     }
                  }
                  response.setResponse(errorResponse);
               }
            }
            else if (e instanceof InternalException)
            {
               Throwable cause = e.getCause();
               Class causeClazz = cause.getClass();
               ExceptionMapper excmap = ProviderBinder.getInstance().getExceptionMapper(causeClazz);
               while (causeClazz != null && excmap == null)
               {
                  excmap = ProviderBinder.getInstance().getExceptionMapper(causeClazz);
                  if (excmap == null)
                     causeClazz = causeClazz.getSuperclass();
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

         for (ObjectFactory<FilterDescriptor> factory : ProviderBinder.getInstance().getResponseFilters(
            context.getPath()))
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
      if (applicationProperties.containsKey(WS_RS_TMP_DIR))
         tmpDir = (File)applicationProperties.get(WS_RS_TMP_DIR);
      else
      {
         tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "ws_jaxrs");
         applicationProperties.put(WS_RS_TMP_DIR, tmpDir);
      }

      if (!tmpDir.exists())
         tmpDir.mkdirs();

      // Register Shutdown Hook for cleaning temporary files.
      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         public void run()
         {
            File[] files = tmpDir.listFiles();
            for (File file : files)
            {
               if (file.exists())
                  file.delete();
            }
         }
      });

      Integer bufferSize = (Integer)applicationProperties.get(WS_RS_BUFFER_SIZE);
      if (bufferSize == null)
      {
         bufferSize = 204800; // TODO move somewhere as const
         applicationProperties.put(WS_RS_BUFFER_SIZE, bufferSize);
      }

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
