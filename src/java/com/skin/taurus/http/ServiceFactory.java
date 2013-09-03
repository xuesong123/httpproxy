/*
 * $RCSfile: ServiceFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-26  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

import java.net.Socket;

/**
 * <p>Title: ServiceFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public interface ServiceFactory
{
    /**
     * @param server
     * @param socket
     * @return Httpd
     */
    public Httpd getInstance(HttpServer server, Socket socket);

    /**
     * @param httpd
     */
    public void close(Httpd httpd);
}
