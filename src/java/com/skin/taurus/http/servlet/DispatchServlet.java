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
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.annotation.Namespace;
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
    private static final Class<Servlet> SERVLET = Servlet.class;
    private static final Class<DispatchServlet> MODEL = DispatchServlet.class;
    private static final Logger logger = LoggerFactory.getLogger(MODEL);

    private String packages = null;
    private Map<String, String> actionsMap = null;
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

        if(this.contextPath.length() > 0)
        {
            requestURI = requestURI.substring(this.contextPath.length());
        }

        requestURI = this.replace(requestURI, "//", "/");

        if(logger.isDebugEnabled())
        {
            logger.debug("requestURI: " + requestURI);
        }

        String className = this.getServletName(requestURI);
        String methodName = this.getServletMethod(requestURI);

        if(logger.isDebugEnabled())
        {
            logger.debug("dispatch: " + className + "." + methodName + "()");
        }

        Servlet servlet = null;
        Throwable throwable = null;

        try
        {
            if(className == null)
            {
                this.sendError(request, response);
                return;
            }

            Class<?> type = this.getServletClass(className);

            if(type == null)
            {
                this.sendError(request, response);
                return;
            }

            servlet = this.getServletInstance(type);

            if(servlet == null)
            {
                this.sendError(request, response);
                return;
            }

            servlet.setServletContext(this.getServletContext());
            servlet.init();

            methodName = java.beans.Introspector.decapitalize(methodName);
            Method method = type.getMethod(methodName, new Class[]{HttpRequest.class, HttpResponse.class});

            if(java.lang.reflect.Modifier.isPublic(method.getModifiers()))
            {
                method.invoke(servlet, new Object[]{request, response});
            }
            else
            {
                throw new NoSuchMethodException("Can't Access 'private' or 'protected' method !");
            }
        }
        catch(SecurityException e)
        {
            throwable = e;
        }
        catch(NoSuchMethodException e)
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
            this.sendError(request, response);
        }
    }

    
    
    /**
     * @param requestURI
     * @return String
     */
    public String getServletName(String requestURI)
    {
        String className = null;
        int k = requestURI.lastIndexOf(".");

        if(k > -1)
        {
            String url = requestURI.substring(0, k);
            className = this.actionsMap.get(url);
        }
        else
        {
            className = this.actionsMap.get(requestURI);
        }

        if(className == null)
        {
            k = requestURI.lastIndexOf("/");

            if(k > -1)
            {
                String url = requestURI.substring(0, k);
                className = this.actionsMap.get(url);
            }
        }

        return className;
    }

    /**
     * @param className
     * @return Class<?>
     */
    public Class<?> getServletClass(String className)
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
                return (Servlet)(type.newInstance());
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
     * @param requestURI
     * @return String
     */
    public String getServletMethod(String requestURI)
    {
        int k = requestURI.lastIndexOf(".");

        String className = null;

        if(k > -1)
        {
            String url = requestURI.substring(0, k);
            className = this.actionsMap.get(url);
        }
        else
        {
            className = this.actionsMap.get(requestURI);
        }

        if(className != null)
        {
            return "execute";
        }

        String methodName = null;
        k = requestURI.lastIndexOf("/");

        if(k > -1)
        {
            methodName = requestURI.substring(k + 1);
        }

        if(methodName != null && (methodName = methodName.trim()).length() > 0)
        {
            k = methodName.indexOf(".");

            if(k > -1)
            {
                methodName = methodName.substring(0, k);
            }
        }
        else
        {
            methodName = "execute";
        }

        return methodName;
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
        this.actionsMap = new HashMap<String, String>();

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
     * @param urlPattern
     * @param className
     */
    public void setAction(String urlPattern, String className)
    {
        if(this.actionsMap == null)
        {
            this.actionsMap = new HashMap<String, String>();
        }

        this.actionsMap.put(urlPattern, className);
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

            if(SERVLET.isAssignableFrom(type))
            {
                Namespace namespace = type.getAnnotation(Namespace.class);

                if(namespace != null)
                {
                    path = namespace.value();
                }
            }

            if(path != null)
            {
                path = path.trim().replace('\\', '/');

                if(path.charAt(0) != '/')
                {
                    path = "/" + path;
                }

                if(path.endsWith("/"))
                {
                    path = path.substring(0, path.length() - 1);
                }

                this.actionsMap.put(path, type.getName());

                if(logger.isInfoEnabled())
                {
                    logger.info("Load Action: " + path + ", Path: " + type.getName());
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
            for(Map.Entry<String, String> entry : this.actionsMap.entrySet())
            {
                logger.debug("ActionMapping: " + entry.getKey() + ", Path: " + entry.getValue());
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
    public void sendError(HttpRequest request, HttpResponse response) throws IOException
    {
        response.setStatus(500);
        response.setReasonPhrase("Internal Server Error");
    }

    public void destroy()
    {
        this.packages = null;
        this.actionsMap.clear();
        this.actionsMap = null;
    }
}
