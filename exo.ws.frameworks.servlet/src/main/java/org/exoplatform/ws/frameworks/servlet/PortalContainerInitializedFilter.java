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
package org.exoplatform.ws.frameworks.servlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Created by The eXo Platform SAS .<br>
 * Servlet Filter that is used to initialize and remove the portal container from the ThreadLocal
 * of PortalContainer, it relies on PortalContainer.getCurrentInstance to retrieve the right portal container. 
 * 
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public class PortalContainerInitializedFilter extends AbstractFilter
{

   private static final Log LOG = ExoLogger.getLogger("exo.ws.frameworks.servlet.PortatContainerInitializedFilter");

   /**
    * initializes PortalContainer instance.
    * 
    * @see jakarta.servlet.Filter#doFilter(jakarta.servlet.ServletRequest,
    *      jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      ExoContainer container = getContainer();
      if (!(container instanceof PortalContainer))
      {
         container = RootContainer.getInstance().getPortalContainer(PortalContainer.DEFAULT_PORTAL_CONTAINER_NAME);
         if (container == null)
         {
            throw new ServletException("Could not initialize PortalContainer." + "Current ExoContainer is: "
               + ExoContainerContext.getCurrentContainer());            
         }
      }
      PortalContainer pcontainer = (PortalContainer)container;
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
