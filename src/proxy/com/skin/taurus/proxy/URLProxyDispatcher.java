/*
 * $RCSfile: URLProxyDispatcher.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-12-30 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.http.HttpHeader;
import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.util.IO;

/**
 * <p>Title: URLProxyDispatcher</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @version 1.0
 */
public class URLProxyDispatcher extends ProxyDispatcher
{
    private static Logger logger = LoggerFactory.getLogger(URLProxyDispatcher.class);

    /**
     * @param proxyHost
     */
    public URLProxyDispatcher()
    {
    }

    public void dispatch(HttpRequest request, HttpResponse response, ProxyHost proxyHost)
    {
        URL url = null;
        HttpURLConnection connection = null;

        try
        {
            url = new URL(request.getOriginalURL());
            connection = (HttpURLConnection)(url.openConnection());
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod(request.getMethod());
            this.setRequestHeader(request, connection);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            if(request.getContentLength() > 0)
            {
                connection.setDoOutput(true);
                IO.copy(request.getInputStream(), connection.getOutputStream());
            }

            int status = connection.getResponseCode();
            response.setStatus(status);
            response.setReasonPhrase(connection.getResponseMessage());
            this.setResponseHeader(response, connection);

            if(response.getHeader("Transfer-Encoding") != null)
            {
                // 最好重设Content-Length, 不设可以
                response.removeHeader("Transfer-Encoding");
            }

            if(logger.isDebugEnabled())
            {
                logger.debug("HttpHeader\r\n"
                        + request.toString() + "\r\n"
                        + "--------------------------\r\n"
                        + response.toString());
            }

            OutputStream outputStream = response.getOutputStream();
            outputStream.flush();
            IO.copy(connection.getInputStream(), outputStream);
        }
        catch(Exception e)
        {
        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }
        }
    }

    /**
     * @param request
     * @param connection
     */
    public void setRequestHeader(HttpRequest request, HttpURLConnection connection)
    {
        HttpHeader httpHeader = request.getHttpHeader();
        String[] names = httpHeader.getHeaderNames();

        if(names.length > 0)
        {
            for(String name : names)
            {
                String[] values = httpHeader.getHeaderValues(name);

                if(values != null)
                {
                    for(String value : values)
                    {
                        connection.addRequestProperty(name, value);
                    }
                }
            }
        }
    }

    /**
     * @param request
     * @param connection
     */
    public void setResponseHeader(HttpResponse response, HttpURLConnection connection)
    {
        Map<String, List<String>> headers = connection.getHeaderFields();
        
        if(headers != null)
        {
            for(Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                String name = entry.getKey();

                if(name != null)
                {
                    List<String> values = entry.getValue();
    
                    if(values != null)
                    {
                        for(String value : values)
                        {
                            response.addHeader(name, value);
                        }
                    }
                }
            }
        }
    }
}
