/*
 * $RCSfile: ProxyConfigFactory.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-3-28 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ProxyConfigFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ProxyConfigFactory
{
    public static WhiteList getWhiteList()
    {
        return new WhiteList(load("whitelist.conf", "utf-8"));
    }

    public static BlackList getBlackList()
    {
        return new BlackList(load("blacklist.conf", "utf-8"));
    }

    public static List<String> load(String source, String encoding)
    {
        BufferedReader buffer = null;
        InputStream inputStream = null;
        InputStreamReader reader = null;
        List<String> list = new ArrayList<String>();

        try
        {
            String line = null;
            inputStream = ProxyConfigFactory.class.getClassLoader().getResourceAsStream(source);

            if(inputStream != null)
            {
                reader = new InputStreamReader(inputStream, encoding);
                buffer = new BufferedReader(reader);
    
                while((line = buffer.readLine()) != null)
                {
                    line = line.trim();
    
                    if(line.length() < 1)
                    {
                        continue;
                    }
    
                    if(line.startsWith("#"))
                    {
                        continue;
                    }
                    
                    list.add(line);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(buffer != null)
            {
                try
                {
                    buffer.close();
                }
                catch(IOException e)
                {
                }
            }

            if(reader != null)
            {
                try
                {
                    reader.close();
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
        
        return list;
    }
}
