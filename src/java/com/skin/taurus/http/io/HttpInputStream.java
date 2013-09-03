/*
 * $RCSfile: HttpInputStream.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.io;

import java.io.IOException;
import java.io.InputStream;

import com.skin.taurus.http.HttpRequest;


/**
 * <p>Title: HttpServletInputStream</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpInputStream extends InputStream
{
    private HttpRequest request;
    private InputStream inputStream;
    private long contentLength;
    private long readedByts = 0;

    public HttpInputStream(HttpRequest request, InputStream inputStream)
    {
        this.request = request;
        this.inputStream = inputStream;
        this.contentLength = request.getContentLength();
    }

    /**
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException
    {
        return (int)this.contentLength;
    }

    /**
     * 
     * @see java.io.InputStream#close()
     * 
     */
    public void close() throws IOException
    {
        inputStream.close();
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     */
    public boolean equals(Object obj)
    {
        return inputStream.equals(obj);
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     * 
     */
    public int hashCode()
    {
        return inputStream.hashCode();
    }

    /**
     * 
     * @see java.io.InputStream#mark(int)
     * 
     */
    public void mark(int readlimit)
    {
        inputStream.mark(readlimit);
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported()
    {
        return inputStream.markSupported();
    }

    /**
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException
    {
        inputStream.reset();
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException
    {
        return inputStream.skip(n);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return inputStream.toString();
    }

    /**
     * @return - long
     */
    public long getContentLength()
    {
        return contentLength;
    }

    /**
     * @param contentLength 
     */
    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }

    /**
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException
    {
        if(readedByts >= contentLength)
        {
            return -1;
        }

        int i = this.inputStream.read();
        this.readedByts++;
        return i;
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] bytes) throws IOException
    {
        return this.read(bytes, 0, bytes.length);
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] bytes, int off, int len) throws IOException
    {
        if(readedByts >= contentLength)
        {
            return -1;
        }

        len = (int)Math.min((int)(contentLength - readedByts), (int)len);
        len = this.inputStream.read(bytes, off, len);
        this.readedByts += len;
        return len;
    }

    public HttpRequest getHttpRequest()
    {
        return this.request;
    }
    
    public InputStream getInputStream()
    {
        return this.inputStream;
    }
}
