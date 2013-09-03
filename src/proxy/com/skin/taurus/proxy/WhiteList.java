/*
 * $RCSfile: WhiteList.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-3-22 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: WhiteList</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class WhiteList
{
    private Map<String, String> map = new HashMap<String, String>();

    public WhiteList(List<String> list)
    {
        if(list != null)
        {
            for(String host : list)
            {
                map.put(host, "1");
            }
        }
    }

    public boolean has(String host)
    {
        return (map.get(host) != null || map.get("*") != null);
    }
}
