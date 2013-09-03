/*
 * $RCSfile: HttpTouch.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */
package com.skin.taurus.touch;

import java.io.IOException;
import java.net.Socket;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.http.HttpServer;
import com.skin.taurus.http.Httpd;
import com.skin.taurus.util.HttpProtocol;

/**
 * <p>Title: HttpTouch</p>
 * <p>Description: </p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpTouch extends Httpd
{
    /**
     */
    public HttpTouch()
    {
        super();
    }

    /**
     * @param server
     */
    public HttpTouch(HttpServer server)
    {
        super(server);
    }
    
    /**
     * @param server
     * @param socket
     */
    public HttpTouch(HttpServer server, Socket socket)
    {
        super(server, socket);
    }
    
    /**
     * @param request
     * @param response
     */
    @Override
    protected void service(HttpRequest request, HttpResponse response) throws IOException
    {
        HttpProtocol.service(request, response);
    }
}
