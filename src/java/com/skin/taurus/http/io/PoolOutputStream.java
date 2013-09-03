/*
 * $RCSfile: PoolOutputStream.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: PoolOutputStream.java</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class PoolOutputStream extends OutputStream
{
    public List<OutputStream> outputStreamList;

    public PoolOutputStream()
    {
        outputStreamList = new ArrayList<OutputStream>();
    }

    public PoolOutputStream(OutputStream outputStream)
    {
        outputStreamList = new ArrayList<OutputStream>();

        outputStreamList.add(outputStream);
    }

    public PoolOutputStream(OutputStream[] outputStreamPool)
    {
        outputStreamList = new ArrayList<OutputStream>();

        for(int i = 0; i < outputStreamPool.length; i++)
        {
            if(outputStreamPool[i] != null)
            {
                outputStreamList.add(outputStreamPool[i]);
            }
        }
    }

    public void add(OutputStream outputStream)
    {
        outputStreamList.add(outputStream);
    }

    public void remove(OutputStream outputStream)
    {
        outputStreamList.remove(outputStream);
    }

    public int size()
    {
        return outputStreamList.size();
    }

    public void close() throws IOException
    {
        for(Iterator<OutputStream> iterator = outputStreamList.iterator(); iterator.hasNext();)
        {
            OutputStream outputStream = (OutputStream)(iterator.next());

            try
            {
                outputStream.close();
            }
            catch(IOException e)
            {
            }
        }
    }

    public void flush() throws IOException
    {
        for(Iterator<OutputStream> iterator = outputStreamList.iterator(); iterator.hasNext();)
        {
            OutputStream outputStream = (OutputStream)(iterator.next());

            try
            {
                outputStream.flush();
            }
            catch(IOException e)
            {
            }
        }
    }

    public void write(byte[] bytes, int offset, int len) throws IOException
    {
        for(Iterator<OutputStream> iterator = outputStreamList.iterator(); iterator.hasNext();)
        {
            OutputStream outputStream = (OutputStream)(iterator.next());

            try
            {
                outputStream.write(bytes, offset, len);
            }
            catch(IOException e)
            {
            }
        }
    }

    public void write(byte[] bytes) throws IOException
    {
        for(Iterator<OutputStream> iterator = outputStreamList.iterator(); iterator.hasNext();)
        {
            OutputStream outputStream = (OutputStream)(iterator.next());

            try
            {
                outputStream.write(bytes);
            }
            catch(IOException e)
            {
            }
        }
    }

    public void write(int b) throws IOException
    {
        for(Iterator<OutputStream> iterator = outputStreamList.iterator(); iterator.hasNext();)
        {
            OutputStream outputStream = (OutputStream)(iterator.next());

            try
            {
                outputStream.write(b);
            }
            catch(IOException e)
            {
            }
        }
    }
}
