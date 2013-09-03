/*
 * $RCSfile: Httpd.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: Httpd</p>
 * <p>Description: </p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class Httpd implements Runnable
{
    private Socket socket = null;
    private HttpServer httpServer = null;
    private static Logger logger = LoggerFactory.getLogger(Httpd.class);
    private static Logger accesslogger = LoggerFactory.getLogger("accesslog");

    protected Httpd()
    {
    }

    /**
     * @param server
     */
    protected Httpd(HttpServer server)
    {
        this.httpServer = server;
    }

    /**
     * @param httpServer
     * @param socket
     */
    protected Httpd(HttpServer server, Socket socket)
    {
        this.socket = socket;
        this.httpServer = server;
    }

    /**
     * @param httpServer
     */
    public void setHttpServer(HttpServer server)
    {
        this.httpServer = server;
    }

    /**
     * @return HttpServer
     */
    public HttpServer getHttpServer()
    {
        return this.httpServer;
    }

    /**
     * @param socket
     */
    public void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * @return Socket
     */
    public Socket getSocket()
    {
        return this.socket;
    }

    public final void run()
    {
        try
        {
            boolean keepAlive = true;
            int keepAliveTimeMillis = 3000;
            HttpRequest request = null;
            HttpResponse response = null;
            StringBuilder buffer = new StringBuilder();
 
            while(keepAlive)
            {
                long t1 = System.currentTimeMillis();
                HttpConnection httpConnection = null;
                socket.setSoTimeout(keepAliveTimeMillis);
                socket.setKeepAlive(keepAlive);

                try
                {
                    httpConnection = HttpConnectionFactory.getHttpConnection(socket);
                }
                catch(SocketTimeoutException e)
                {
                    break;
                }
                catch(SocketException e)
                {
                    break;
                }

                if(httpConnection == null)
                {
                    break;
                }

                keepAlive = httpConnection.isKeeyAlive();

                socket.setSoTimeout(30000);
                socket.setKeepAlive(keepAlive);
                request = httpConnection.getRequest();
                response = httpConnection.getResponse();

                if(keepAlive)
                {
                    keepAliveTimeMillis = httpConnection.getKeepAliveTimeMillis();

                    if(keepAliveTimeMillis < 300)
                    {
                        keepAliveTimeMillis = 300;
                    }
                }

                if(request.getMethod() == null || request.getRequestURL() == null)
                {
                    continue;
                }

                try
                {
                    response.setHeader("Server", "Httpd/1.1");
                    this.service(request, response);
                }
                catch(SocketException e)
                {
                }
                catch(Throwable t)
                {
                    t.printStackTrace();
                }

                if(accesslogger.isInfoEnabled())
                {
                    long t2 = System.currentTimeMillis();

                    buffer.setLength(0);
                    buffer.append((t2 - t1));
                    buffer.append(" \"");
                    buffer.append((request.getMethod() != null ? request.getMethod() : ""));
                    buffer.append(" ");
                    buffer.append(request.getOriginalURL());
                    buffer.append(" ");
                    buffer.append(request.getHttpProtocol());
                    buffer.append("\" ");
                    buffer.append(response.getStatus());
                    buffer.append(" ");
                    buffer.append(request.getContentLength());
                    buffer.append(" ");
                    buffer.append(response.getContentLength());

                    buffer.append(" \"");
                    buffer.append((request.getHeader("Referer") != null ? request.getHeader("Referer") : ""));
                    buffer.append("\"");
                    
                    buffer.append(" \"");
                    buffer.append((request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : ""));
                    buffer.append("\"");
                    accesslogger.info(buffer.toString());
                }

                if(socket.isClosed())
                {
                    break;
                }
            }
        }
        catch(SocketException e)
        {
            if(logger.isErrorEnabled())
            {
                logger.error(e.getMessage(), e);
            }
        }
        catch(SocketTimeoutException e)
        {
            if(logger.isErrorEnabled())
            {
                logger.error(e.getMessage(), e);
            }
        }
        catch(IOException e)
        {
            if(logger.isErrorEnabled())
            {
                logger.error(e.getMessage(), e);
            }
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch(IOException e)
            {
            }

            this.getHttpServer().getServiceFactory().close(this);
        }
    }

    /**
     * @param request
     * @param response
     */
    protected void print(HttpRequest request, HttpResponse response)
    {
        String requestURI = request.getRequestURI();

        if(requestURI.endsWith(".htm") || requestURI.endsWith(".html"))
        {
            System.out.println(request.toString());
            System.out.println(response.toString());
            System.out.println("-----------------------------------------------");
            System.out.println();
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    protected abstract void service(HttpRequest request, HttpResponse response) throws IOException;
}
