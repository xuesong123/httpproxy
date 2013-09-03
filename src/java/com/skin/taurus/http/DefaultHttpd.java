/*
 * $RCSfile: DefaultHttpd.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.skin.taurus.http.servlet.Servlet;
import com.skin.taurus.http.servlet.ServletFactory;
import com.skin.taurus.util.IO;

/**
 * <p>Title: DefaultHttpd</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class DefaultHttpd extends Httpd
{
    public DefaultHttpd()
    {
    }
    
    /**
     * @param server
     * @param socket
     */
    public DefaultHttpd(HttpServer server)
    {
        super(server);
    }

    /**
     * @param server
     * @param socket
     */
    public DefaultHttpd(HttpServer server, Socket socket)
    {
        super(server, socket);
    }

    /**
     * @param request
     * @param response
     */
    @Override
    protected void service(HttpRequest request, HttpResponse response) throws IOException
    {
        String requestURL = request.getRequestURL();

        if(requestURL.startsWith("http://"))
        {
            requestURL = requestURL.substring(7);
        }

        if(requestURL.startsWith("https://"))
        {
            requestURL = requestURL.substring(8);
        }

        int i = requestURL.indexOf("/");

        if(i > -1)
        {
            requestURL = requestURL.substring(i);
        }

        i = requestURL.indexOf("?");
        
        if(i > -1)
        {
            requestURL = requestURL.substring(0, i);
        }

        i = requestURL.indexOf("&");

        if(i > -1)
        {
            requestURL = requestURL.substring(0, i);
        }

        request.setRequestURI(requestURL);
        request.setRequestURL(requestURL);
        Map<String, List<String>> parameters = ParameterParser.parse(request.getQueryString());
        request.setParameters(parameters);

        if(request.getMethod().toLowerCase().equals("post"))
        {
            int contentLength = request.getContentLength();

            if(contentLength > 0)
            {
                String contentType = request.getContentType();
                
                if(contentType != null)
                {
                    int j = contentType.indexOf(";");
                    String charset = "UTF-8";

                    if(j > -1)
                    {
                        charset = contentType.substring(j + 1).trim().toLowerCase();
                        contentType = contentType.substring(0, j);

                        if(charset.startsWith("charset="))
                        {
                            charset = charset.substring(8);
                        }
                        else
                        {
                            charset = "UTF-8";
                        }
                    }

                    if(contentType.toLowerCase().equals("application/x-www-form-urlencoded"))
                    {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(contentLength);
                        InputStream inputStream = request.getInputStream();
                        IO.copy(inputStream, outputStream);
                        String queryString = new String(outputStream.toByteArray(), charset);
                        parameters = ParameterParser.parse(queryString);
                        request.setParameters(parameters);
                    }
                }
            }
        }

        response.setStatus(200);
        response.setReasonPhrase("OK");
        Servlet instance = ServletFactory.getServlet(request, response);

        if(instance != null)
        {
            instance.service(request, response);
        }

        response.flush();
    }
}
