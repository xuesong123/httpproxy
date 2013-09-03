/*
 * $RCSfile: DefaultObjectPool.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-7-26  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.pool;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: DefaultObjectPool</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class DefaultObjectPool implements ObjectPool
{
    private int minSize = 32;
    private int maxSize = 3000;

    private int numIdle = 0;
    private int minIdle = 32;
    private int maxIdle = 128;
    private int maxWait = 3000;
    private int numActive = 0;
    private int maxActive = 2048;

    private ObjectFactory factory = null;
    private List<Object> pool = new ArrayList<Object>();
    private List<Object> idle = new ArrayList<Object>();
    private Lock poolLock = new Lock();
    private Lock idleLock = new Lock();

    /**
     */
    public DefaultObjectPool()
    {
    }

    /**
     * @param minSize the minSize to set
     */
    public void setMinSize(int minSize)
    {
        synchronized(this)
        {
            this.minSize = minSize;
        }
    }

    /**
     * @return the minSize
     */
    public int getMinSize()
    {
        synchronized(this)
        {
            return this.minSize;
        }
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(int maxSize)
    {
        synchronized(this)
        {
            this.maxSize = maxSize;
        }
    }

    /**
     * @return the maxSize
     */
    public int getMaxSize()
    {
        synchronized(this)
        {
            return this.maxSize;
        }
    }

    /**
     * @param numIdle the numIdle to set
     */
    public void setNumIdle(int numIdle)
    {
        this.numIdle = numIdle;
    }

    /**
     * @return the numIdle
     */
    public int getNumIdle()
    {
        return this.numIdle;
    }

    /**
     * @param minIdle the minIdle to set
     */
    public void setMinIdle(int minIdle)
    {
        this.minIdle = minIdle;
    }

    /**
     * @return the minIdle
     */
    public int getMinIdle()
    {
        return this.minIdle;
    }

    /**
     * @param maxIdle the maxIdle to set
     */
    public void setMaxIdle(int maxIdle)
    {
        this.maxIdle = maxIdle;
    }

    /**
     * @return the maxIdle
     */
    public int getMaxIdle()
    {
        return this.maxIdle;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setMaxWait(int maxWait)
    {
        synchronized(this)
        {
            this.maxWait = maxWait;
        }
    }

    /**
     * @return the timeout
     */
    public int getMaxWait()
    {
        synchronized(this)
        {
            return this.maxWait;
        }
    }

    /**
     * @return the numActive
     */
    public int getNumActive()
    {
        synchronized(this)
        {
            return this.numActive;
        }
    }

    /**
     * @param numActive the numActive to set
     */
    public void setNumActive(int numActive)
    {
        synchronized(this)
        {
            this.numActive = numActive;
        }
    }

    /**
     * @param maxActive the maxActive to set
     */
    public void setMaxActive(int maxActive)
    {
        synchronized(this)
        {
            this.maxActive = maxActive;
        }
    }

    /**
     * @return the maxActive
     */
    public int getMaxActive()
    {
        synchronized(this)
        {
            return this.maxActive;
        }
    }
    
    /**
     * @param factory
     */
    public void setObjectFactory(ObjectFactory factory)
    {
        this.factory = factory;
    }
    
    /**
     * @return ObjectFactory
     */
    @Override
    public ObjectFactory getObjectFactory()
    {
        return this.factory;
    }

    @Override
    public Object getInstance()
    {
        int size  = 0;
        int index = 0;
        long t1 = 0L;
        long t2 = 0L;

        t1 = System.currentTimeMillis();
        Object object = null;

        while(true)
        {
            synchronized(this.idleLock)
            {
                index = this.idle.size();

                if(index > 0)
                {
                    object = this.idle.remove(index - 1);
                }
            }

            if(object != null)
            {
                synchronized(this)
                {
                    this.numIdle--;
                    this.numActive++;
                }

                return object;
            }
            
            synchronized(this)
            {
                size = this.pool.size();
            }
            
            if(size < this.maxSize)
            {
                object = this.getObjectFactory().create();

                synchronized(this.poolLock)
                {
                    this.pool.add(object);
                }

                synchronized(this)
                {
                    this.numActive++;
                }

                return object;
            }

            t2 = System.currentTimeMillis();

            synchronized(this.idleLock)
            {
                try
                {
                    if(this.maxWait < 0)
                    {
                        this.idleLock.wait();
                    }
                    else
                    {
                        long elapsed = t2 - t1;
                        long waitTime = this.maxWait - elapsed;

                        System.out.println("wait: " + waitTime);

                        if(waitTime > 0L)
                        {
                            this.idleLock.wait(waitTime);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                catch(InterruptedException e)
                {
                    break;
                }
            }
        }

        return null;
    }

    /**
     * @param object
     */
    public void close(Object object)
    {
        synchronized(this)
        {
            this.numIdle++;
            this.numActive--;
        }
        
        synchronized(this.idleLock)
        {
            this.idle.add(object);
            this.idleLock.notifyAll();
        }
    }

    private class Lock{}
}
