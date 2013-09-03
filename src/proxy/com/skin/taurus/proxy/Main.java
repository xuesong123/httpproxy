/*
 * $RCSfile: Main.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-14  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import com.skin.app.Options;
import com.skin.taurus.http.ServiceFactory;

/**
 * <p>Title: Main</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Main
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Options options = new Options(args);
        String arguments = options.getArguments();

        if(arguments.length() > 0)
        {
            System.out.println("Options: " + arguments);
            System.out.println();
        }

        Integer port = options.getInteger("-p", 6666);
        String proxyHost = options.getString("-proxyHost");
        Integer proxyPort = options.getInteger("-proxyPort", 80);

        DefaultProxyManager proxyManager = new DefaultProxyManager();
        ServiceFactory serviceFactory = new ProxyServiceFactory();

        if(proxyHost != null)
        {
            proxyManager.setProxyHost(new ProxyHost(proxyHost, proxyPort));
        }

        ProxyServer server = new ProxyServer(port);
        server.setProxyManager(proxyManager);
        server.setServiceFactory(serviceFactory);
        com.skin.taurus.http.Main.start(server);
    }
}
