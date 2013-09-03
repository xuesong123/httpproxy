/*
 * $RCSfile: HttpProxy.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */
package com.skin.taurus.proxy;

import java.io.IOException;
import java.net.Socket;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.http.HttpServer;
import com.skin.taurus.http.Httpd;
import com.skin.taurus.http.servlet.FileServlet;
import com.skin.taurus.http.servlet.ServletFactory;

/**
 * <p>Title: HttpProxy</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpProxy extends Httpd
{
    public HttpProxy()
    {
        super();
    }

    /**
     * @param server
     * @param socket
     */
    public HttpProxy(HttpServer server)
    {
        super(server);
    }

    /**
     * @param server
     * @param socket
     */
    public HttpProxy(HttpServer server, Socket socket)
    {
        super(server, socket);
    }

    /**
     * @return ProxyServer
     */
    public ProxyServer getProxyServer()
    {
        HttpServer httpServer = this.getHttpServer();
        
        if(httpServer instanceof ProxyServer)
        {
            return (ProxyServer)(this.getHttpServer());
        }

        return null;
    }

    /**
     * @return ProxyManager
     */
    protected ProxyManager getProxyManager()
    {
        ProxyServer proxyServer = this.getProxyServer();

        if(proxyServer != null)
        {
            return proxyServer.getProxyManager();
        }

        return null;
    }

    /**
     * @param request
     * @return ProxyHost
     */
    protected ProxyHost getProxyHost(HttpRequest request)
    {
        ProxyManager manager = this.getProxyManager();

        if(manager != null)
        {
            return manager.getProxyHost(request);
        }

        return null;
    }

    /**
     * @param request
     * @param response
     */
    @Override
    protected void service(HttpRequest request, HttpResponse response) throws IOException
    {
        boolean flag = true;
        ProxyHost proxyHost = this.getProxyHost(request);

        if(proxyHost != null && proxyHost.getHost() != null)
        {
            String host = this.getHttpServer().getHost();
            int port = this.getHttpServer().getPort();

            if(port == proxyHost.getPort())
            {
                if(host.equals(proxyHost.getHost()) || proxyHost.getHost().equals("127.0.0.1") || proxyHost.getHost().equals("localhost"))
                {
                    flag = false;
                }
            }
        }
        else
        {
            flag = false;
        }

        if(flag)
        {
            String method = request.getMethod();

            if(method.equalsIgnoreCase("CONNECT"))
            {
                new TunnelDispatcher().dispatch(request, response, proxyHost);
            }
            else
            {
                new ProxyDispatcher().dispatch(request, response, proxyHost);
            }
        }
        else
        {
            FileServlet instance = ServletFactory.getFileServlet();

            if(instance != null)
            {
                instance.service(request, response, "/error/404.html");
            }
        }
    }
}
