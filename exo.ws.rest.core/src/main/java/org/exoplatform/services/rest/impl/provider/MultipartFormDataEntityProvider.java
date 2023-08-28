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
package org.exoplatform.services.rest.impl.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;

import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.provider.EntityProvider;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Processing multipart data based on apache fileupload.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@SuppressWarnings("rawtypes")
@Provider
@Consumes({ "multipart/*" })
public class MultipartFormDataEntityProvider implements EntityProvider<Iterator<? extends FileItem>> {

  @Context
  private HttpServletRequest httpRequest;

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    if (type == Iterator.class && genericType instanceof ParameterizedType t) {
      Type[] ta = t.getActualTypeArguments();
      return ta.length == 1 && ta[0] == FileItem.class;
    }
    return false;
  }

  @Override
  public Iterator<? extends FileItem> readFrom(Class<Iterator<? extends FileItem>> type,
                                               Type genericType,
                                               Annotation[] annotations,
                                               MediaType mediaType,
                                               MultivaluedMap<String, String> httpHeaders,
                                               InputStream entityStream) throws IOException {
    try {
      ApplicationContext context = ApplicationContextImpl.getCurrent();
      int bufferSize =
                     context.getProperties().get(RequestHandler.WS_RS_BUFFER_SIZE)
                         == null ? RequestHandler.WS_RS_BUFFER_SIZE_VALUE :
                                 Integer.parseInt(context.getProperties()
                                                         .get(
                                                              RequestHandler.WS_RS_BUFFER_SIZE));
      File repo = new File(context.getProperties().get(RequestHandler.WS_RS_TMP_DIR));
      DiskFileItemFactory factory = DiskFileItemFactory.builder()
                                                       .setBufferSize(bufferSize)
                                                       .setFile(repo)
                                                       .setCharset(StandardCharsets.UTF_8)
                                                       .get();
      JakartaServletDiskFileUpload servletUpload = new JakartaServletDiskFileUpload(factory);
      servletUpload.setHeaderCharset(StandardCharsets.UTF_8);
      List<DiskFileItem> fileItems = servletUpload.parseRequest(httpRequest);
      return fileItems == null ? Collections.emptyIterator() : fileItems.iterator();
    } catch (FileUploadException e) {
      throw new IOException("Can't process multipart data item ", e);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Can't process multipart data item ", e);
    }
  }

  @Override
  public long getSize(Iterator<? extends FileItem> t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    // output is not supported
    return false;
  }

  @Override
  public void writeTo(Iterator<? extends FileItem> t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException {
    throw new UnsupportedOperationException();
  }

}
