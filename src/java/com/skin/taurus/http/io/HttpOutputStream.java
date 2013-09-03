/*
 * $RCSfile: HttpOutputStream.java,v $$
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
import java.io.OutputStream;

import com.skin.taurus.http.HttpResponse;


/**
 * <p>Title: HttpOutputStream</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpOutputStream extends OutputStream
{
    private static final byte[] CRLF = new byte[]{0x0D, 0x0A};
    private HttpResponse response = null;
    private OutputStream outputStream = null;

    private int bufferSize = 8192;
    private int position = 0;
    private byte[] buffer = null;

    public HttpOutputStream(HttpResponse response, OutputStream outputStream, int bufferSize)
    {
        this.response = response;
        this.outputStream = outputStream;
        this.position = 0;
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream()
    {
        return this.outputStream;
    }

    public void setCommit(boolean commit)
    {
        this.response.setCommit(commit);
    }

    public boolean getCommit()
    {
        return this.response.getCommit();
    }

    public boolean getChunked()
    {
        return response.getChunked();
    }

    public void write(byte[] bytes, int offset, int length) throws IOException
    {
        if(length < this.bufferSize - this.position)
        {
            System.arraycopy(bytes, offset, this.buffer, this.position, length);
            this.position = this.position + length;
        }
        else
        {
            this.flush();
            outputStream.write(bytes, offset, length);
        }
    }

    public void write(byte[] bytes) throws IOException
    {
        this.write(bytes, 0, bytes.length);
    }

    public void write(int b) throws IOException
    {
        outputStream.write(new byte[]{(byte)b});
    }

    public void flush() throws IOException
    {
        if(this.getCommit() == false)
        {
            String headers = response.toString();
            byte[] bytes = headers.getBytes();
            outputStream.write(bytes);
            this.setCommit(true);
        }

        if(this.position > 0)
        {
            if(this.getChunked())
            {
                this.writeChunkedLength(this.position);
            }

            outputStream.write(this.buffer, 0, this.position);

            if(this.getChunked())
            {
                outputStream.write(CRLF);
            }

            this.position = 0;
            outputStream.flush();
        }
    }

    public void writeChunkedLength(int length) throws IOException
    {
        outputStream.write(Integer.toHexString(length).getBytes());
        outputStream.write(CRLF);
    }

    public void close() throws IOException
    {
        if(this.getChunked())
        {
            this.writeChunkedLength(0);
        }

        outputStream.close();
        response.close();
    }

    public int hashCode()
    {
        return outputStream.hashCode();
    }

    public String toString()
    {
        return outputStream.toString();
    }
}
