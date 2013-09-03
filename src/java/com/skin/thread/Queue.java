/*
 * $RCSfile: Queue.java,v $$
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
 * <p>Title: Queue</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Queue
{
    private transient int head;
    private transient int tail;
    private transient boolean increase;
    private transient Object[] queue;

    public Queue()
    {
        this(32, true);
    }

    public Queue(int size, boolean increase)
    {
        this.head = 0;
        this.tail = 0;
        this.increase = increase;
        this.queue = this.allocate(size);
    }

    /**
     * @param size
     * @return Object[]
     */
    private Object[] allocate(int size)
    {
        int capacity = size;

        /*
        capacity |= (capacity >>>  1);
        capacity |= (capacity >>>  2);
        capacity |= (capacity >>>  4);
        capacity |= (capacity >>>  8);
        capacity |= (capacity >>> 16);
        capacity++;

        if(capacity < 0)
        {
            capacity >>>= 1;
        }
        */

        return new Object[capacity];
    }

    /**
     * @param e
     * @return boolean
     */
    public boolean add(Object e)
    {
        boolean result = this.offer(e);

        if(result == false)
        {
            throw new IllegalStateException("");
        }

        return true;
    }

    /**
     * @param e
     * @return boolean
     */
    public boolean offer(Object e)
    {
        if(this.tail < this.queue.length)
        {
            this.queue[this.tail] = e;
            this.tail++;
            return true;
        }
        else
        {
            if(this.head == 0)
            {
                if(this.increase == true)
                {
                    this.increase();
                    this.queue[this.tail++] = e;
                    return true;
                }

                return false;
            }
            else if(this.head < this.tail)
            {
                int length = this.tail - this.head;

                for(int i = 0, j = this.head; i < length; i++, j++)
                {
                    this.queue[i] = this.queue[j];
                    this.queue[j] = null;
                }

                this.head = 0;
                this.tail = length;
                this.queue[this.tail] = e;
                this.tail++;

                return true;
            }
            else
            {
                this.queue[0] = e;
                this.head = 0;
                this.tail = 1;
            }
        }

        return false;
    }

    public void increase()
    {
        int size = this.tail - this.head;
        Object[] array = this.allocate(size * 2);
        System.arraycopy(this.queue, this.head, array, 0, size);
        this.queue = array;
        this.head = 0;
        this.tail = size;
    }

    /**
     * get head element
     * @return Object
     */
    public Object element()
    {
        Object e = this.peek();

        if(e == null)
        {
            throw new IllegalStateException("");
        }

        return e;
    }

    /**
     * get head element
     * @return Object
     */
    public Object peek()
    {
        if(this.size() > 0 && this.head < this.queue.length)
        {
            return this.queue[this.head];
        }

        return null;
    }

    /**
     * @return e
     */
    public Object remove()
    {
        Object e = this.poll();

        if(e == null)
        {
            throw new IllegalStateException("");
        }

        return e;
    }

    /**
     * @return e
     */
    public Object poll()
    {
        if(this.size() > 0 && this.head < this.queue.length)
        {
            Object e = this.queue[this.head];
            this.queue[this.head] = null;
            this.head++;
            return e;
        }

        return null;
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.tail - this.head;
    }

    /**
     * @return int
     */
    public int length()
    {
        return this.queue.length;
    }

    public void print(java.io.PrintStream out)
    {
        this.print(new java.io.PrintWriter(out));
    }

    public void print(java.io.PrintWriter out)
    {
        out.println("length: " + this.queue.length + ", size: " + this.size() + ", head: " + this.head + ", tail: " + this.tail);

        for(int i = 0; i < this.queue.length; i++)
        {
            out.println("i: " + this.queue[i]);
        }

        out.flush();
    }
}
