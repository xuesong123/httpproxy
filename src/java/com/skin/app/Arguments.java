/*
 * $RCSfile: Arguments.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-21  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.util.ArrayList;
import java.util.List;

import com.skin.taurus.util.StringStream;

/**
 * <p>Title: Arguments</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Arguments
{
    private static final int EOF = -1;

    /**
     * @param arguments
     * @return String[]
     */
    public static String[] parse(String arguments)
    {
        String temp = arguments;

        if(arguments != null)
        {
            temp = arguments.trim();
        }
        else
        {
            temp = "";
        }

        List<String> list = new ArrayList<String>();

        if(temp.length() > 0)
        {
            int i;
            char c;
            char d;

            StringBuilder buf = new StringBuilder();
            StringStream stream = new StringStream(temp);

            while((i = stream.read()) != EOF)
            {
                c = (char)i;

                if(c == '"' || c == '\'')
                {
                    d = c;

                    if(buf.length() > 0)
                    {
                        list.add(buf.toString());
                        buf.setLength(0);
                    }

                    read(stream, buf, d);

                    if(buf.length() > 0)
                    {
                        list.add(buf.toString());
                        buf.setLength(0);
                    }
                }
                else if(c == ' ')
                {
                    if(buf.length() > 0)
                    {
                        list.add(buf.toString());
                        buf.delete(0, buf.length());
                    }
                }
                else
                {
                    stream.back();
                    read(stream, buf, ' ');

                    if(buf.length() > 0)
                    {
                        list.add(buf.toString());
                        buf.setLength(0);
                    }
                }
            }

            if(buf.length() > 0)
            {
                list.add(buf.toString());
            }
        }

        String[] args = new String[list.size()];
        return list.toArray(args);
    }

    /**
     * @param reader
     * @param buf
     * @param token
     */
    private static void read(StringStream stream, StringBuilder buf, char token)
    {
        int i;
        char c;

        while((i = stream.read()) != EOF)
        {
            c = (char)i;

            if(c == '\\')
            {
                escape(stream, buf);
            }
            else
            {
                if(c != token)
                {
                    buf.append(c);
                }
                else
                {
                    break;
                }
            }
        }
    }

    /**
     * @param reader
     * @param buf
     */
    private static void escape(StringStream stream, StringBuilder buf)
    {
        char c = (char)(stream.read());

        if(c != EOF)
        {
            switch(c)
            {
                case 'n':
                {
                    buf.append('\n');
                    break;
                }
                case 't':
                {
                    buf.append('\t');
                    break;
                }
                case 'b':
                {
                    buf.append('\b');
                    break;
                }
                case 'r':
                {
                    buf.append('\r');
                    break;
                }
                case 'f':
                {
                    buf.append('\f');
                    break;
                }
                case '\'':
                {
                    buf.append('\'');
                    break;
                }
                case '\"':
                {
                    buf.append('\"');
                    break;
                }
                case '\\':
                {
                    buf.append('\\');
                    break;
                }
                case 'u':
                {
                    char[] cbuf = new char[4];
                    int i = stream.read(cbuf);

                    if(i == 4)
                    {
                        String hex = new String(cbuf);

                        try
                        {
                            Integer value = Integer.parseInt(hex, 16);

                            buf.append((char)(value.intValue()));
                        }
                        catch(NumberFormatException e)
                        {
                        }
                    }

                    break;
                }
                default:
                {
                    stream.back();

                    char[] cbuf = new char[3];
                    int i = stream.read(cbuf);

                    if(i == 3)
                    {
                        String oct = new String(cbuf);

                        try
                        {
                            Integer value = Integer.parseInt(oct, 8);

                            buf.append((char)(value.intValue()));
                        }
                        catch(NumberFormatException e)
                        {

                        }
                    }

                    break;
                }
            }
        }
    }
}
