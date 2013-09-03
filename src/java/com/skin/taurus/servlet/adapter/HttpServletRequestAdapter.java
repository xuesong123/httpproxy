/*
 * $RCSfile: HttpServletRequestAdapter.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.servlet.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.skin.taurus.http.HttpRequest;


/**
 * <p>Title: HttpServletRequestAdapter</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpServletRequestAdapter implements HttpServletRequest
{
    private HttpRequest request;
    private ServletInputStream servletInputStream;

    public HttpServletRequestAdapter(HttpRequest request)
    {
        this.request = request;
    }

    @Override
    public String getAuthType()
    {
        return null;
    }

    @Override
    public String getContextPath()
    {
        return request.getContextPath();
    }

    @Override
    public Cookie[] getCookies()
    {
        return null;
    }

    @Override
    public long getDateHeader(String name)
    {
        return 0;
    }

    @Override
    public String getHeader(String name)
    {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames()
    {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name)
    {
        return null;
    }

    @Override
    public int getIntHeader(String name)
    {
        return 0;
    }

    @Override
    public String getMethod()
    {
        return null;
    }

    @Override
    public String getPathInfo()
    {
        return null;
    }

    @Override
    public String getPathTranslated()
    {
        return null;
    }

    @Override
    public String getQueryString()
    {
        return null;
    }

    @Override
    public String getRemoteUser()
    {
        return null;
    }

    @Override
    public String getRequestURI()
    {
        return null;
    }

    @Override
    public StringBuffer getRequestURL()
    {
        return null;
    }

    @Override
    public String getRequestedSessionId()
    {
        return null;
    }

    @Override
    public String getServletPath()
    {
        return null;
    }

    @Override
    public HttpSession getSession()
    {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create)
    {
        return null;
    }

    @Override
    public Principal getUserPrincipal()
    {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
        return false;
    }

    @Override
    public boolean isUserInRole(String arg0)
    {
        return false;
    }

    @Override
    public Object getAttribute(String arg0)
    {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
        return null;
    }

    @Override
    public String getCharacterEncoding()
    {
        return this.request.getCharacterEncoding();
    }

    @Override
    public int getContentLength()
    {
        return this.request.getContentLength();
    }

    @Override
    public String getContentType()
    {
        return this.request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        if(servletInputStream == null)
        {
            servletInputStream = new ServletInputStreamImpl(this.request.getInputStream());
        }

        return servletInputStream;
    }

    @Override
    public String getLocalAddr()
    {
        return null;
    }

    @Override
    public String getLocalName()
    {
        return null;
    }

    @Override
    public int getLocalPort()
    {
        return 0;
    }

    @Override
    public Locale getLocale()
    {
        return null;
    }

    @Override
    public Enumeration<String> getLocales()
    {
        return null;
    }

    @Override
    public String getParameter(String arg0)
    {
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return null;
    }

    @Override
    public String[] getParameterValues(String arg0)
    {
        return null;
    }

    @Override
    public String getProtocol()
    {
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return null;
    }

    @Override
    public String getRealPath(String arg0)
    {
        return null;
    }

    @Override
    public String getRemoteAddr()
    {
        return null;
    }

    @Override
    public String getRemoteHost()
    {
        return null;
    }

    @Override
    public int getRemotePort()
    {
        return 0;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        return null;
    }

    @Override
    public String getScheme()
    {
        return null;
    }

    @Override
    public String getServerName()
    {
        return null;
    }

    @Override
    public int getServerPort()
    {
        return 0;
    }

    @Override
    public boolean isSecure()
    {
        return false;
    }

    @Override
    public void removeAttribute(String name)
    {

    }

    @Override
    public void setAttribute(String name, Object value)
    {

    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException
    {
        this.request.setCharacterEncoding(encoding);
    }
}
