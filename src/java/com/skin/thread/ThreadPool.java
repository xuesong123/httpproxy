/*
 * $RCSfile: ThreadPool.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-13  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.thread;

/**
 * <p>Title: ThreadPool</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public interface ThreadPool
{
    /**
     */
    public void start() throws Exception;

    /**
     */
    public void shutdown() throws Exception;
    
    /**
     * @param job
     * @return boolean
     */
    public boolean dispatch(Runnable job);

    /**
     * @throws InterruptedException
     */
    public void join() throws InterruptedException;

    /**
     * @return int
     */
    public int getThreads();

    /**
     * @return int
     */
    public int getIdleThreads();
}
