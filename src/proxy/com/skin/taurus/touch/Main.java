/*
 * $RCSfile: Main.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-23  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.touch;

import com.skin.app.Options;
import com.skin.taurus.http.HttpServer;

/**
 * <p>Title: Main</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Main
{
    public static void main(String[] args)
    {
        Options options = new Options(args);

        String arguments = options.getArguments();

        if(arguments.length() > 0)
        {
            System.out.println("Options: " + arguments);
            System.out.println();
        }

        Integer port = options.getInteger("-p", 7272);

        HttpServer server = new HttpServer(port);
        server.setServiceFactory(new TouchServiceFactory());

        com.skin.taurus.http.Main.start(server);
    }
}
