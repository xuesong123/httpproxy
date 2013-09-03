/*
 * $RCSfile: HttpConnection.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.net.Socket;

/**
 * <p>Title: HttpConnection</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpConnection
{
    private Socket socket;
    private HttpRequest request;
    private HttpResponse response;
    private String httpProtocol = null;
    private boolean keepAlive = false;
    private int keepAliveTimeMillis = 0;

    protected HttpConnection(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * @return the socket
     */
    protected Socket getSocket()
    {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    protected void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * @return the request
     */
    protected HttpRequest getRequest()
    {
        return request;
    }

    /**
     * @param request the request to set
     */
    protected void setRequest(HttpRequest request)
    {
        this.request = request;
    }

    /**
     * @return the response
     */
    protected HttpResponse getResponse()
    {
        return response;
    }

    /**
     * @param response the response to set
     */
    protected void setResponse(HttpResponse response)
    {
        this.response = response;
    }

    /**
     * @param protocol
     */
    protected void setHttpProtocol(String protocol)
    {
        this.httpProtocol = protocol;
    }

    /**
     * @return String
     */
    protected String getHttpProtocol()
    {
        return this.httpProtocol;
    }

    /**
     * @param keepAlive
     */
    protected void setKeepAlive(boolean keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    /**
     * @return
     */
    protected boolean isKeeyAlive()
    {
        return this.keepAlive;
    }

    /**
     * @param keepAliveTimeMillis
     */
    public void setKeepAliveTimeMillis(int keepAliveTimeMillis)
    {
        this.keepAliveTimeMillis = keepAliveTimeMillis;
    }

    /**
     * @return long
     */
    public int getKeepAliveTimeMillis()
    {
        return this.keepAliveTimeMillis;
    }
}
