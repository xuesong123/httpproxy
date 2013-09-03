/*
 * $RCSfile: Main.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.skin.app.CommandLine;
import com.skin.app.Options;
import com.skin.taurus.config.ServletContext;
import com.skin.taurus.http.servlet.DispatchServlet;
import com.skin.taurus.http.servlet.FileServlet;
import com.skin.taurus.http.servlet.ServletFactory;
import com.skin.thread.ThreadPool;
import com.skin.thread.ThreadPoolFactory;

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
     * HttpServer
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

        Integer port = options.getInteger("-p", 8686);

        try
        {
            Properties properties = load("server.properties");

            if(properties == null)
            {
                System.exit(1);
            }

            ServletContext servletContext = new ServletContext();
            servletContext.setName(properties.getProperty("server.name"));
            servletContext.setDisplayName(properties.getProperty("server.name"));
            servletContext.setDesrciption(properties.getProperty("server.desrciption"));
            servletContext.setHome(new File(properties.getProperty("server.home")).getCanonicalPath());
            servletContext.setContextPath("/");

            FileServlet fileServlet = new FileServlet();
            fileServlet.setServletContext(servletContext);
            fileServlet.init();

            DispatchServlet dispatchServlet = new DispatchServlet();
            dispatchServlet.setServletContext(servletContext);
            dispatchServlet.setPackages(properties.getProperty("servlet.packages"));
            dispatchServlet.init();

            ServletFactory.setServletContext(servletContext);
            ServletFactory.setFileServlet(fileServlet);
            ServletFactory.setDispatchServlet(dispatchServlet);

            HttpServer server = new HttpServer(port);
            server.setServiceFactory(new DefaultServiceFactory());
            start(server);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void start(final HttpServer server)
    {
        final ThreadPool threadPool = ThreadPoolFactory.getInstance();

        try
        {
            threadPool.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        server.setThreadPool(threadPool);
        server.start();
        System.out.println(server.getClass().getName() + " Start on port " + server.getPort());
        System.out.println("Please Enter \"quit\" or \"exit\" to stop." );
        System.out.println();

        CommandLine cmd = new CommandLine(System.in)
        {
            @SuppressWarnings("unused")
            public int start(String[] args)
            {
                try
                {
                    threadPool.start();
                    server.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                return 0;
            }

            @SuppressWarnings("unused")
            public int stop(String[] args)
            {
                try
                {
                    System.out.println("Server Stop ...");
                    server.stop();
                    System.out.println("ThreadPool Stop ...");
                    threadPool.shutdown();
                    System.out.println("ThreadPool Stoped !");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                return 0;
            }

            @SuppressWarnings("unused")
            public int restart(String[] args)
            {
                try
                {
                    server.stop();
                    server.stop();
                    threadPool.start();
                    server.start();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                return 0;
            }

            @Override
            public int dump(String[] args)
            {
                return 0;
            }

            @Override
            public int exit(String[] args)
            {
                try
                {
                    server.stop();
                    threadPool.shutdown();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                return 0;
            }
        };

        try
        {
            cmd.start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param source
     * @return PropertyConfig
     */
    public static Properties load(String source)
    {
        InputStream inputStream = null;

        try
        {
            inputStream = Main.class.getClassLoader().getResourceAsStream(source);
            return load(inputStream);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch(IOException e)
                {
                }
            }
        }

        return null;
    }

    /**
     * @param inputStream
     * @return PropertyConfig
     */
    public static Properties load(InputStream inputStream)
    {
        Properties properties = new Properties();

        try
        {
            properties.load(inputStream);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return properties;
    }
}
