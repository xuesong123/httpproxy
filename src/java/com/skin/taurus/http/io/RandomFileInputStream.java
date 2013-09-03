/*
 * $RCSfile: RandomFileInputStream.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-8 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * <p>Title: RandomFileInputStream</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class RandomFileInputStream extends InputStream
{
    private long offset = 0;
    private long length = 0;
    private long readBytes = 0;
    private RandomAccessFile randomAccessFile;

    /**
     * @param file
     * @param offset
     * @param length
     * @author xuesong.net
     */
    public RandomFileInputStream(File file) throws IOException
    {
        this(file, 0, file.length());
    }

    /**
     * @param file
     * @param offset
     * @param length
     * @author xuesong.net
     * @throws IOException
     */
    public RandomFileInputStream(File file, long offset, long length) throws IOException
    {
        this.offset = offset;
        this.length = length;
        this.randomAccessFile = new RandomAccessFile(file, "r");

        if(offset > 0)
        {
            this.randomAccessFile.seek(offset);
        }
    }

    @Override
    public int read() throws IOException
    {
        if(this.readBytes < this.length)
        {
            this.readBytes++;
            return this.randomAccessFile.read();
        }

        return -1;
    }

    @Override
    public void close() throws IOException
    {
        this.randomAccessFile.close();
    }

    /**
     * @return the offset
     */
    public long getOffset()
    {
        return this.offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    /**
     * @return the length
     */
    public long getLength()
    {
        return this.length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(long length)
    {
        this.length = length;
    }

    /**
     * @return the readBytes
     */
    public long getReadBytes()
    {
        return this.readBytes;
    }

    /**
     * @param readBytes the readBytes to set
     */
    public void setReadBytes(long readBytes)
    {
        this.readBytes = readBytes;
    }
}
