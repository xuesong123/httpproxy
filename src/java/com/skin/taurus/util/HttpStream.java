/*
 * $RCSfile: HttpStream.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */
package com.skin.taurus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.skin.taurus.util.IO;

/**
 * <p>Title: HttpStream</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class HttpStream
{
    /**
     * @param inputStream
     * @param outputStream
     * @param size
     * @throws IOException
     */
    public static void pipe(InputStream inputStream, OutputStream outputStream, int size) throws IOException
    {
        if(size > -1)
        {
            IO.copy(inputStream, outputStream, 4096, size);
        }
        else
        {
            IO.copy(inputStream, outputStream, 4096);
        }
    }
}
