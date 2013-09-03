/*
 * $RCSfile: Options.java,v $$
 * $Revision: 1.1  $
 * $Date: 2008-5-28  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <p>Title: Options</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Options extends Getter
{
    private Map<String, String> options;

    /**
     * @param args
     */
    public Options(String args)
    {
        this.options = new LinkedHashMap<String, String>();
        this.parse(Arguments.parse(args));
    }

    /**
     * @param args
     */
    public Options(String[] args)
    {
        options = new HashMap<String, String>();

        this.parse(args);
    }

    /**
     * @param args
     */
    public void parse(String[] args)
    {
        int j = 0;
        String s1 = null;
        String s2 = null;

        String name = null;
        String value = null;

        for(int i = 0; i < args.length; i++)
        {
            s1 = args[i];

            if(s1 != null && (s1 = s1.trim()).length() > 0)
            {
                if(s1.startsWith("-"))
                {
                    j = i + 1;

                    if(j < args.length)
                    {
                        s2 = args[j];

                        if(s2 != null && (s2 = s2.trim()).length() > 0)
                        {
                            if(s2.startsWith("-") == false)
                            {
                                name = s1;
                                value = s2;

                                options.put(name, value);

                                i = j;
                            }
                            else
                            {
                                name = parseName(s1);
                                value = parseValue(s1);

                                options.put(name, value);
                            }
                        }
                    }
                    else
                    {
                        name = parseName(s1);
                        value = parseValue(s1);

                        options.put(name, value);
                    }
                }
                else
                {
                    j = args[i].indexOf(":");

                    if(j < 0)
                    {
                        j = args[i].indexOf("=");
                    }

                    if(j > -1)
                    {
                        name = args[i].substring(0, j);
                        value = args[i].substring(j + 1);

                        options.put(name, value);
                    }
                }
            }
        }
    }

    /**
     * @param name
     * @return String
     */
    private static String parseName(String name)
    {
        int i = name.indexOf(":");

        if(i < 0)
        {
            i = name.indexOf("=");
        }

        if(i > -1)
        {
            return name.substring(0, i);
        }

        return name;
    }

    /**
     * @param name
     * @return String
     */
    private static String parseValue(String name)
    {
        int i = name.indexOf(":");

        if(i < 0)
        {
            i = name.indexOf("=");
        }

        if(i > -1)
        {
            return name.substring(i + 1);
        }

        return "true";
    }

    @Override
    public String getValue()
    {
        return null;
    }

    @Override
    public String getValue(String name)
    {
        return options.get(name);
    }

    /**
     * @param name
     * @return
     */
    public String getOption(String name)
    {
        return this.getValue(name);
    }

    /**
     * @param name
     * @return
     */
    public String getRequired(String name)
    {
        String value = this.getValue(name);

        if(value == null)
        {
            throw new java.lang.IllegalArgumentException("the parameter named '" + name + "' must be not null !");
        }

        return value;
    }

    /**
     * @param name
     * @param value
     * @return
     */
    public String setOption(String name, String value)
    {
        return this.options.put(name, value);
    }

    /**
     * @param name
     * @return
     */
    public String remove(String name)
    {
        return this.options.remove(name);
    }

    /**
     * @return
     */
    public String getArguments()
    {
        StringBuilder buffer = new StringBuilder();

        for(Map.Entry<String, String> entry: this.options.entrySet())
        {
            String name = entry.getKey();
            String value = entry.getValue();
            buffer.append("\"").append(name).append(":").append(value).append("\" ");
        }

        if(buffer.length() > 0)
        {
            buffer.deleteCharAt(buffer.length() - 1);
        }

        return buffer.toString();
    }

    /**
     * @return
     */
    public String[] getArgumentsArray()
    {
        String[] args = new String[this.options.size()];

        int i = 0;

        for(Map.Entry<String, String> entry: this.options.entrySet())
        {
            String name = entry.getKey();
            String value = entry.getValue();

            args[i++] = (name + ":" + value);
        }

        return args;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        args = new String[]{"username:test", "password:1234"};

        Options options = new Options(args);

        System.out.println("username: " + options.getOption("username"));
        System.out.println("password: " + options.getOption("password"));

        options.setOption("driverClass", "test.TestDriver");

        System.out.println(options.getArguments());

        options.setOption("url", "jdbc:test:book");

        System.out.println(options.getArguments());

        options.remove("url");

        System.out.println(options.getArguments());
    }
}
