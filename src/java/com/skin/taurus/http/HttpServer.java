/*
 * $RCSfile: HttpServer.java,v $$
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.net.SocketServer;

/**
 * <p>Title: HttpServer</p>
 * <p>Description: </p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpServer extends SocketServer
{
    private ServiceFactory serviceFactory;
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    /**
     * @param port
     */
    public HttpServer(int port)
    {
        super(port);
    }

    /**
     * @param String
     */
    @Override
    public String getServerInfo()
    {
        return "HttpServer/1.0";
    }
    
    /**
     * @param serviceFactory
     */
    public void setServiceFactory(ServiceFactory serviceFactory)
    {
        this.serviceFactory = serviceFactory;
    }

    /**
     * @return ServiceFactory
     */
    public ServiceFactory getServiceFactory()
    {
        return this.serviceFactory;
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    /**
     * @see com.skin.component.SocketServer#service(java.net.Socket)
     */
    @Override
    protected void service(Socket socket)
    {
        Httpd instance = null;

        if(this.serviceFactory != null)
        {
            instance = this.serviceFactory.getInstance(this, socket);
        }

        if(instance != null)
        {
            this.getThreadPool().dispatch(instance);
        }
        else
        {
            if(logger.isErrorEnabled())
            {
                logger.error("Can't get HttpdInstance !");
            }

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
