/*
 * $RCSfile: DefaultServlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.skin.taurus.util.IO;

/**
 * <p>Title: DefaultServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class DefaultServlet extends HttpServlet
{
    /**
     * @param file
     * @param outputStream
     * @param bufferSize
     * @throws IOException
     */
    protected void copy(File file, OutputStream outputStream, int bufferSize) throws IOException
    {
        FileInputStream inputStream = null;

        try
        {
            IO.copy(inputStream, outputStream, bufferSize);
        }
        finally
        {
            IO.close(inputStream);
        }
    }

    /**
     * @param file
     * @param outputStream
     * @param offset
     * @param length
     * @throws IOException
     */
    protected void copy(File file, OutputStream outputStream, long offset, long length) throws IOException
    {
        FileInputStream inputStream = null;

        try
        {
            inputStream = new FileInputStream(file);

            if(offset > 0)
            {
                inputStream.skip(offset);
            }

            IO.copy(inputStream, outputStream, 8192, length);
        }
        finally
        {
            IO.close(inputStream);
        }
    }
    
    /**
     * @param bytes
     * @return
     */
    protected byte[] gzip(byte[] bytes)
    {
        ByteArrayOutputStream outputStream = null;
        GZIPOutputStream gzipOutputStream = null;

        try
        {
            outputStream = new ByteArrayOutputStream();
            gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(bytes);
            gzipOutputStream.finish();
            gzipOutputStream.flush();
            return outputStream.toByteArray();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
