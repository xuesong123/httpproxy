/*
 * $RCSfile: ServletContext.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-30  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.config;

import java.io.File;
import java.io.IOException;

/**
 * <p>Title: ServletContext</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ServletContext
{
    private String name;
    private String displayName;
    private String desrciption;

    private String contextPath;

    private String home;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * @return the desrciption
     */
    public String getDesrciption()
    {
        return desrciption;
    }

    /**
     * @param desrciption the desrciption to set
     */
    public void setDesrciption(String desrciption)
    {
        this.desrciption = desrciption;
    }

    /**
     * @return the home
     */
    public String getHome()
    {
        return this.home;
    }

    /**
     * @param home the home to set
     */
    public void setHome(String home)
    {
        this.home = home;
    }

    /**
     * @return the contextPath
     */
    public String getContextPath()
    {
        return this.contextPath;
    }

    /**
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    /**
     * @param uri
     * @return String
     */
    public String getRealPath(String uri)
    {
        try
        {
            String file_separator = System.getProperty("file.separator");
            String regex = file_separator.replaceAll("\\\\", "\\\\\\\\");
            String path = uri.replaceAll("/", regex);

            File file = new File(this.getHome(), path);
            return file.getCanonicalPath();
        }
        catch(IOException e)
        {
        }

        return uri;
    }
}
