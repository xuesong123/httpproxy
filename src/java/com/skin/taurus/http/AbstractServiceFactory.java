/*
 * $RCSfile: AbstractServiceFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-26  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.net.Socket;

import com.skin.pool.DefaultObjectPool;
import com.skin.pool.ObjectFactory;

/**
 * <p>Title: AbstractServiceFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class AbstractServiceFactory extends DefaultObjectPool implements ObjectFactory, ServiceFactory
{
    public AbstractServiceFactory()
    {
        this.setObjectFactory(this);
    }

    @Override
    public Httpd getInstance(HttpServer server, Socket socket)
    {
        Httpd instance = (Httpd)(this.create());

        if(instance != null)
        {
            instance.setHttpServer(server);
            instance.setSocket(socket);
        }

        return instance;
    }

    @Override
    public void destroy(Object object)
    {
    }

    @Override
    public void close(Httpd httpd)
    {
        super.close(httpd);
    }
}
