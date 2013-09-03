/*
 * $RCSfile: DefaultProxyManager.java,v $$
 * $Revision: 1.1  $
 * $Date: 2009-6-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import com.skin.taurus.http.HttpRequest;

/**
 * <p>Title: DefaultProxyManager</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class DefaultProxyManager implements ProxyManager
{
    private ProxyHost proxyHost;

    /**
     * 
     */
    public DefaultProxyManager()
    {
    }

    /**
     * @param host
     * @param port
     */
    public DefaultProxyManager(String host, int port)
    {
        this.proxyHost = new ProxyHost(host, port);
    }

    public void setProxyHost(ProxyHost proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    @Override
    public ProxyHost getProxyHost()
    {
        return this.proxyHost;
    }

    @Override
    public ProxyHost getProxyHost(HttpRequest request)
    {
        ProxyHost proxyHost = this.getProxyHost();

        if(proxyHost == null)
        {
            String method = request.getMethod();
            String host = request.getRequestHost();
            int port = request.getRequestPort();

            if(method != null && method.equalsIgnoreCase("CONNECT"))
            {
                String requestURL = request.getRequestURL();
                int i = requestURL.lastIndexOf(":");

                if(i > -1)
                {
                    port = Integer.parseInt(requestURL.substring(i + 1));
                }

                proxyHost = new ProxyHost(host, port);
            }
            else
            {
                proxyHost = new ProxyHost(host, port);
            }
        }

        return proxyHost;
    }
}
