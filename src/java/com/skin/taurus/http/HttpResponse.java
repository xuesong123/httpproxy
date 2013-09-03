/*
 * $RCSfile: HttpResponse.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * <p>Title: HttpResponse</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpResponse
{
    private String httpProtocol;
    private int status = 0;
    private String reasonPhrase;
    private String contentType;

    private HttpHeader httpHeader;
    private boolean commit = false;
    private int bufferSize = 8192;
    private InputStream inputStream;
    private OutputStream outputStream;
    private PrintWriter writer;

    public HttpResponse()
    {
        this.httpHeader = new HttpHeader();
    }

    public void setCommit(boolean commit)
    {
        this.commit = commit;
    }

    public boolean getCommit()
    {
        return this.commit;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    public int getBufferSize()
    {
        return this.bufferSize;
    }

    /**
     * @return HttpHeader
     */
    public void setHttpHeader(HttpHeader httpHeader)
    {
        this.httpHeader = httpHeader;
    }

    /**
     * @return HttpHeader
     */
    public HttpHeader getHttpHeader()
    {
        return this.httpHeader;
    }

    public void addHeader(String name, String value)
    {
        this.checkCommit();
        this.httpHeader.addHeader(name, value);
    }

    public void setHeader(String name, String value)
    {
        this.checkCommit();
        this.httpHeader.setHeader(name, value);
    }

    public String getHeader(String name)
    {
        return this.httpHeader.getHeader(name);
    }

    public List<String> removeHeader(String name)
    {
        return this.httpHeader.remove(name);
    }

    public int getContentLength()
    {
        int contentLength = -1;
        String value = this.getHeader("Content-Length");

        if(value == null)
        {
            return contentLength;
        }

        try
        {
            contentLength = Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
        }

        return contentLength;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentLength(long contentLength)
    {
        this.addHeader("Content-Length", Long.toString(contentLength));
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
        this.addHeader("Content-Type", contentType);
    }

    public void setChunked(boolean chunked)
    {
        this.checkCommit();

        if(chunked)
        {
            this.setHeader("Transfer-Encoding", "chunked");
        }
        else
        {
            this.setHeader("Transfer-Encoding", null);
        }
    }

    public boolean getChunked()
    {
        String chunked = this.getHeader("Transfer-Encoding");
        return (chunked != null && chunked.equals("chunked"));
    }

    public String getHttpProtocol()
    {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol)
    {
        this.checkCommit();
        this.httpProtocol = httpProtocol;
    }

    /**
     * @return the status
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status)
    {
        this.checkCommit();
        this.status = status;
    }

    /**
     * @return the reasonPhrase
     */
    public String getReasonPhrase()
    {
        return this.reasonPhrase;
    }

    /**
     * @param reasonPhrase the reasonPhrase to set
     */
    public void setReasonPhrase(String reasonPhrase)
    {
        this.checkCommit();
        this.reasonPhrase = reasonPhrase;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    /**
     * @return PrintWriter
     */
    public PrintWriter getWriter()
    {
        if(writer == null)
        {
            this.writer = new PrintWriter(this.outputStream);
        }

        return this.writer;
    }

    private void checkCommit()
    {
        if(this.commit)
        {
            throw new HttpStatusException("The Response alread Commit!");
        }
    }

    public void close() throws IOException
    {
    }

    public void flush() throws IOException
    {
        if(this.writer != null)
        {
            this.writer.flush();
        }

        this.outputStream.flush();
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.getHttpProtocol()).append(" ").append(this.getStatus()).append(" ").append(this.getReasonPhrase()).append("\r\n");
        buffer.append(this.httpHeader.toString());
        buffer.append("\r\n");
        return buffer.toString();
    }
}
