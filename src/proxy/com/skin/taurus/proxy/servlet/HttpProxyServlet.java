/*
 * $RCSfile: ProxyServlet.java,v $
 * $Revision: 1.1  $
 * $Date: 2006-10-21  $
 */

package com.skin.taurus.proxy.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skin.taurus.http.HttpHeader;
import com.skin.taurus.util.IO;

/**
 * <p>Title: ProxyServlet</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2006</p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpProxyServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private String proxyHost = null;
    private int proxyPort = 80;
    
    public void init(ServletConfig config) throws ServletException
    {
        proxyHost = config.getInitParameter("proxyHost");
        String temp = config.getInitParameter("proxyPort");

        if(temp != null)
        {
            try
            {
                proxyPort = Integer.parseInt(temp);
            }
            catch(NumberFormatException e)
            {
            }
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        Socket socket = null;
        InputStream remoteInputStream = null;
        OutputStream remoteOutputStream = null;

        try
        {
            if(proxyHost != null)
            {
                serverName = proxyHost;
                serverPort = proxyPort;
            }

            socket = this.connect(serverName, serverPort);
            remoteInputStream = socket.getInputStream();
            remoteOutputStream = socket.getOutputStream();

            String httpRequestHeader = this.getHttpRequestHeader(request).toString();
            remoteOutputStream.write(httpRequestHeader.getBytes());
            IO.copy(request.getInputStream(), remoteOutputStream, 4096);

            Map<String, Object> map = HttpParser.parse(remoteInputStream);
            Integer status = (Integer)(map.get("Status"));
            HttpHeader httpHeader = (HttpHeader)(map.get("HttpHeader"));

            response.setStatus(status);

            String[] names = httpHeader.getHeaderNames();
            
            for(String name : names)
            {
                String[] values = httpHeader.getHeaderValues(name);
                
                for(String value : values)
                {
                    response.addHeader(name, value);
                }
            }

            IO.copy(remoteInputStream, response.getOutputStream(), 4096);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(remoteInputStream != null)
            {
                try
                {
                    remoteInputStream.close();
                }
                catch(IOException e)
                {
                }
            }

            if(remoteOutputStream != null)
            {
                try
                {
                    remoteOutputStream.close();
                }
                catch(IOException e)
                {
                }
            }
            
            if(socket != null)
            {
                try
                {
                    socket.close();
                }
                catch(IOException e)
                {
                }
            }
        }
    }

    /**
     * @param request
     * @return StringBuilder
     */
    public StringBuilder getHttpRequestHeader(HttpServletRequest request)
    {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if(queryString != null && queryString.length() > 0)
        {
            requestURL.append("?").append(queryString);
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append(request.getMethod());
        buffer.append(" ");
        buffer.append(requestURL.toString());
        buffer.append(" ");
        buffer.append(request.getProtocol());
        buffer.append("\r\n");

        java.util.Enumeration<?> enu = request.getHeaderNames();

        while(enu.hasMoreElements())
        {
            String name = (String)enu.nextElement();
            java.util.Enumeration<?> values = request.getHeaders(name);

            if(values != null)
            {
                while(values.hasMoreElements())
                {
                    buffer.append(name);
                    buffer.append(": ");
                    buffer.append(values.nextElement());
                    buffer.append("\r\n");
                }
            }
        }

        buffer.append("\r\n");
        System.out.println(buffer);
        return buffer;
    }

    public Socket connect(String host, int port) throws IOException
    {
        InetAddress inetAddress = InetAddress.getByName(host);
        return new Socket(inetAddress, port);
    }
}
