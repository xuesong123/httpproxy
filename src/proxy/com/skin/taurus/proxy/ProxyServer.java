/*
 * $RCSfile: ProxyServer.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import com.skin.taurus.http.HttpServer;

/**
 * <p>Title: ProxyServer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ProxyServer extends HttpServer
{
    private ProxyManager proxyManager;

    public ProxyServer(int port)
    {
        super(port);
    }

    public ProxyServer(int port, ProxyManager proxyManager)
    {
        super(port);
        this.proxyManager = proxyManager;
    }

    /**
     * @param proxyManager
     */
    public void setProxyManager(ProxyManager proxyManager)
    {
        this.proxyManager = proxyManager;
    }

    /**
     * @return ProxyManager
     */
    public ProxyManager getProxyManager()
    {
        return this.proxyManager;
    }
}
