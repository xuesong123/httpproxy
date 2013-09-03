/*
 * $RCSfile: ChunkedStream.java,v $
 * $Revision: 1.1  $
 * $Date: 2007-7-25  $
 */
package com.skin.taurus.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.skin.taurus.util.EncodingUtil;
import com.skin.taurus.util.IO;

/**
 * <p>Title: ChunkedStream</p> 
 * <p>Description: </p> 
 * @author xuesong.net
 * @version 1.0
 */
public class ChunkedStream
{
    /**
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static long pipe(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        return pipe(inputStream, outputStream, 4096);
    }

    /**
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static long pipe(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException
    {
        int chunkSize = 0;
        long contentLength = 0;

        do
        {
            chunkSize = getChunkSizeFromInputStream(inputStream);
            outputStream.write((Integer.toString(chunkSize, 16) + "\r\n").getBytes());

            if(chunkSize == 0)
            {
                break;
            }

            contentLength += chunkSize;
            IO.copy(inputStream, outputStream, bufferSize, chunkSize);
            outputStream.write("\r\n".getBytes());
            readCRLF(inputStream);
        }
        while(true);

        outputStream.write("\r\n".getBytes());
        outputStream.flush();
        return contentLength;
    }

    /**
     * @param inputStream
     * @throws IOException 
     */
    public static void readCRLF(InputStream inputStream) throws IOException 
    {
        int cr = inputStream.read();
        int lf = inputStream.read();

        /* 13, 10 */
        if((cr != '\r') || (lf != '\n'))
        { 
            throw new IOException("CRLF expected at end of chunk: [" + cr + "/" + lf + "]");
        }
    }

    /**
     * @param inputStream
     * @return int
     * @throws IOException
     */
    public static int getChunkSizeFromInputStream(final InputStream inputStream) throws IOException 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // States: 0=normal, 1=\r was scanned, 2=inside quoted string, -1=end

        int state = 0; 
        while (state != -1)
        {
            int b = inputStream.read();

            if(b == -1)
            { 
                throw new IOException("chunked stream ended unexpectedly");
            }

            switch (state) 
            {
                case 0: 
                    switch (b) 
                    {
                        case '\r':
                            state = 1;
                            break;
                        case '\"':
                            state = 2;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;
                case 1:
                    if (b == '\n') 
                    {
                        state = -1;
                    } 
                    else 
                    {
                        // this was not CRLF
                        throw new IOException("Protocol violation: Unexpected" + " single newline character in chunk size");
                    }
                    break;

                case 2:
                    switch (b) 
                    {
                        case '\\':
                            b = inputStream.read();
                            baos.write(b);
                            break;
                        case '\"':
                            state = 0;
                            /* fall through */
                        default:
                            baos.write(b);
                    }
                    break;
                default: throw new RuntimeException("assertion failed");
            }
        }

        //parse data
        String dataString = EncodingUtil.getAsciiString(baos.toByteArray());
        int separator = dataString.indexOf(';');
        dataString = (separator > 0) ? dataString.substring(0, separator).trim() : dataString.trim();

        int result;

        try 
        {
            result = Integer.parseInt(dataString.trim(), 16);
        } 
        catch (NumberFormatException e) 
        {
            throw new IOException ("Bad chunk size: " + dataString);
        }

        return result;
    }

    /**
     * 
     * @param inputStream
     * @param outputStream
     * @param size
     * @throws IOException
     */
    protected static void copy2(InputStream inputStream, OutputStream outputStream, int size) throws IOException
    {
        int b = 0;

        for(int i = 0; i < size && (b = inputStream.read()) > -1; i++)
        {
            System.out.print((char)(b));

            outputStream.write(b);
        }
    }

    public static void main(String[] args)
    {
        byte[] bytes = new String("01234567890123456789012345678901-->").getBytes();
        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bytes);
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();

        try
        {
            IO.copy(bis, bos, 4096, bytes.length);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("[" + bos.toString() + "]");
    }
}
