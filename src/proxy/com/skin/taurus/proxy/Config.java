/*
 * $RCSfile: Config.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-8-17  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.skin.app.Getter;

/**
 * <p>Title: Config</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Config extends Getter
{
    private Map<String, String> configs;

    /**
     */
    public Config()
    {
        this(false);
    }

    /**
     * @param synch
     */
    public Config(boolean synch)
    {
        configs = (synch ? new ConcurrentHashMap<String, String>() : new HashMap<String, String>());
    }
    
    /**
     * @return Map<String, String>
     */
    public Map<String, String> getConfigs()
    {
        return this.configs;
    }
    
    /**
     * @param name
     * @param value
     */
    public void setValue(String name, String value)
    {
        if(name != null)
        {
            this.configs.put(name, value);
        }
    }

    /* (non-Javadoc)
     * @see com.skin.app.Getter#getValue(java.lang.String)
     */
    @Override
    public String getValue(String name)
    {
        return configs.get(name);
    }

    /* (non-Javadoc)
     * @see com.skin.app.Getter#getValue()
     */
    @Override
    public String getValue()
    {
        return null;
    }
}
