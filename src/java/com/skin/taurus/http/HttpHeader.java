/*
 * $RCSfile: HttpHeader.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-6 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: HttpHeader</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpHeader implements java.lang.Cloneable
{
    private Map<String, HeaderEntry> headers;

    public HttpHeader()
    {
        this.headers = new LinkedHashMap<String, HeaderEntry>();
    }

    /**
     * @param name
     * @param value
     */
    public void addHeader(String name, String value)
    {
        this.setHeader(name, value, true);
    }

    /**
     * @param name
     * @param value
     */
    public void setHeader(String name, String value)
    {
        this.setHeader(name, value, false);
    }

    /**
     * @param name
     * @param value
     * @param append
     */
    private void setHeader(String name, String value, boolean append)
    {
        if(value != null)
        {
            HeaderEntry entry = this.getHeaderEntry(name);

            if(entry != null)
            {
                entry.setValue(value, append);
            }
            else
            {
                entry = new HeaderEntry(name, new String[]{value});
                this.headers.put(name.toLowerCase(), entry);
            }
        }
        else
        {
            this.remove(name);
        }
    }

    /**
     * @param name
     * @param newName
     * @param value
     */
    public void replace(String name, String newName, String value)
    {
        HeaderEntry entry = this.getHeaderEntry(name);

        if(entry != null)
        {
            this.headers.remove(entry);
            entry.setName(newName);
            entry.setValue(value, false);
            this.headers.put(newName.toLowerCase(), entry);
        }
        else
        {
            entry = new HeaderEntry(name, new String[]{value});
            this.headers.put(name.toLowerCase(), entry);
        }
    }

    /**
     * @param name
     * @return String
     */
    public String getHeader(String name)
    {
        HeaderEntry entry = this.getHeaderEntry(name);

        if(entry != null)
        {
            return entry.getHeader();
        }

        return null;
    }
    
    /**
     * @return String[]
     */
    public String[] getHeaderNames()
    {
        Set<String> set = this.headers.keySet();
        String[] names = new String[set.size()];
        return set.toArray(names);
    }

    /**
     * @param name
     * @return String[]
     */
    public String[] getHeaderValues(String name)
    {
        HeaderEntry entry = this.getHeaderEntry(name);

        if(entry != null)
        {
            List<String> values = entry.getValues();
            String[] result = new String[values.size()];
            values.toArray(result);
            return result;
        }

        return new String[0];
    }

    /**
     * @param name
     * @return List<String>
     */
    public List<String> remove(String name)
    {
        HeaderEntry entry = this.getHeaderEntry(name);

        if(entry != null)
        {
            this.headers.remove(name.toLowerCase());
            return entry.getValues();
        }

        return null;
    }

    /**
     * @param name
     * @return List<String>
     */
    public HeaderEntry getHeaderEntry(String name)
    {
        return this.headers.get(name.toLowerCase());
    }

    @Override
    public HttpHeader clone()
    {
        HttpHeader httpHeader = new HttpHeader();

        for(Map.Entry<String, HeaderEntry> entry : this.headers.entrySet())
        {
            String name = entry.getKey();
            HeaderEntry headerEntry = entry.getValue();
            List<String> values = headerEntry.getValues();

            for(String value : values)
            {
                httpHeader.addHeader(name, value);
            }
        }

        return httpHeader;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();

        for(Map.Entry<String, HeaderEntry> entry : this.headers.entrySet())
        {
            HeaderEntry headerEntry = entry.getValue();
            List<String> values = headerEntry.getValues();

            for(String value : values)
            {
                buffer.append(headerEntry.getName()).append(": ").append(value).append("\r\n");
            }
        }

        return buffer.toString();
    }
}
