/*
 * $RCSfile: EmptyDispatcher.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */
package com.skin.taurus.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.util.HttpStream;

/**
 * 
 * <p>Title: EmptyDispatcher</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class EmptyDispatcher extends ProxyDispatcher
{
    /**
     * @param request 
     */
    @Override
    public void dispatch(HttpRequest request, HttpResponse response, ProxyHost proxyHost)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            this.request(request, bos);
            System.out.println(bos.toString());
            OutputStream out = request.getOutputStream();
            System.out.println("--------------------");
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            out.write("Content-Type: text/html;charset=ISO-8859-1\r\n".getBytes());
            out.write("Date: Wed, 25 Jul 2007 02:07:55 GMT\r\n".getBytes());
            out.write("Server: Apache-Coyote/1.1\r\n".getBytes());
            out.write("\r\n".getBytes());
            out.write("<html>".getBytes());
            out.write("<head>".getBytes());
            out.write("<title>Test</title>".getBytes());
            out.write("</head>".getBytes());
            out.write("<body>".getBytes());
            out.write("<h1>Hello World !</h1>".getBytes());
            out.write("</body>".getBytes());
            out.write("<html>".getBytes());
            out.flush();
        }
        catch(IOException e)
        {
            System.out.println("Warning: " + e.getMessage());
        }
        finally
        {
        }
    }

    /**
     * 
     * @param request
     * @throws IOException
     */
    public void request(HttpRequest request, OutputStream outputStream) throws IOException
    {
        String headers = request.toString();
        System.out.print("------------------ request.headers ------------------\r\n" + headers);
        outputStream.write(headers.getBytes());
        HttpStream.pipe(request.getInputStream(), outputStream, request.getContentLength());
    }
}
