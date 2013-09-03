/*
 * $RCSfile: SocketServer.java,v $$
 * $Revision: 1.1  $
 * $Date: 2006-10-21  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.skin.thread.ThreadPool;

/**
 * <p>Title: SocketServer</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public abstract class SocketServer implements Runnable
{
    private String host;
    private int port;
    private volatile int state = 0;
    private int timeout = 5000;
    private ThreadPool threadPool;

    protected SocketServer()
    {
        this(80);
    }

    /**
     * @param port
     */
    protected SocketServer(int port)
    {
        try
        {
            this.host = InetAddress.getLocalHost().getHostAddress();
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
        }

        this.port = port;
    }

    /**
     * @return String
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the timeout
     */
    public int getTimeout()
    {
        return this.timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * @param String
     */
    public String getServerInfo()
    {
        return "Server start on [" + this.getHost() + ":" + this.getPort() + "]";
    }
    
    /**
     * @return the threadPool
     */
    public ThreadPool getThreadPool()
    {
        return this.threadPool;
    }

    /**
     * @param threadPool the threadPool to set
     */
    public void setThreadPool(ThreadPool threadPool)
    {
        this.threadPool = threadPool;
    }

    /**
     */
    public void start()
    {
        if(this.state == 0)
        {
            this.state = 1;
            Thread thread = new Thread(this);
            thread.setDaemon(false);
            thread.start();
        }
    }

    public void stop()
    {
        this.state = 0;
    }

    public void run()
    {
        ServerSocket socketServer = null;

        try
        {
            socketServer = new ServerSocket(this.getPort());
            socketServer.setSoTimeout(this.getTimeout());

            while(this.state == 1)
            {
                try
                {
                    Socket socket = socketServer.accept();
                    this.service(socket);
                }
                catch(IOException e)
                {
                }
            }

            System.out.println("Server Stopped !");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(socketServer != null)
            {
                try
                {
                    socketServer.close();
                }
                catch(IOException e)
                {
                }
            }
        }
    }

    /**
     * @param socket
     */
    protected abstract void service(Socket socket);
}
