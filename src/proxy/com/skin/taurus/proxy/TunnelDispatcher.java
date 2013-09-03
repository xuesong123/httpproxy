/*
 * $RCSfile: TunnelDispatcher.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-8-15 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.http.io.HttpInputStream;
import com.skin.taurus.http.io.HttpOutputStream;
import com.skin.taurus.util.IO;
import com.skin.thread.ThreadPool;
import com.skin.thread.ThreadPoolFactory;

/**
 * <p>Title: TunnelDispatcher</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @version 1.0
 */
public class TunnelDispatcher extends ProxyDispatcher
{
    private volatile int count = 0;
    private static Logger logger = LoggerFactory.getLogger(TunnelDispatcher.class);

    /**
     * @param request 
     */
    public void dispatch(HttpRequest request, HttpResponse response, ProxyHost proxyHost)
    {
        Socket socket = null;

        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("CONNECT " + proxyHost.getHost() + ":" + proxyHost.getPort() + "\r\n"
                        + request.toString());
            }

            socket = this.createSocket(proxyHost.getHost(), proxyHost.getPort());
            InputStream remoteInputStream = socket.getInputStream();
            OutputStream remoteOutputStream = socket.getOutputStream();

            InputStream clientInputStream = ((HttpInputStream)(request.getInputStream())).getInputStream();
            OutputStream clientOutputStream = ((HttpOutputStream)(response.getOutputStream())).getOutputStream();

            this.request(request, remoteOutputStream);

            ThreadPool threadPool = ThreadPoolFactory.getInstance();
            threadPool.dispatch(new TunnelThread(clientInputStream, remoteOutputStream));
            threadPool.dispatch(new TunnelThread(remoteInputStream, clientOutputStream));

            while(this.count < 2)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch(InterruptedException e)
                {
                }
            }

            if(logger.isDebugEnabled())
            {
                logger.debug("close socket");
            }
        }
        catch(IOException e)
        {
            if(logger.isErrorEnabled())
            {
                logger.error(request.getRequestURL(), e);
            }
        }
        finally
        {
            if(socket != null)
            {
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

    /**
     * @param request
     * @param outputStream
     * @throws IOException
     */
    public void request(HttpRequest request, OutputStream outputStream) throws IOException
    {
        String headers = request.toString();
        outputStream.write(headers.getBytes());
        outputStream.flush();
    }

    public class TunnelThread implements Runnable
    {
        public InputStream inputStream;
        public OutputStream outputStream;

        public TunnelThread(InputStream inputStream, OutputStream outputStream)
        {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public void run()
        {
            try
            {
                IO.copy(inputStream, outputStream, 4096);
            }
            catch(IOException e)
            {
            }

            TunnelDispatcher.this.count++;
        }
    }
}
