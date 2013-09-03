/*
 * $RCSfile: ProxyHost.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-6 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

/**
 * <p>Title: ProxyHost</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ProxyHost
{
    private String host;
    private int port;

    /**
     * @param host
     * @param port
     */
    public ProxyHost(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return this.host;
    }
    
    /**
     * @param host the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }
    
    /**
     * @return the port
     */
    public int getPort()
    {
        return this.port;
    }
    
    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    public int hashCode()
    {
        return (this.host + ":" + this.port).hashCode();
    }

    public boolean equals(ProxyHost proxyHost)
    {
        if(proxyHost != null)
        {
            return (this.host != null && this.host.equals(proxyHost.host)) && (this.port == proxyHost.port);
        }

        return false;
    }
}
