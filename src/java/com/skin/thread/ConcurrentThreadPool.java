/*
 * $RCSfile: ConcurrentThreadPool.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-31  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.thread;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.skin.component.AbstractLifeCycle;

/**
 * <p>Title: ConcurrentThreadPool</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ConcurrentThreadPool extends AbstractLifeCycle implements ThreadPool, Serializable
{
    private static final long serialVersionUID = 1L;
    private BlockingQueue<Runnable> blockingQueue;
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     */
    public ConcurrentThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime)
    {
        this.blockingQueue = new ArrayBlockingQueue<Runnable>(maximumPoolSize);
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue);
    }

    @Override
    public boolean dispatch(final Runnable job)
    {
        this.threadPoolExecutor.execute(job);
        return true;
    }

    @Override
    public int getIdleThreads()
    {
        return 0;
    }

    @Override
    public int getThreads()
    {
        return 0;
    }

    @Override
    public void join() throws InterruptedException
    {
        
    }
    
    @Override
    public void shutdown()
    {
        this.threadPoolExecutor.shutdown();
    }

    @Override
    public void doStop()
    {
        this.shutdown();
    }
}
