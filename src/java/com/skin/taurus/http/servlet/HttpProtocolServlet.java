/*
 * $RCSfile: HttpProtocolServlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-19  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.IOException;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.util.HttpProtocol;

/**
 * <p>Title: HttpProtocolServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpProtocolServlet extends DefaultServlet
{
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException
    {
        HttpProtocol.service(request, response);
    }
}
