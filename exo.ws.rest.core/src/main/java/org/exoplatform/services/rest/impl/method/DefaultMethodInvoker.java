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
package org.exoplatform.services.rest.impl.method;

import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.impl.InternalException;
import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * Invoker for Resource Method, Sub-Resource Method and SubResource Locator.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DefaultMethodInvoker implements MethodInvoker
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger("exo.ws.rest.core.DefaultMethodInvoker");

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Object invokeMethod(Object resource, GenericMethodResource methodResource, ApplicationContext context)
   {

      for (ObjectFactory<FilterDescriptor> factory : context.getProviders().getMethodInvokerFilters(context.getPath()))
      {
         MethodInvokerFilter f = (MethodInvokerFilter)factory.getInstance(context);
         f.accept(methodResource);
      }

      Object[] p = new Object[methodResource.getMethodParameters().size()];
      int i = 0;
      for (org.exoplatform.services.rest.method.MethodParameter mp : methodResource.getMethodParameters())
      {
         Annotation a = mp.getAnnotation();
         if (a != null)
         {
            ParameterResolver<?> pr = ParameterResolverFactory.createParameterResolver(a);
            try
            {
               p[i++] = pr.resolve(mp, context);
            }
            catch (Exception e)
            {

               String msg = "Not able resolve method parameter " + mp;
               Class<?> ac = a.annotationType();
               if (ac == MatrixParam.class || ac == QueryParam.class || ac == PathParam.class)
               {
                  throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).entity(msg).type(
                     MediaType.TEXT_PLAIN).build());
               }

               throw new WebApplicationException(e, Response.status(Response.Status.BAD_REQUEST).entity(msg).type(
                  MediaType.TEXT_PLAIN).build());

            }

         }
         else
         {

            InputStream entityStream = context.getContainerRequest().getEntityStream();
            if (entityStream == null)
            {
               p[i++] = null;
            }
            else
            {
               MediaType contentType = context.getContainerRequest().getMediaType();

               MessageBodyReader entityReader =
                  context.getProviders().getMessageBodyReader(mp.getParameterClass(), mp.getGenericType(),
                     mp.getAnnotations(), contentType);
               if (entityReader == null)
               {
                  List<String> contentLength =
                     context.getContainerRequest().getRequestHeader(HttpHeaders.CONTENT_LENGTH);
                  int length = 0;
                  if (contentLength != null && contentLength.size() > 0)
                  {
                     length = Integer.parseInt(contentLength.get(0));
                  }

                  if (contentType == null && length == 0)
                  {
                     // If both Content-Length and Content-Type is not set
                     // consider there is no content. In this case we not able
                     // to determine reader required for content but
                     // 'Unsupported Media Type' (415) status looks strange if
                     // there is no content at all.
                     p[i++] = null;
                  }
                  else
                  {
                     String msg =
                        "Media type " + contentType + " is not supported. There is no corresponded entity reader.";
                     if (LOG.isDebugEnabled())
                     {
                        LOG.warn(msg);
                     }
                     throw new WebApplicationException(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(
                        msg).type(MediaType.TEXT_PLAIN).build());
                  }
               }
               else
               {
                  try
                  {
                     MultivaluedMap<String, String> headers = context.getContainerRequest().getRequestHeaders();
                     p[i++] =
                        entityReader.readFrom(mp.getParameterClass(), mp.getGenericType(), mp.getAnnotations(),
                           contentType, headers, entityStream);
                  }
                  catch (Exception e)
                  {
                     if (LOG.isDebugEnabled())
                     {
                        LOG.debug(e.getLocalizedMessage(), e);
                     }
                     if (e instanceof WebApplicationException)
                     {
                        throw (WebApplicationException)e;
                     }
                     throw new InternalException(e);
                  }
               }
            }
         }

      }
      return invokeMethod(resource, methodResource, p);
   }

   protected Object invokeMethod(final Object resource, final GenericMethodResource methodResource, final Object[] p)
   {
      try
      {
         return SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Object>()
         {
            public Object run() throws Exception
            {
               return methodResource.getMethod().invoke(resource, p);
            }
         });
      }
      catch (PrivilegedActionException pae)
      {
         Throwable cause = pae.getCause();
         if (cause instanceof IllegalArgumentException)
         {
            // should not be thrown
            throw new InternalException(cause);
         }
         else if (cause instanceof IllegalAccessException)
         {
            // should not be thrown
            throw new InternalException(cause);
         }
         else if (cause instanceof InvocationTargetException)
         {
            if (LOG.isDebugEnabled())
            {
               LOG.debug(cause.getLocalizedMessage(), cause);
            }
            // get cause of exception that method produces
            Throwable throwable = cause.getCause();
            // if WebApplicationException than it may contain response
            if (WebApplicationException.class == throwable.getClass())
            {
               throw (WebApplicationException)throwable;
            }

            throw new InternalException(throwable);
         }
         else if (cause instanceof RuntimeException)
         {
            throw (RuntimeException)cause;
         }
         else
         {
            throw new RuntimeException(cause);
         }
      }
   }

}
