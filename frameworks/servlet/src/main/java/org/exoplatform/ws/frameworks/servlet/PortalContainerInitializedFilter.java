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
package org.exoplatform.ws.frameworks.servlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by The eXo Platform SAS .<br/> Servlet Filter for initialization
 * PortalContainer instance in following way: - try to get current
 * PortalContainer instance using
 * ExoContainerContext.getContainerByName(contextName) - if not found try to get
 * RootContainer instance using ExoContainerContext.getTopContainer() and then
 * create PortalContainer after it - if neither Portal nor Root Container found
 * (possible if there is instantiated StandaloneContainer) throws
 * ServletException
 * 
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class PortalContainerInitializedFilter implements Filter
{

   private static final Log LOG = ExoLogger.getLogger("PortatContainerInitializedFilter");

   private String portalContainerName;

   /**
    * {@inheritDoc}
    */
   public void init(FilterConfig config) throws ServletException
   {
      portalContainerName = config.getInitParameter("portalContainerName");
      if (portalContainerName == null)
         portalContainerName = config.getServletContext().getServletContextName();
   }

   /**
    * initializes PortalContainer instance.
    * 
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
    *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      PortalContainer pcontainer = (PortalContainer)ExoContainerContext.getContainerByName(portalContainerName);
      if (LOG.isDebugEnabled())
         LOG.debug("get-by-name");
      if (pcontainer == null)
      {
         if (LOG.isInfoEnabled())
            LOG.info("get-from-root");
         ExoContainer container = ExoContainerContext.getTopContainer();
         if (container instanceof RootContainer)
         {
            pcontainer = ((RootContainer)container).getPortalContainer(portalContainerName);
            if (LOG.isDebugEnabled())
               LOG.debug("PortalContainer is created after RootContainer");
         }
      }
      if (pcontainer == null)
      {
         throw new ServletException("Could not initialize PortalContainer." + "Current ExoContainer is: "
            + ExoContainerContext.getCurrentContainer());
      }
      try
      {
         PortalContainer.setInstance(pcontainer);
         chain.doFilter(request, response);
      }
      finally
      {
         try
         {
            PortalContainer.setInstance(null);
         }
         catch (Exception e)
         {
            LOG.warn("An error occured while cleaning the ThreadLocal", e);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void destroy()
   {
   }

}
