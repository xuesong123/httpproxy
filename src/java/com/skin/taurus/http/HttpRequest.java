/*
 * $RCSfile: HttpRequest.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: HttpRequest</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpRequest
{
    private String method;
    private String requestURI;
    private String requestURL;
    private String originalURL;
    private String queryString;
    private String httpProtocol;

    private String localAddr;
    private int serverPort = -1;

    private String remoteAddr;
    private String remoteHost;
    private int remotePort = -1;
    private String requestHost;
    private int requestPort;
    private String characterEncoding;
    private HttpHeader httpHeader;
    private Map<String, List<String>> parameters;
    private InputStream inputStream;
    private OutputStream outputStream;

    public HttpRequest()
    {
        this.httpHeader = new HttpHeader();
        this.parameters = new HashMap<String, List<String>>();
    }

    public String getContextPath()
    {
        return "/";
    }

    /**
     * @param encoding
     */
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException
    {
        this.characterEncoding = encoding;
    }

    /**
     * @return String
     */
    public String getCharacterEncoding()
    {
        if(this.characterEncoding == null)
        {
            this.characterEncoding = "UTF-8";
        }

        return this.characterEncoding;
    }

    /**
     * @return HttpHeader
     */
    public void setHttpHeader(HttpHeader httpHeader)
    {
        this.httpHeader = httpHeader;
    }

    /**
     * @return HttpHeader
     */
    public HttpHeader getHttpHeader()
    {
        return this.httpHeader;
    }

    /**
     * @param name
     * @param value
     */
    public void addHeader(String name, String value)
    {
        if(value != null)
        {
            this.httpHeader.addHeader(name, value);
        }
        else
        {
            this.httpHeader.remove(name);
        }
    }

    /**
     * @param name
     * @param value
     */
    public void setHeader(String name, String value)
    {
        if(value != null)
        {
            this.httpHeader.setHeader(name, value);
        }
        else
        {
            this.httpHeader.remove(name);
        }
    }
    
    /**
     * @param name
     * @param newName
     * @param value
     */
    public void replaceHeader(String name, String newName, String value)
    {
        this.httpHeader.replace(name, newName, value);
    }

    /**
     * @param name
     */
    public void removeHeader(String name)
    {
        this.httpHeader.remove(name);
    }

    /**
     * @param name
     * @return - String
     */
    public String getHeader(String name)
    {
        return this.httpHeader.getHeader(name);
    }

    /**
     * @param name
     * @return - String
     */
    public String[] getHeaderValues(String name)
    {
        return this.httpHeader.getHeaderValues(name);
    }

    /**
     * @return - String
     */
    public String getHttpProtocol()
    {
        return httpProtocol;
    }

    /**
     * @param httpProtocol
     */
    public void setHttpProtocol(String httpProtocol)
    {
        this.httpProtocol = httpProtocol;
    }

    /**
     * 
     * @return - String
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(String method)
    {
        this.method = method;
    }

    /**
     * @return - String
     */
    public String getQueryString()
    {
        return queryString;
    }

    /**
     * 
     * @param queryString
     */
    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    /**
     * @param name
     * @return String
     */
    public String getParameter(String name)
    {
        List<String> values = this.parameters.get(name);

        if(values != null && values.size() > 0)
        {
            String value = values.get(0);

            try
            {
                value = URLDecoder.decode(value, this.getCharacterEncoding());
            }
            catch(UnsupportedEncodingException e)
            {
                value = null;
            }

            return value;
        }

        return null;
    }

    /**
     * @param name
     * @return String
     */
    public String[] getParameterValues(String name)
    {
        List<String> values = this.parameters.get(name);

        if(values != null && values.size() > 0)
        {
            String[] array = new String[values.size()];
            values.toArray(array);

            for(int i = 0; i < array.length; i++)
            {
                try
                {
                    array[i] = URLDecoder.decode(array[i], this.getCharacterEncoding());
                }
                catch(UnsupportedEncodingException e)
                {
                    array[i] = "";
                }
            }

            return array;
        }

        return new String[0];
    }

    /**
     * @param name
     * @return String
     */
    public void setParameters(Map<String, List<String>> parameters)
    {
        this.parameters.putAll(parameters);
    }

    /**
     * 
     * @return - String
     */
    public String getRequestURI()
    {
        return requestURI;
    }

    /**
     * 
     * @param requestURI
     */
    public void setRequestURI(String requestURI)
    {
        this.requestURI = requestURI;
    }

    /**
     * 
     * @return - String
     */
    public String getRequestURL()
    {
        return this.requestURL;
    }

    /**
     * 
     * @param requestURL
     */
    public void setRequestURL(String requestURL)
    {
        this.requestURL = requestURL;
    }
    
    /**
     * @return the originalURL
     */
    public String getOriginalURL()
    {
        return this.originalURL;
    }

    /**
     * @param originalURL the originalURL to set
     */
    public void setOriginalURL(String originalURL)
    {
        this.originalURL = originalURL;
    }

    /**
     * 
     * @return - String
     */
    public String getContentType()
    {
        return (String)(this.getHeader("Content-Type"));
    }

    /**
     * 
     * @return - int
     */
    public int getContentLength()
    {
        int contentLength = -1;
        String value = (String)(this.getHeader("Content-Length"));

        if(value == null)
        {
            return contentLength;
        }

        try
        {
            contentLength = Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
        }

        return contentLength;
    }

    public String getLocalAddr()
    {
        return localAddr;
    }

    public void setLocalAddr(String localAddr)
    {
        this.localAddr = localAddr;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr)
    {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort()
    {
        return remotePort;
    }

    public void setRemotePort(int remotePort)
    {
        this.remotePort = remotePort;
    }
    
    /**
     * @return the requestHost
     */
    public String getRequestHost()
    {
        return this.requestHost;
    }

    /**
     * @param requestHost the requestHost to set
     */
    public void setRequestHost(String requestHost)
    {
        this.requestHost = requestHost;
    }

    /**
     * @return the requestPort
     */
    public int getRequestPort()
    {
        return this.requestPort;
    }

    /**
     * @param requestPort the requestPort to set
     */
    public void setRequestPort(int requestPort)
    {
        this.requestPort = requestPort;
    }

    /**
     * @return - InputStream
     */
    public InputStream getInputStream()
    {
        return inputStream;
    }

    /**
     * @param inputStream
     */
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    /**
     * @return - OutputStream
     */
    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * @param outputStream
     */
    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String getHttpHeaders()
    {
        String url = this.getOriginalURL();

        if(url.startsWith("http://"))
        {
            url = url.substring(7);

            int k = url.indexOf("/");

            if(k > -1)
            {
                url = url.substring(k);
            }
        }
        else if(url.startsWith("https://"))
        {
            url = url.substring(8);

            int k = url.indexOf("/");

            if(k > -1)
            {
                url = url.substring(k);
            }
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(this.method).append(" ").append(url);
        buffer.append(" ").append(this.getHttpProtocol()).append("\r\n");
        buffer.append(this.httpHeader.toString());
        buffer.append("\r\n");
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.method).append(" ").append(this.getOriginalURL());
        buffer.append(" ").append(this.getHttpProtocol()).append("\r\n");
        buffer.append(this.httpHeader.toString());
        buffer.append("\r\n");
        return buffer.toString();
    }
}
