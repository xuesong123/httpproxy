/*
 * $RCSfile: HeaderEntry.java,v $$
 * $Revision: 1.1  $
 * $Date: 2013-3-30  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: HeaderEntry</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HeaderEntry
{
    private String name;
    private List<String> values;

    public HeaderEntry(String name)
    {
        this(name, new String[0]);
    }

    public HeaderEntry(String name, String[] values)
    {
        this.name = name;
        this.values = new ArrayList<String>();
        for(String value : values)
        {
            this.values.add(value);
        }
    }

    public HeaderEntry(String name, List<String> values)
    {
        this.name = name;
        this.values = values;
    }

    /**
     * @param value
     */
    public void addHeader(String value)
    {
        this.setValue(value, true);
    }

    /**
     * @param value
     */
    public void setHeader(String value)
    {
        this.setValue(value, false);
    }

    /**
     * @param value
     * @param append
     */
    public void setValue(String value, boolean append)
    {
        if(append == false)
        {
            this.values.clear();
        }
        this.values.add(value);
    }

    public String getHeader()
    {
        if(this.values.size() > 0)
        {
            return this.values.get(0);
        }
        
        return null;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @return the value
     */
    public List<String> getValues()
    {
        return this.values;
    }
    
    /**
     * @param value the value to set
     */
    public void setValues(List<String> values)
    {
        this.values = values;
    }
}
