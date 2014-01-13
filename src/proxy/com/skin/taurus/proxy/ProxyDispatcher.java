/*
 * $RCSfile: Main.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.http.HttpConnectionFactory;
import com.skin.taurus.http.HttpHeader;
import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.util.ChunkedStream;
import com.skin.taurus.util.HttpStream;

/**
 * <p>Title: ProxyDispatcher</p>
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class ProxyDispatcher
{
    private static final WhiteList whiteList = ProxyConfigFactory.getWhiteList();
    private static final BlackList blackList = ProxyConfigFactory.getBlackList();
    private static final boolean useXForwardedFor = false;
    private static Logger logger = LoggerFactory.getLogger(ProxyDispatcher.class);

    /**
     * @param proxyHost
     */
    public ProxyDispatcher()
    {
    }

    /**
     * @param request 
     */
    public void dispatch(HttpRequest request, HttpResponse response, ProxyHost proxyHost)
    {
        Socket socket = null;
        String host = request.getRequestHost();
        System.out.println(request.getMethod() + " " + request.getOriginalURL());

        try
        {
            if(blackList.has(host))
            {
                response.setStatus(502);
                response.setReasonPhrase("Connection Failed");
                return;
            }

            socket = this.createSocket(proxyHost.getHost(), proxyHost.getPort());
            socket.setSoTimeout(10000);
            socket.setKeepAlive(false);
            InputStream socketInputStream = socket.getInputStream();
            OutputStream socketOutputStream = socket.getOutputStream();
            String requestURL = request.getRequestURL();

            if(requestURL.indexOf("cloudquery.php") > -1)
            {
                response.setStatus(404);
                response.setReasonPhrase("Not Found");
                return;
            }

            boolean flag = whiteList.has(host);

            if(flag == false)
            {
                String originalURL = this.removeReferer(request.getOriginalURL());
                request.setOriginalURL(originalURL);
                request.removeHeader("Cookie");
                request.removeHeader("Referer");
                request.setHeader("User-Agent", "Nyi1lb/X.0 (Wind0ws 1.0) App1eWebK1t/5X7.X2 (KHLML, l1ke Geck0) Chr0me/2X.0.1X64.1X2 Sfaari/5X7.2X");
            }

            try
            {
                if(request.getHeader("Connection") != null)
                {
                    request.setHeader("Connection", "Close");
                }

                if(request.getHeader("Proxy-Connection") != null)
                {
                    request.removeHeader("Proxy-Connection");
                    request.setHeader("Connection", "Close");
                }

                this.request(request, socketOutputStream);
            }
            catch(IOException e)
            {
            }

            HttpResponse httpResponse = HttpConnectionFactory.createHttpResponse(socketInputStream, socketOutputStream);

            if(httpResponse != null)
            {
                HttpHeader httpHeader = httpResponse.getHttpHeader().clone();

                if(flag == false)
                {
                    httpHeader.remove("Set-Cookie");
                }

                response.setHttpHeader(httpHeader);
                response.setStatus(httpResponse.getStatus());
                response.setReasonPhrase(httpResponse.getReasonPhrase());
                this.resonse(request, httpResponse, request.getOutputStream());

                if(logger.isDebugEnabled())
                {
                    logger.debug("\r\n"
                            + request.getOriginalURL() + "\r\n"
                            + request.getHttpHeaders() + "\r\n"
                            + "--- ResponeHeader ---\r\n"
                            + httpResponse.toString());
                }
            }
        }
        catch(IOException e)
        {
            if(logger.isErrorEnabled())
            {
                logger.error(request.getRequestURL(), e);
            }
        }
        finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }
                catch(IOException e)
                {
                }
            }
        }
    }

    /**
     * @param request
     * @param outputStream
     * @throws IOException
     */
    protected void request(HttpRequest request, OutputStream outputStream) throws IOException
    {
        String host = InetAddress.getLocalHost().getHostAddress();

        if(useXForwardedFor)
        {
            String xForwardedFor = request.getHeader("X-Forwarded-For");

            if(xForwardedFor == null)
            {
                xForwardedFor = host;
            }
            else
            {
                xForwardedFor = xForwardedFor + ", " + host;
            }

            // local: not set http_client_ip 
            request.setHeader("HTTP_CLIENT_IP", request.getRemoteAddr());
            request.setHeader("X-Forwarded-For", xForwardedFor);
        }

        String headers = request.getHttpHeaders();
        outputStream.write(headers.getBytes("UTF-8"));
        HttpStream.pipe(request.getInputStream(), outputStream, request.getContentLength());
        outputStream.flush();
    }

    /**
     * @param timeMillis
     * @return String
     */
    public String toGMTString(long timeMillis)
    {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date(timeMillis));
    }

    /**
     * @param response
     * @throws IOException 
     */
    private void resonse(HttpRequest request, HttpResponse response, OutputStream outputStream) throws IOException
    {
        String headers = response.toString();
        outputStream.write(headers.getBytes("UTF-8"));
        outputStream.flush();

        int status = response.getStatus();

        if(status == 304 || status == 503)
        {
            return;
        }

        if(response.getContentLength() == 0)
        {
            return;
        }

        if(response.getHeader("Transfer-Encoding") != null)
        {
            InputStream inputStream = response.getInputStream();
            long contentLength = ChunkedStream.pipe(inputStream, outputStream);
            response.setContentLength(contentLength);
        }
        else
        {
            InputStream inputStream = response.getInputStream();
            HttpStream.pipe(inputStream, outputStream, response.getContentLength());
        }

        outputStream.flush();
    }

    /**
     * @param host
     * @param port
     * @return Socket
     * @throws IOException
     */
    protected Socket createSocket(String host, int port) throws IOException
    {
        InetAddress inetAddress = InetAddress.getByName(host);
        return new Socket(inetAddress, port);
    }

    /**
     * @param requestURL
     * @return String
     */
    private String removeReferer(String requestURL)
    {
        int k = 0; 

        do{
            k = requestURL.indexOf("http%3A%2F%2F");
    
            if(k > -1)
            {
                int i = k + 13;
                for(; i < requestURL.length(); i++)
                {
                    if(requestURL.charAt(i) == '&')
                    {
                        break;
                    }
                }

                requestURL = requestURL.substring(0, k) + requestURL.substring(i);
            }
            else
            {
                break;
            }
        }while(true);

        return requestURL;
    }
}