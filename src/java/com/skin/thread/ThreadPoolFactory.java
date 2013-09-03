/*
 * $RCSfile: ThreadPoolFactory.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-8-16 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.thread;

/**
 * <p>Title: ThreadPoolFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @version 1.0
 */
public class ThreadPoolFactory
{
    private static ThreadPool instance = null;

    public static ThreadPool getInstance()
    {
        if(ThreadPoolFactory.instance == null)
        {
            ThreadPoolFactory.instance = new ConcurrentThreadPool(512, 1024, 10000);
        }

        return ThreadPoolFactory.instance;
    }
}
