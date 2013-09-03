/*
 * $RCSfile: HttpParser.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-8-16 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.skin.taurus.http.HttpHeader;

/**
 * <p>Title: HttpParser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @version 1.0
 */
public class HttpParser
{
    private static final byte[] LF = new byte[]{0x0A};
    private static final byte[] CRLF = new byte[]{0x0D, 0x0A};

    /**
     * @param inputStream
     * @return Map<String, Object>
     * @throws IOException
     */
    public static Map<String, Object> parse(InputStream inputStream) throws IOException
    {
        byte[] bytes = readLine(inputStream);
        Map<String, Object> map = new HashMap<String, Object>();

        if(bytes.length < 1)
        {
            return map;
        }

        HttpHeader httpHeader = new HttpHeader();
        if(!Arrays.equals(bytes, CRLF) && !Arrays.equals(bytes, LF))
        {
            String header = new String(bytes).trim();
            int k = header.indexOf(" ");

            if(k > -1)
            {
                map.put("HttpProtocol", header.substring(0, k));

                header = header.substring(k + 1);
                k = header.indexOf(" ");

                if(k > -1)
                {
                    int status = 200;
                    String value = header.substring(0, k);

                    try
                    {
                        status = Integer.parseInt(value);
                    }
                    catch(NumberFormatException e)
                    {
                        e.printStackTrace();
                    }

                    header = header.substring(k + 1);
                    map.put("Status", status);
                    map.put("ReasonPhrase", header);
                }
            }
        }

        while((bytes = readLine(inputStream)).length > 0)
        {
            if(Arrays.equals(bytes, CRLF) || Arrays.equals(bytes, LF))
            {
                break;
            }

            String header = new String(bytes);
            int k = header.indexOf(":");

            if(k > -1)
            {
                String name  = header.substring(0, k).trim();
                String value = header.substring(k + 1).trim();
                httpHeader.addHeader(name, value);
            }
        }

        map.put("HttpHeader", httpHeader);
        return map;
    }

    /**
     * @param stream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] readLine(InputStream stream) throws IOException
    {
        int b = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);

        while((b = stream.read()) != -1)
        {
            if(b == '\n')
            {
                bos.write(b);
                break;
            }

            bos.write(b);
        }

        return bos.toByteArray();
    }
}
