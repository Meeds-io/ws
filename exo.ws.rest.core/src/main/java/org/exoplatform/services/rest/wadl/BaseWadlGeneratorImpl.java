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
package org.exoplatform.services.rest.wadl;

import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.wadl.research.Application;
import org.exoplatform.services.rest.wadl.research.Param;
import org.exoplatform.services.rest.wadl.research.ParamStyle;
import org.exoplatform.services.rest.wadl.research.RepresentationType;
import org.exoplatform.services.rest.wadl.research.Resources;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

/**
 * Base implementation of {@link WadlGenerator}. This implementation does not
 * provide doc and grammar extension of WADL.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BaseWadlGeneratorImpl implements WadlGenerator
{

   /**
    * {@inheritDoc}
    */
   public Application createApplication()
   {
      return new Application();
   }

   /**
    * {@inheritDoc}
    */
   public Resources createResources()
   {
      return new Resources();
   }

   /**
    * {@inheritDoc}
    */
   public org.exoplatform.services.rest.wadl.research.Resource createResource(AbstractResourceDescriptor rd)
   {
      if (rd.isRootResource())
         return createResource(rd.getPathValue().getPath());
      return createResource((String)null);
   }

   /**
    * {@inheritDoc}
    */
   public org.exoplatform.services.rest.wadl.research.Resource createResource(String path)
   {
      org.exoplatform.services.rest.wadl.research.Resource wadlResource =
         new org.exoplatform.services.rest.wadl.research.Resource();
      if (path != null)
         wadlResource.setPath(path);
      return wadlResource;
   }

   /**
    * {@inheritDoc}
    */
   public org.exoplatform.services.rest.wadl.research.Method createMethod(ResourceMethodDescriptor rmd)
   {
      String httpMethod = rmd.getHttpMethod();
      // FIXME Ignore HEAD methods currently.
      // Implementation of wadl2java for generation client code does not support
      // HEAD method. See https://wadl.dev.java.net/ . 
      // If WADL contains HEAD method description then client code get part of
      // code as next:
      // -------------------------------------------- 
      //    public DataSource headAs()                                                                  
      //    throws IOException, MalformedURLException                                               
      // {                                                                                           
      //    HashMap<String, Object> _queryParameterValues = new HashMap<String, Object>();          
      //    HashMap<String, Object> _headerParameterValues = new HashMap<String, Object>();         
      //    String _url = _uriBuilder.buildUri(_templateAndMatrixParameterValues, _queryParameterVal
      //    DataSource _retVal = _dsDispatcher.doHEAD(_url, _headerParameterValues, "*/*");         
      //    return _retVal;                                                                         
      // }
      // --------------------------------------------
      // But class  org.jvnet.ws.wadl.util.DSDispatcher doesn't have method doHEAD at all.
      //
      if (httpMethod.equals("HEAD"))
         return null;

      org.exoplatform.services.rest.wadl.research.Method wadlMethod =
         new org.exoplatform.services.rest.wadl.research.Method();
      wadlMethod.setName(httpMethod);
      java.lang.reflect.Method m = rmd.getMethod();
      // NOTE Method may be null in some cases. For example OPTIONS method
      // processor use null method and fake invoker. See
      // OptionsRequestResourceMethodDescriptorImpl.
      if (m != null)
         wadlMethod.setId(m.getName());
      return wadlMethod;
   }

   /**
    * {@inheritDoc}
    */
   public org.exoplatform.services.rest.wadl.research.Request createRequest()
   {
      return new org.exoplatform.services.rest.wadl.research.Request();
   }

   /**
    * {@inheritDoc}
    */
   public org.exoplatform.services.rest.wadl.research.Response createResponse()
   {
      return new org.exoplatform.services.rest.wadl.research.Response();
   }

   /**
    * {@inheritDoc}
    */
   public RepresentationType createRequestRepresentation(MediaType mediaType)
   {
      RepresentationType wadlRepresentation = new RepresentationType();
      wadlRepresentation.setMediaType(mediaType.toString());
      return wadlRepresentation;
   }

   /**
    * {@inheritDoc}
    */
   public RepresentationType createResponseRepresentation(MediaType mediaType)
   {
      RepresentationType wadlRepresentation = new RepresentationType();
      wadlRepresentation.setMediaType(mediaType.toString());
      return wadlRepresentation;
   }

   /**
    * {@inheritDoc}
    */
   public Param createParam(MethodParameter methodParameter)
   {
      Param wadlParemeter = null;
      Annotation annotation = methodParameter.getAnnotation();
      Class<?> annotationClass = methodParameter.getAnnotation().annotationType();
      // In fact annotation may be one of from
      // MethodParameterHelper#PARAMETER_ANNOTATIONS_MAP
      if (annotationClass == PathParam.class)
      {
         wadlParemeter = new Param();
         // attribute 'name'
         wadlParemeter.setName(((PathParam)annotation).value());
         // attribute 'style'
         wadlParemeter.setStyle(ParamStyle.TEMPLATE);
      }
      else if (annotationClass == MatrixParam.class)
      {
         wadlParemeter = new Param();
         wadlParemeter.setName(((MatrixParam)annotation).value());
         wadlParemeter.setStyle(ParamStyle.MATRIX);
      }
      else if (annotationClass == QueryParam.class)
      {
         wadlParemeter = new Param();
         wadlParemeter.setName(((QueryParam)annotation).value());
         wadlParemeter.setStyle(ParamStyle.QUERY);
      }
      else if (annotationClass == HeaderParam.class)
      {
         wadlParemeter = new Param();
         wadlParemeter.setName(((HeaderParam)annotation).value());
         wadlParemeter.setStyle(ParamStyle.HEADER);
      }

      if (wadlParemeter == null)
         // ignore this method parameter
         return null;

      // attribute 'repeat'
      Class<?> parameterClass = methodParameter.getParameterClass();
      if (parameterClass == List.class || parameterClass == Set.class || parameterClass == SortedSet.class)
         wadlParemeter.setRepeating(true);

      // attribute 'default'
      if (methodParameter.getDefaultValue() != null)
         wadlParemeter.setDefault(methodParameter.getDefaultValue());

      // attribute 'type'
      if (parameterClass.equals(Boolean.class) || parameterClass.equals(boolean.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
      else if (parameterClass.equals(Byte.class) || parameterClass.equals(byte.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
      else if (parameterClass.equals(Short.class) || parameterClass.equals(short.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
      else if (parameterClass.equals(Integer.class) || parameterClass.equals(int.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "integer", "xs"));
      else if (parameterClass.equals(Long.class) || parameterClass.equals(long.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
      else if (parameterClass.equals(Float.class) || parameterClass.equals(float.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
      else if (parameterClass.equals(Double.class) || parameterClass.equals(double.class))
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
      else
         wadlParemeter.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));

      return wadlParemeter;
   }

}
