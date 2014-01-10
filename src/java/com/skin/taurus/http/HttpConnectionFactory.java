/*
 * $RCSfile: HttpConnectionFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

import com.skin.taurus.http.io.HttpInputStream;
import com.skin.taurus.http.io.HttpOutputStream;
import com.skin.taurus.util.IO;

/**
 * <p>Title: HttpConnectionFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpConnectionFactory
{
    private static final byte[] LF = new byte[]{0x0A};
    private static final byte[] CRLF = new byte[]{0x0D, 0x0A};

    /**
     * @param socket
     * @return HttpConnection
     * @throws IOException
     */
    public static HttpConnection getHttpConnection(Socket socket) throws IOException
    {
        HttpRequest request = createHttpRequest(socket);
        HttpResponse response = createHttpResponse(socket);

        if(request == null)
        {
            return null;
        }

        HttpConnection httpConnection = new HttpConnection(socket);
        httpConnection.setRequest(request);
        httpConnection.setResponse(response);

        boolean keepAlive = false;
        String httpProtocol = request.getHttpProtocol();
        String connection = request.getHeader("Connection");
        String proxyConnection = request.getHeader("Proxy-Connection");

        if(getHttpProtocolVersion(httpProtocol) > 1.0F)
        {
            keepAlive = (proxyConnection != null && proxyConnection.equalsIgnoreCase("Keep-Alive"));

            if(keepAlive == false)
            {
                keepAlive = (connection != null && connection.equalsIgnoreCase("Keep-Alive"));
            }
        }
        else
        {
            keepAlive = false;
        }

        httpConnection.setKeepAlive(keepAlive);
        socket.setKeepAlive(keepAlive);
        // response.setHeader("ThreadName", Thread.currentThread().getName());
        // response.addHeader("Connection", (keepAlive ? "Keep-Alive" : "Close")); // ? Proxy-Connection
        return httpConnection;
    }

    /**
     * @param socket
     * @return HttpRequest
     * @throws IOException
     */
    public static HttpRequest createHttpRequest(Socket socket) throws IOException
    {
        InputStream inputStream = new BufferedInputStream(socket.getInputStream());
        OutputStream out = socket.getOutputStream();
        HttpRequest httpRequest = new HttpRequest();
        byte[] bytes = IO.readLine(inputStream);

        if(bytes.length < 1)
        {
            return null;
        }

        if(!Arrays.equals(bytes, CRLF) && !Arrays.equals(bytes, LF))
        {
            String header = new String(bytes).trim();
            java.util.StringTokenizer st = new java.util.StringTokenizer(header, " ");

            if(st.hasMoreTokens())
            {
                httpRequest.setMethod(st.nextToken());
            }

            if(st.hasMoreTokens())
            {
                String url = st.nextToken();
                String requestURL = null;
                String queryString = null;
                String originalURL = url;

                if(url != null && (url = url.trim()).length() > 0)
                {
                    int k = url.indexOf("?");

                    if(k < 0)
                    {
                        k = url.indexOf("&");

                        if(k > -1)
                        {
                            url = url.substring(0, k) + "?" + url.substring(0, k + 1);
                        }
                    }

                    if(k > -1)
                    {
                        requestURL = url.substring(0, k);
                        queryString = url.substring(k + 1);
                    }
                    else
                    {
                        requestURL = url;
                        queryString = "";
                    }

                    try
                    {
                        requestURL = URLDecoder.decode(requestURL, "UTF-8");
                    }
                    catch(IOException e)
                    {
                    }
                }
                else
                {
                    requestURL = "/";
                    queryString = "";
                }

                httpRequest.setRequestURL(requestURL);
                httpRequest.setRequestURI(requestURL);
                httpRequest.setOriginalURL(originalURL);
                httpRequest.setQueryString(queryString);
            }

            if(st.hasMoreTokens())
            {
                httpRequest.setHttpProtocol(st.nextToken());
            }
        }

        while((bytes = IO.readLine(inputStream)).length > 0)
        {
            if(Arrays.equals(bytes, CRLF) || Arrays.equals(bytes, LF))
            {
                break;
            }

            String header = new String(bytes).trim();
            int k = header.indexOf(":");

            if(k > -1)
            {
                String name  = header.substring(0, k).trim();
                String value = header.substring(k + 1).trim();
                httpRequest.addHeader(name, value);
            }
        }

        InetAddress inetAddress = socket.getInetAddress();
        httpRequest.setRemoteAddr(inetAddress.getHostAddress());
        httpRequest.setRemoteHost((inetAddress.getHostName() != null ? inetAddress.getHostName() : inetAddress.getHostAddress()));
        httpRequest.setRemotePort(socket.getPort());
        httpRequest.setLocalAddr(inetAddress.getHostAddress());
        httpRequest.setServerPort(socket.getLocalPort());
        httpRequest.setInputStream(new HttpInputStream(inputStream, httpRequest.getContentLength()));
        httpRequest.setOutputStream(out);
        return getRequestHost(httpRequest);
    }

    /**
     * @param request
     * @return HttpRequest
     */
    public static HttpRequest getRequestHost(HttpRequest request)
    {
        String host = null;
        String requestURL = request.getRequestURL();
        int port = -1;

        if(requestURL != null)
        {
            if(requestURL.startsWith("http://") || requestURL.startsWith("https://"))
            {
                try
                {
                    URL url = new URL(requestURL);
                    host = url.getHost();
                    port = url.getPort();

                    if(port < 0 && requestURL.startsWith("https://"))
                    {
                        port = 443;
                    }
                }
                catch(MalformedURLException e)
                {
                }
            }
        }

        if(host == null)
        {
            host = request.getHeader("Host");

            if(host != null)
            {
                int k = host.indexOf(":");

                if(k > -1)
                {
                    try
                    {
                        port = Integer.parseInt(host.substring(k + 1));
                    }
                    catch(NumberFormatException e)
                    {
                    }

                    host = host.substring(0, k);
                }
            }
        }

        if(port < 1)
        {
            port = 80;
        }

        request.setRequestHost(host);
        request.setRequestPort(port);
        return request;
    }

    /**
     * @param url
     * @param cookies
     * @return HttpRequest
     * @throws IOException
     */
    public static HttpRequest createHttpRequest(String url, String[] cookies) throws IOException
    {
        URL httpUrl = new URL(url);
        HttpRequest httpRequest = new HttpRequest();

        String host = httpUrl.getHost();
        int port = httpUrl.getPort();
        String requestURI = httpUrl.getPath();
        String requestURL = httpUrl.getPath();
        String query = httpUrl.getQuery();

        if(query != null && query.length() > 0)
        {
            requestURL += "?" + query;
        }

        if(port > 0 && port < 65536 && port != 80)
        {
            host += ":" + port;
        }

        httpRequest.setMethod("GET");
        httpRequest.setRequestURI(requestURI);
        httpRequest.setRequestURL(requestURL);
        httpRequest.setHttpProtocol("HTTP/1.1");

        httpRequest.setHeader("Accept", "*" + "/" + "*");
        httpRequest.setHeader("Accept-Language", "zh-cn");
        httpRequest.setHeader("Accept-Encoding", "gzip, deflate");
        httpRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)");
        httpRequest.setHeader("Host", host);
        httpRequest.setHeader("Connection", "Keep-Alive");

        if(cookies != null)
        {
            for(int i = 0; i < cookies.length; i++)
            {
                httpRequest.addHeader("Cookie", cookies[i]);
            }
        }

        return httpRequest;
    }

    /**
     * @param socket
     * @return HttpResponse
     * @throws IOException
     */
    public static HttpResponse createHttpResponse(Socket socket) throws IOException
    {
        int bufferSize = 8192;
        OutputStream outputStream = socket.getOutputStream();

        HttpResponse response = new HttpResponse();
        response.setHttpProtocol("HTTP/1.1");
        response.setBufferSize(bufferSize);
        response.setHeader("Transfer-Encoding", null);

        HttpOutputStream httpOutputStream = new HttpOutputStream(response, outputStream, bufferSize);
        response.setOutputStream(httpOutputStream);
        return response;
    }

    /**
     * @param inputStream
     * @param outputStream
     * @return HttpResponse
     * @throws IOException
     */
    public static HttpResponse createHttpResponse(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        byte[] bytes = IO.readLine(inputStream);
        HttpResponse httpResponse = new HttpResponse();

        if(bytes.length < 1)
        {
            return null;
        }

        if(!Arrays.equals(bytes, CRLF) && !Arrays.equals(bytes, LF))
        {
            String header = new String(bytes).trim();
            int k = header.indexOf(" ");
            
            if(k > -1)
            {
                httpResponse.setHttpProtocol(header.substring(0, k));
                header = header.substring(k + 1);
                k = header.indexOf(" ");

                if(k > -1)
                {
                    int status = 200;
                    String value = header.substring(0, k);

                    try
                    {
                        status = Integer.parseInt(value);
                    }
                    catch(NumberFormatException e)
                    {
                        e.printStackTrace();
                    }

                    header = header.substring(k + 1);
                    httpResponse.setStatus(status);
                    httpResponse.setReasonPhrase(header);
                }
            }
        }

        while((bytes = IO.readLine(inputStream)).length > 0)
        {
            if(Arrays.equals(bytes, CRLF) || Arrays.equals(bytes, LF))
            {
                break;
            }

            String header = new String(bytes);
            int k = header.indexOf(":");

            if(k > -1)
            {
                String name  = header.substring(0, k).trim();
                String value = header.substring(k + 1).trim();
                httpResponse.addHeader(name, value);
            }
        }

        httpResponse.setInputStream(inputStream);
        httpResponse.setOutputStream(outputStream);
        return httpResponse;
    }

    /**
     * @param httpProtocol
     * @return float
     */
    protected static float getHttpProtocolVersion(String httpProtocol)
    {
        float version = 0.0F;

        if(httpProtocol != null && httpProtocol.startsWith("HTTP/"))
        {
            if(httpProtocol.length() > 5)
            {
                try
                {
                    version = Float.parseFloat(httpProtocol.substring(5));
                }
                catch(NumberFormatException e)
                {
                }
            }
        }

        return version;
    }
    
    public static void main(String[] args)
    {
        String url = "/gen/test/PubTomeSQL%20-%20%E5%89%AF%E6%9C%AC%20(2).xml";
        String requestURL = null;
        String queryString = null;

        if(url != null && (url = url.trim()).length() > 0)
        {
            int k = url.indexOf("?");

            if(k < 0)
            {
                k = url.indexOf("&");

                if(k > -1)
                {
                    url = url.substring(0, k) + "?" + url.substring(0, k + 1);
                }
            }

            if(k > -1)
            {
                requestURL = url.substring(0, k);
                queryString = url.substring(k + 1);
            }
            else
            {
                requestURL = url;
                queryString = "";
            }

            try
            {
                requestURL = URLDecoder.decode(requestURL, "UTF-8");
            }
            catch(IOException e)
            {
            }
        }
        else
        {
            requestURL = "/";
            queryString = "";
        }

        System.out.println("requestURL: " + requestURL);
        System.out.println("queryString: " + queryString);
    }
}
