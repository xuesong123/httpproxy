/*
 * $RCSfile: AbstractLifeCycle.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-13  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.component;

/**
 * <p>Title: AbstractLifeCycle</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class AbstractLifeCycle implements LifeCycle
{
    private volatile int state = 0;

    private final int STOPPED  = 0;
    private final int STARTING = 1;
    private final int STARTED  = 2;
    private final int STOPPING = 3;

    private Object lock = new Object();

    protected void doStart() throws Exception
    {
    }

    protected void doStop() throws Exception
    {
    }

    public final void start() throws Exception
    {
        synchronized(this.lock)
        {
            try
            {
                if(state == STARTED || state == STARTING)
                {
                    return;
                }

                setStarting();

                doStart();

                setStarted();
            }
            catch(Exception e)
            {
                throw e;
            }
            catch(Error e)
            {
                throw e;
            }
        }
    }

    public final void stop() throws Exception
    {
        synchronized(lock)
        {
            try
            {
                if(state == STOPPING || state == STOPPED)
                {
                    return;
                }

                setStopping();
                doStop();
                setStopped();
            }
            catch(Exception e)
            {
                throw e;
            }
            catch(Error e)
            {
                throw e;
            }
        }
    }

    private void setStarted()
    {
        state = STARTED;
    }

    private void setStarting()
    {
        state = STARTING;
    }

    private void setStopping()
    {
        state = STOPPING;
    }

    private void setStopped()
    {
        state = STOPPED;
    }

    @Override
    public boolean isRunning()
    {
        return this.state == STARTED;
    }

    @Override
    public boolean isStarted()
    {
        return this.state == STARTED;
    }

    @Override
    public boolean isStarting()
    {
        return this.state == STARTING;
    }

    @Override
    public boolean isStopped()
    {
        return this.state == STOPPED;
    }

    @Override
    public boolean isStopping()
    {
        return this.state == STOPPING;
    }
}
