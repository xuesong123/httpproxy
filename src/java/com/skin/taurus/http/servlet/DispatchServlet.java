/*
 * $RCSfile: DispatchServlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2009-7-13  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.annotation.Namespace;
import com.skin.taurus.annotation.UrlPattern;
import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;

/**
 * <p>Title: DispatchServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class DispatchServlet extends HttpServlet
{
    private static final Class<DispatchServlet> MODEL = DispatchServlet.class;
    private static final Class<Servlet> SERVLET = Servlet.class;
    private static final Logger logger = LoggerFactory.getLogger(MODEL);

    private String packages = null;
    private Map<String, Method> actionsMap = null;
    private ClassLoader classLoader = null;
    private String contextPath = null;

    public void init()
    {
        this.contextPath = this.getServletContext().getContextPath();

        if(this.contextPath == null || this.contextPath.equals("/"))
        {
            this.contextPath = "";
        }

        if(this.packages != null)
        {
            char c;
            StringBuilder buffer = new StringBuilder();

            for(int i = 0, length = this.packages.length(); i < length; i++)
            {
                c = this.packages.charAt(i);

                if(Character.isWhitespace(c) || Character.isISOControl(c))
                {
                    continue;
                }

                buffer.append(c);
            }

            this.packages = buffer.toString();
        }

        if(logger.isInfoEnabled())
        {
            logger.info("packages: " + this.packages);
        }

        this.initActionMap();
        this.debug();
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException
    {
        this.dispatch(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void dispatch(HttpRequest request, HttpResponse response) throws IOException
    {
        String requestURI = request.getRequestURI();
        // request.setAttribute("DispatchFilter$requestURI", requestURI);

        if(this.contextPath.length() > 0)
        {
            requestURI = requestURI.substring(this.contextPath.length());
        }

        requestURI = this.replace(requestURI, "//", "/");
        Method method = this.getMethod(requestURI);

        if(method == null)
        {
            this.sendError(request, response, 404, null);
            return;
        }

        if(logger.isDebugEnabled())
        {
            logger.debug("requestURI: " + requestURI + ", dispatch: " + method.getDeclaringClass().getName() + "." + method.getName() + "()");
        }

        int status = 500;
        Servlet servlet = null;
        Throwable throwable = null;

        try
        {
            Class<?> type = method.getDeclaringClass();

            if(type == null)
            {
                status = 404;
                throw new ServletException("There is no Action mapped for url " + requestURI);
            }

            servlet = this.getServletInstance(type);

            if(servlet == null)
            {
                status = 404;
                throw new ServletException("There is no Servlet mapped for url " + requestURI);
            }

            // action.setServletContext(this.servletContext);
            // servlet.setRequest(request);
            // servlet.setResponse(response);
            // servlet.init();

            long t1 = System.currentTimeMillis();
            method.invoke(servlet, new Object[]{request, response});
            long t2 = System.currentTimeMillis();

            if(logger.isDebugEnabled())
            {
                logger.debug("execute time: " + (t2 - t1) + " - " + requestURI);
            }
        }
        catch(SecurityException e)
        {
            throwable = e;
        }
        catch(IllegalArgumentException e)
        {
            throwable = e;
        }
        catch(IllegalAccessException e)
        {
            throwable = e;
        }
        catch(InvocationTargetException e)
        {
            throwable = e;
        }
        catch(Throwable t)
        {
            throwable = t;
        }
        finally
        {
            if(servlet != null)
            {
                try
                {
                    servlet.destroy();
                }
                catch(Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }

        if(throwable != null)
        {
            Throwable t = throwable.getCause();

            if(t != null)
            {
                throwable = t;
            }
            throwable.printStackTrace();
            this.sendError(request, response, status, new ServletException(throwable));
        }
    }

    /**
     * @param requestURI
     * @return Method
     */
    public Method getMethod(String requestURI)
    {
        return this.actionsMap.get(requestURI);
    }

    /**
     * @param className
     * @return Class<?>
     */
    public Class<?> getActionClass(String className)
    {
        Class<?> clazz = null;

        try
        {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch(Exception e)
        {
        }

        if(clazz == null)
        {
            try
            {
                clazz = MODEL.getClassLoader().loadClass(className);
            }
            catch(ClassNotFoundException e)
            {
            }
        }

        if(clazz == null)
        {
            try
            {
                clazz = Class.forName(className);
            }
            catch(ClassNotFoundException e)
            {
            }
        }

        return clazz;
    }

    /**
     * @param type
     * @return Action
     */
    public Servlet getServletInstance(Class<?> type)
    {
        if(SERVLET.isAssignableFrom(type))
        {
            try
            {
                Servlet servlet = (Servlet)(type.newInstance());
                return servlet;
            }
            catch(InstantiationException e)
            {
            }
            catch(IllegalAccessException e)
            {
            }
        }

        return null;
    }

    /**
     * @param packages
     */
    public void setPackages(String packages)
    {
        this.packages = packages;
    }

    /**
     * @return String
     */
    public String getPackages()
    {
        return this.packages;
    }

    /**
     * @return ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return (classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param classLoader
     */
    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     */
    public void initActionMap()
    {
        this.actionsMap = new HashMap<String, Method>();

        if(packages != null)
        {
            String[] names = packages.split("\\s*[,]\\s*");

            for(int i = 0; i < names.length; i++)
            {
                if(logger.isInfoEnabled())
                {
                    logger.info("Find In: " + names[i]);
                }

                this.findInPackage(names[i]);
            }
        }
    }

    /**
     * @param packageName
     */
    private void findInPackage(String packageName)
    {
        packageName = packageName.replace('.', '/');
        java.util.Enumeration<URL> urls = null;

        try
        {
            urls = this.getClassLoader().getResources(packageName);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }

        while(urls.hasMoreElements())
        {
            try
            {
                String urlPath = ((URL)urls.nextElement()).getFile();
                urlPath = URLDecoder.decode(urlPath, "UTF-8");

                if(urlPath.startsWith("file:"))
                {
                    urlPath = urlPath.substring(5);
                }

                if(urlPath.indexOf('!') > 0)
                {
                    urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                }

                if(logger.isInfoEnabled())
                {
                    logger.info("Scanning for classes in [" + urlPath + "]");
                }

                File file = new File(urlPath);

                if(file.isDirectory())
                {
                    loadImplementationsInDirectory(packageName, file);
                }
                else
                {
                    loadImplementationsInJar(packageName, file);
                }
            }
            catch(IOException e)
            {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * @param parent
     * @param location
     */
    private void loadImplementationsInDirectory(String parent, File location)
    {
        File files[] = location.listFiles();

        for(int i = 0, length = files.length; i < length; i++)
        {
            File file = files[i];
            StringBuilder buffer = new StringBuilder(100);
            buffer.append(parent).append("/").append(file.getName());
            String packageOrClass = parent != null ? buffer.toString() : file.getName();

            if(file.isDirectory())
            {
                loadImplementationsInDirectory(packageOrClass, file);
                continue;
            }

            if(file.getName().endsWith(".class"))
            {
                addIfMatching(packageOrClass);
            }
        }
    }

    /**
     * @param parent
     * @param jarfile
     */
    private void loadImplementationsInJar(String parent, File jarfile)
    {
        InputStream inputStream = null;
        JarInputStream jarInputStream = null;

        try
        {
            JarEntry entry = null;
            inputStream = new FileInputStream(jarfile);
            jarInputStream = new JarInputStream(new FileInputStream(jarfile));

            while((entry = jarInputStream.getNextJarEntry()) != null)
            {
                String name = entry.getName();

                if(!entry.isDirectory() && name.startsWith(parent) && name.endsWith(".class"))
                {
                    addIfMatching(name);
                }
            }
        }
        catch(IOException e)
        {
            logger.error(e.getMessage());
            logger.error("Could not search jar file '" + jarfile + "'");
        }
        finally
        {
            if(jarInputStream != null)
            {
                try
                {
                    jarInputStream.close();
                }
                catch(IOException e)
                {
                }
            }

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
    }

    /**
     * @param fqn
     */
    private void addIfMatching(String fqn)
    {
        try
        {
            String path = null;
            String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
            Class<?> type = getClassLoader().loadClass(externalName);

            if(Modifier.isAbstract(type.getModifiers()))
            {
                return;
            }

            if(Modifier.isInterface(type.getModifiers()))
            {
                return;
            }

            if(SERVLET.isAssignableFrom(type))
            {
                Namespace namespace = type.getAnnotation(Namespace.class);

                if(namespace != null)
                {
                    path = namespace.value();
                }

                if(path == null)
                {
                    path = "";
                }

                Method[] methods = type.getMethods();

                for(Method method : methods)
                {
                    if(Modifier.isPublic(method.getModifiers()))
                    {
                        UrlPattern urlPattern = method.getAnnotation(UrlPattern.class);

                        if(urlPattern != null)
                        {
                            if(this.actionsMap.get(urlPattern.value()) != null)
                            {
                                throw new RuntimeException(urlPattern.value() + " exists !");
                            }

                            this.actionsMap.put(urlPattern.value(), method);

                            if(logger.isDebugEnabled())
                            {
                                logger.debug(urlPattern.value() + " - " + method.getDeclaringClass().getName() + "." + method.getName() + "()");
                            }
                        }
                    }
                }
            }
            else
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Ignore Type: " + type.getName());
                }
            }
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            logger.warn("Could not examine class '" + fqn + "' due to a " + t.getClass().getName() + " with message: " + t.getMessage());
        }
    }

    /**
     * @param source
     * @param search
     * @param replacement
     * @return String
     */
    private String replace(String source, String search, String replacement)
    {
        if(source == null)
        {
            return "";
        }

        if(search == null)
        {
            return source;
        }

        int s = 0;
        int e = 0;
        int d = search.length();
        StringBuilder buffer = new StringBuilder();

        do
        {
            e = source.indexOf(search, s);

            if(e == -1)
            {
                buffer.append(source.substring(s));
                break;
            }
            else
            {
                buffer.append(source.substring(s, e)).append(replacement);
                s = e + d;
            }
        }
        while(true);

        return buffer.toString();
    }

    /**
     */
    public void debug()
    {
        if(logger.isDebugEnabled())
        {
            for(Map.Entry<String, Method> entry : this.actionsMap.entrySet())
            {
                Method method = entry.getValue();
                logger.debug("ActionMapping: " + entry.getKey() + " - " + method.getDeclaringClass().getName() + "." + method.getName() + "()");
            }
        }
    }

    /**
     * @param request
     * @param response
     * @param code
     * @param throwable
     * @throws IOException
     */
    public void sendError(HttpRequest request, HttpResponse response, int code, Throwable throwable) throws IOException
    {
        if(code == 500)
        {
            // request.setAttribute("javax.servlet.error.message", throwable.getMessage());
            // request.setAttribute("javax.servlet.error.exception", throwable.getCause());
            // request.setAttribute("javax.servlet.jsp.jspException", throwable.getCause());
        }

        // response.sendError(500);
    }

    @Override
    public void destroy()
    {
        this.packages = null;
        this.actionsMap.clear();
        this.actionsMap = null;
    }
}
