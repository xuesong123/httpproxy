/*
 * $RCSfile: Servlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
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
 * <p>Title: Servlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public interface Servlet
{
    public void init();
    
    /**
     * @param request
     * @param response
     * @throws IOException
     */
    public void service(HttpRequest request, HttpResponse response) throws IOException;

    /**
     * @param servletContext
     */
    public void setServletContext(ServletContext servletContext);

    /**
     * @return ServletContext
     */
    public ServletContext getServletContext();

    public void destroy();
}
