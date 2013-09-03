/*
 * $RCSfile: ServletFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import com.skin.taurus.config.ServletContext;
import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;

/**
 * <p>Title: ServletFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ServletFactory
{
    private static ServletContext servletContext;
    private static DispatchServlet dispatchServlet;
    private static FileServlet fileServlet;

    /**
     * @param servletContext the servletContext to set
     */
    public static void setServletContext(ServletContext servletContext)
    {
        ServletFactory.servletContext = servletContext;
    }

    /**
     * @return the servletContext
     */
    public static ServletContext getServletContext()
    {
        return ServletFactory.servletContext;
    }

    /**
     * @param dispatchServlet the dispatchServlet to set
     */
    public static void setDispatchServlet(DispatchServlet dispatchServlet)
    {
        ServletFactory.dispatchServlet = dispatchServlet;
    }

    /**
     * @return the dispatchServlet
     */
    public static DispatchServlet getDispatchServlet()
    {
        return dispatchServlet;
    }
    
    /**
     * @return the fileServlet
     */
    public static FileServlet getFileServlet()
    {
        return ServletFactory.fileServlet;
    }

    /**
     * @param fileServlet the fileServlet to set
     */
    public static void setFileServlet(FileServlet fileServlet)
    {
        ServletFactory.fileServlet = fileServlet;
    }

    /**
     * @param request
     * @param response
     * @return Servlet
     */
    public static Servlet getServlet(HttpRequest request, HttpResponse response)
    {
        String requestURI = request.getRequestURI();

        if(requestURI == null)
        {
            return null;
        }

        if(requestURI.endsWith(".srv"))
        {
            return ServletFactory.dispatchServlet;
        }
        else
        {
            return ServletFactory.fileServlet;
        }
    }
}
