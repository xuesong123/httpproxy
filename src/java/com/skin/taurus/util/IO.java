/*
 * $RCSfile: IO.java,v $
 * $Revision: 1.1  $
 * $Date: 2009-02-05  $
 */

package com.skin.taurus.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * <p>Title: IO</p>
 * <p>Description: </p>
 * @author xuesong.net
 * @version 1.0
 */
public class IO
{
    private IO(){}
    
    /**
     * @param file
     * @param charset
     * @return String
     */
    public static String read(File file, String charset, int bufferSize)
    {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;

        try
        {
            inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream, charset);

            int length = 0;
            char[] buffer = new char[bufferSize];
            StringBuilder result = new StringBuilder();

            while((length = inputStreamReader.read(buffer, 0, bufferSize)) > -1)
            {
                result.append(buffer, 0, length);
            }

            return result.toString();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            close(inputStreamReader);
            close(inputStream);
        }
    }

    /**
     * @param stream
     * @return
     * @throws IOException
     */
    public static byte[] readLine(InputStream stream) throws IOException
    {
        int b = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);

        while((b = stream.read()) != -1)
        {
            if(b == '\n')
            {
                bos.write(b);
                break;
            }

            bos.write(b);
        }

        return bos.toByteArray();
    }
    
    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static OutputStream openOutputStream(File file) throws IOException
    {
        return new FileOutputStream(file);
    }

    /**
     * @param f1
     * @param f2
     * @throws IOException
     */
    public static void copy(String f1, String f2) throws IOException
    {
        copy(new File(f1), new File(f2), true);
    }

    /**
     * @param src
     * @param tgt
     * @throws IOException
     */
    public static boolean copy(File f1, File f2, boolean b) throws IOException
    {
        if(f1 == null)
        {
            throw new NullPointerException("Source must not be null");
        }

        if(f2 == null)
        {
            throw new NullPointerException("Destination must not be null");
        }

        if(!f1.exists() || !f1.isFile())
        {
            throw new IOException(f1.getAbsolutePath() + " must be a file !");
        }

        if(f1.getCanonicalPath().equals(f2.getCanonicalPath()))
        {
            throw new IOException("Source '" + f1 + "' and destination '" + f2 + "' are the same");
        }

        if(f2.getParentFile() != null && !f2.getParentFile().exists() && !f2.getParentFile().mkdirs())
        {
            throw new IOException("Destination '" + f2 + "' directory cannot be created");
        }

        if(f2.exists() && !f2.canWrite())
        {
            throw new IOException("Destination '" + f2 + "' exists but is read-only");
        }

        FileInputStream fin = null;
        FileOutputStream fos = null;

        try
        {
            fin = new FileInputStream(f1.getAbsolutePath());
            fos = new FileOutputStream(f2.getAbsolutePath());
            copy(fin, fos);
        }
        finally
        {
            if(fin != null)
            {
                try
                {
                    fin.close();
                }
                catch(IOException e){}
            }

            if(fos != null)
            {
                try
                {
                    fos.close();
                }
                catch(IOException e){}
            }
        }

        if(b)
        {
            return f2.setLastModified(f1.lastModified());
        }

        return true;
    }

    /**
     * @param inputStream
     * @param file
     * @throws IOException
     */
    public static void copy(InputStream inputStream, File file) throws IOException
    {
        if(inputStream != null)
        {
            OutputStream outputStream = null;

            try
            {
                outputStream = IO.openOutputStream(file);

                IO.copy(inputStream, outputStream);
            }
            finally
            {
                IO.close(outputStream);
            }
        }
        else
        {
            throw new IOException("inputStream is null !");
        }
    }

    /**
     * 
     * @param file
     * @param out
     * @throws IOException
     */
    public static void copy(File file, OutputStream out) throws IOException
    {
        FileInputStream fin = null;

        try
        {
            fin = new FileInputStream(file);

            copy(fin, out);
        }
        finally
        {
            close(fin);
        }
    }

    /**
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        copy(inputStream, outputStream, 4096);
    }

    /**
     * @param inputStream
     * @param outputStream
     * @param bufferSize
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException
    {
        int length = 0;
        byte[] bytes = new byte[Math.max(bufferSize, 4096)];

        while((length = inputStream.read(bytes)) > -1)
        {
            outputStream.write(bytes, 0, length);
        }

        outputStream.flush();
    }

    /**
     * @param inputStream
     * @param outputStream
     * @param bufferSize
     * @param size
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream, int bufferSize, long size) throws IOException
    {
        if(size > 0)
        {
            int readBytes = 0;
            long count = size;
            int length = Math.min(bufferSize, (int)(size));
            byte[] buffer = new byte[length];

            while(count > 0)
            {
                if(count > length)
                {
                    readBytes = inputStream.read(buffer, 0, length);
                }
                else
                {
                    readBytes = inputStream.read(buffer, 0, (int)count);
                }

                if(readBytes > 0)
                {
                    outputStream.write(buffer, 0, readBytes);
                    count -= readBytes;
                }
                else
                {
                    break; 
                }
            }

            outputStream.flush();
        }
    }

    /**
     * @param reader
     * @param writer
     * @throws IOException
     */
    public static void copy(Reader reader, Writer writer) throws IOException
    {
        copy(reader, writer, 4096);
    }

    /**
     * @param reader
     * @param writer
     * @param bufferSize
     * @throws IOException
     */
    public static void copy(Reader reader, Writer writer, int bufferSize) throws IOException
    {
        int len = 0;
        int buf = Math.max(bufferSize, 4096);
        char[] chars = new char[buf];

        while((len = reader.read(chars)) > -1)
        {
            writer.write(chars, 0, len);
        }

        writer.flush();
    }

    /**
     * @param inputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
        copy(inputStream, bos);
        return bos.toByteArray();
    }

    /**
     * @param inputStream
     * @return ByteArrayInputStream
     * @throws IOException
     */
    public static ByteArrayInputStream toByteArrayInputStream(InputStream inputStream) throws IOException
    {
        return new ByteArrayInputStream(toByteArray(inputStream));
    }

    /**
     * @param resource
     */
    public static void close(java.io.Closeable resource)
    {
        if(resource != null)
        {
            try
            {
                resource.close();
            }
            catch(IOException e)
            {
            }
        }
    }

    /**
     * InputStream 
     * 
     * @param ins - java.io.InputStream
     *
     */
    public static void close(InputStream ins)
    {
        if(ins != null)
        {
            try
            {
                ins.close();
            }
            catch(IOException e){}
        }
    }

    /**
     * OutputStream 
     * 
     * @param out - java.io.OutputStream
     *
     */
    public static void close(OutputStream out)
    {
        if(out != null)
        {
            try
            {
                out.close();
            }
            catch(IOException e){}
        }
    }

    /**
     * BufferedReader
     * 
     * @param buf - java.io.Reader
     */
    public static void close(Reader reader)
    {
        if(reader != null)
        {
            try
            {
                reader.close();
            }
            catch(IOException e){}
        }        
    }

    /**
     * BufferedReader
     * 
     * @param buf - java.io.Reader
     */
    public static void close(Writer writer)
    {
        if(writer != null)
        {
            try
            {
                writer.close();
            }
            catch(IOException e){}
        }        
    }
}
