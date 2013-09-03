/*
 * $RCSfile: ParameterParser.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-10 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: ParameterParser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ParameterParser
{
    /**
     * @param query
     * @return Map<String, List<String>>
     */
    public static Map<String, List<String>> parse(String query)
    {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        if(query == null || query.length() < 1) 
        {
            return parameters; 
        }

        String key = null;
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        for(int i = 0, length = query.length(); i < length; i++)
        {
            char c = query.charAt(i);

            if(c == '?')
            {
                if(i + 1 < length)
                {
                    query = query.substring(i + 1);
                }
                else
                {
                    query = "";
                }

                break;
            }
        }

        for(int i = 0, length = query.length(); i < length; i++)
        {
            char c = query.charAt(i);

            if(c == '?' || c == '&')
            {
                continue;
            }
            else if(c == '#')
            {
                for(i++; i < length; i++)
                {
                    c = query.charAt(i);

                    if(c == '?' || c == '&' || c == '#')
                    {
                        i--;
                        break;
                    }
                }
            }
            else if(c == '=')
            {
                for(i++; i < length; i++)
                {
                    c = query.charAt(i);

                    if(c == '?' || c == '&' || c == '#')
                    {
                        if(c == '#')
                        {
                            i--;
                        }

                        break;
                    }
                    else
                    {
                        value.append(c);
                    }
                }

                if(name.length() > 0)
                {
                    key = name.toString();
                    List<String> values = parameters.get(key);

                    if(values == null)
                    {
                        values = new ArrayList<String>();
                        parameters.put(key, values);
                    }

                    values.add(value.toString());
                }

                name.setLength(0);
                value.setLength(0);
            }
            else
            {
                name.append(c);
            }
        }

        return parameters;
    };

    /**
     * @param parameters
     * @param encoding
     * @return String
     */
    public static String serialize(Map<String, List<String>> parameters, String encoding)
    {
        StringBuilder buffer = new StringBuilder();

        if(parameters != null)
        {
            for(Map.Entry<String, List<String>> entry: parameters.entrySet())
            {
                String name = entry.getKey();
                List<String> values = entry.getValue();

                if(values != null)
                {
                    for(String value : values)
                    {
                        buffer.append(name).append("=");

                        try
                        {
                            buffer.append(java.net.URLEncoder.encode(value, encoding));
                        }
                        catch(UnsupportedEncodingException e)
                        {
                        }

                        buffer.append("&");
                    }
                }
            }

            if(buffer.length() > 0)
            {
                buffer.setLength(buffer.length() - 1);
            }
        }

        return buffer.toString();
    }
}
