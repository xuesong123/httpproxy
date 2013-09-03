/*
 * $RCSfile: ConfigFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-8-17  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: ConfigFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class ConfigFactory
{
    public Map<String, Config> getConfig(String file)
    {
        Reader reader = null;
        BufferedReader buffer = null;
        Map<String, Config> context = new HashMap<String, Config>();
        
        try
        {
            String line = null;
            String name = null;
            reader = new FileReader(file);
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
                
                boolean token = line.endsWith("{");
                
                if(token)
                {
                    name = line.substring(0, line.length() - 1).trim();
                }
                else
                {
                    name = line;
                }

                Config config = context.get(line);

                if(config == null)
                {
                    config = new Config();
                    context.put(name, config);

                    if(token)
                    {
                        this.parse(config, buffer, name, false);
                    }
                    else
                    {
                        this.parse(config, buffer, name, true);
                    }
                }
                else
                {
                    throw new IOException("SyntaxError");
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
        }
        
        return context;
    }

    /**
     * @param buffer
     * @param name
     * @throws IOException
     */
    private void parse(Config config, BufferedReader buffer, String name, boolean token) throws IOException
    {
        String line = null;

        if(token)
        {
            while((line = buffer.readLine()) != null)
            {
                line = line.trim();
    
                if(line.length() < 0)
                {
                    continue;
                }
    
                if(line.startsWith("#"))
                {
                    continue;
                }
    
                if(line.equals("{"))
                {
                    break;
                }
            }
            
            if(line.equals("{") == false)
            {
                throw new IOException("SyntaxError");
            }
        }

        while((line = buffer.readLine()) != null)
        {
            line = line.trim();

            if(line.length() < 0)
            {
                continue;
            }

            if(line.startsWith("#"))
            {
                continue;
            }

            if(line.equals("}"))
            {
                break;
            }

            this.process(config, name, line);
        }

        if(line.equals("}") == false)
        {
            throw new IOException("SyntaxError");
        }
    }

    /**
     * @param config
     * @param name
     * @param line
     */
    public abstract void process(Config config, String name, String line);
}
