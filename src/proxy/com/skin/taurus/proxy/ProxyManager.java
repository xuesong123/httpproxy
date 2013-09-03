/*
 * $RCSfile: ProxyManager.java,v $$
 * $Revision: 1.1  $
 * $Date: 2009-6-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import com.skin.taurus.http.HttpRequest;

/**
 * <p>Title: ProxyManager</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public interface ProxyManager
{
    /**
     * @return ProxyHost
     */
    public ProxyHost getProxyHost();
    
    /**
     * @param request
     * @return ProxyHost
     */
    public ProxyHost getProxyHost(HttpRequest request);
}
