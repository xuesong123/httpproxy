/*
 * $RCSfile: HttpServlet.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-3-26 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.IOException;

import com.skin.taurus.config.ServletContext;
import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;

/**
 * <p>Title: HttpServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpServlet implements Servlet
{
    private ServletContext servletContext;

    @Override
    public void init()
    {
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException
    {
    }

    /**
     * @param servletContext
     */
    @Override
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    /**
     * @return ServletContext
     */
    @Override
    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    @Override
    public void destroy()
    {
    }
}
