/*
 * $RCSfile: HttpProtocol.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-24  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.util;

import java.io.IOException;
import java.io.OutputStream;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;

/**
 * <p>Title: HttpProtocol</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpProtocol
{
    /**
     * @param request
     * @param response
     * @throws IOException
     */
    public static void service(HttpRequest request, HttpResponse response) throws IOException
    {
        String headers = request.toString();
        System.out.println(headers);
        System.out.println("------------------- End -------------------");
        System.out.println();
        System.out.println();

        byte[] bytes = (encode(headers) + "<img src=\"1.jpg\"/>").getBytes("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setContentLength(bytes.length);

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        response.flush();
    }

    /**
     * @param text
     * @return String
     */
    public static String encode(String text)
    {
        if(text == null)
        {
            return "";
        }

        char c;

        StringBuilder buf = new StringBuilder();

        for(int i = 0, size = text.length(); i < size; i++)
        {
            c = text.charAt(i);

            switch (c)
            {
                case '&':
                {
                    buf.append("&amp;");
                    break;
                }
                case '"':
                {
                    buf.append("&quot;");
                    break;
                }
                case '<':
                {
                    buf.append("&lt;");
                    break;
                }
                case '>':
                {
                    buf.append("&gt;");
                    break;
                }
                case '\r':
                {
                    if(i + 1 < size && text.charAt(i + 1) == '\n')
                    {
                        i++;
                        buf.append("<br/>\r\n");
                    }
                    else
                    {
                        buf.append(c);
                    }
                    
                    break;
                }
                case '\n':
                {
                    buf.append("<br/>\r\n");
                    break;
                }
                default :
                {
                    buf.append(c);
                    break;
                }

            }   
        }

        return buf.toString();
    }
}
