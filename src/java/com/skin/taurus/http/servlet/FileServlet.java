/*
 * $RCSfile: FileServlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-30  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.http.io.RandomFileInputStream;
import com.skin.taurus.util.GMTUtil;
import com.skin.taurus.util.IO;
import com.skin.taurus.util.MimeType;

/**
 * <p>Title: FileServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class FileServlet extends DefaultServlet
{
    private String compress = "^js$|^gif$|^jpg$|^png$|^css$|^htm$|^html$";
    private String expires =  "^js$|^gif$|^jpg$|^png$|^css$|^htm$|^html$";
    private int maxAge = 180 * 24 * 60 * 60;
    private String[] welcome = new String[]{"index.htm", "index.html", "default.htm", "default.html"};

    /**
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException
    {
        this.service(request, response, request.getRequestURI());
    }

    /**
     * @param request
     * @param response
     * @param requestURI
     * @throws IOException
     */
    public void service(HttpRequest request, HttpResponse response, String requestURI) throws IOException
    {
        String homePath = this.getServletContext().getHome();
        String realPath = this.getServletContext().getRealPath(requestURI);

        if(realPath.startsWith(homePath) == false)
        {
            response.setStatus(403);
            response.setReasonPhrase("Forbidden");
            response.setContentType("text/plain");
            response.setHeader("RequestURI", requestURI);
            response.setContentLength(0L);
            return;
        }

        File file = new File(realPath);
        
        if(file.exists() == false)
        {
            response.setStatus(404);
            response.setReasonPhrase("Not Found");
            response.setContentType("text/plain");
            response.setContentLength(0L);
            response.setHeader("RequestURI", requestURI);
            return;
        }

        if(file.isDirectory())
        {
            File temp = null;

            for(int i = 0; i < this.welcome.length; i++)
            {
                temp = new File(file, this.welcome[i]);
                
                if(temp.exists() && temp.isFile())
                {
                    file = temp;
                    realPath = file.getAbsolutePath();
                    break;
                }
            }
        }

        if(file.exists() == false || file.isFile() == false)
        {
            response.setStatus(404);
            response.setReasonPhrase("Not Found");
            response.setContentType("text/plain");
            response.setHeader("RequestURI", requestURI);
            response.setContentLength(0L);
            return;
        }

        long length = file.length();
        String fileName = file.getName();
        String extension = this.getExtension(realPath);
        long createTime = file.lastModified();
        long lastModified = file.lastModified();
        String contentType = MimeType.getMimeType(file.getName());;
        String etag = this.getETag(lastModified, 0L, length);
        boolean compress = Pattern.matches(this.compress, extension);

        response.setHeader("Last-Modified", GMTUtil.format(lastModified));
        response.setHeader("ETag", etag);
        response.setHeader("Date", GMTUtil.format(createTime));
        response.setHeader("Content-Type", contentType);
        response.setHeader("Accept-Ranges", "bytes");

        if(Pattern.matches(this.expires, extension))
        {
            // Date expires = new Date(System.currentTimeMillis());
            // expires.setTime(expires.getTime() + this.maxAge * 1000);
            // response.setHeader("Expires", GMTUtil.format(expires));
            // response.setHeader("Cache-Control", "max-age=" + this.maxAge);
        }

        if(request.getHeader("Range") != null)
        {
            Range range = this.getRange(request, length);

            if(range != null)
            {
                response.setContentLength((range.end - range.start + 1));
                response.setHeader("Content-Disposition", "attachment;filename=\"" + this.encode(fileName, "UTF-8") + "\"");
                response.setHeader("Content-Range", range.range);
                this.write(request, response, new RandomFileInputStream(file, range.start, range.end - range.start), compress, 206, "Partial");
                return;
            }
            else
            {
                response.setStatus(416);
                response.setReasonPhrase("Request Range Not Satisfiable");
                response.setContentLength(0L);
                return;
            }
        }
        else
        {
            String ifModifiedSince = request.getHeader("if-modified-since");
            String ifNoneMatch = request.getHeader("if-none-match");
            boolean modified = true;
            boolean noneMath = true;

            if(ifModifiedSince != null && ifModifiedSince.equals(GMTUtil.format(lastModified)))
            {
                modified = false;
            }

            if(ifNoneMatch != null && ifNoneMatch.equals(etag))
            {
                noneMath = false;
            }

            if(modified == false && noneMath == false)
            {
                response.setStatus(304);
                response.setReasonPhrase("Not Modified");
                return;
            }

            response.setContentLength(length);
            this.write(request, response, new FileInputStream(realPath), compress, 200, "OK");
            return;
        }
    }
    
    /**
     * @param request
     * @param response
     * @param outputStream
     * @param compress
     * @param status
     * @param reasonPhrase
     * @throws IOException 
     */
    public void write(HttpRequest request, HttpResponse response, InputStream inputStream, boolean compress, int status, String reasonPhrase)
    {
        try
        {
            if(compress)
            {
                String acceptEncoding = request.getHeader("Accept-Encoding");
    
                if(acceptEncoding != null)
                {
                    if(acceptEncoding.indexOf("gzip") > -1)
                    {
                        response.setStatus(status);
                        response.setReasonPhrase(reasonPhrase);
                        response.setHeader("Content-Encoding", "gzip");
        
                        OutputStream outputStream = response.getOutputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
                        IO.copy(inputStream, gzipOutputStream, 4096);
                        gzipOutputStream.finish();
                        gzipOutputStream.flush();
    
                        byte[] pack = bos.toByteArray();
                        response.setContentLength(pack.length);
                        outputStream.write(pack, 0, pack.length);
                        outputStream.flush();
                        return;
                    }
                    else if(acceptEncoding.indexOf("deflate") > -1)
                    {
                        response.setStatus(status);
                        response.setReasonPhrase(reasonPhrase);
                        response.removeHeader("Content-Length");
                        response.setHeader("Content-Encoding", "deflate");
                        OutputStream outputStream = response.getOutputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(bos);
                        IO.copy(inputStream, deflaterOutputStream, 4096);
                        deflaterOutputStream.finish();
                        deflaterOutputStream.flush();

                        byte[] pack = bos.toByteArray();
                        response.setContentLength(pack.length);
                        outputStream.write(pack, 0, pack.length);
                        outputStream.flush();
                        return;
                    }
                }
            }

            response.setStatus(status);
            response.setReasonPhrase(reasonPhrase);
            IO.copy(inputStream, response.getOutputStream(), 4096);
        }
        catch(IOException e)
        {
        }
        finally
        {
            this.close(inputStream);
        }
    }

    /**
     * @param source
     * @param charset
     * @return String
     */
    protected String encode(String source, String charset)
    {
        try
        {
            return URLEncoder.encode(source, charset);
        }
        catch(UnsupportedEncodingException e)
        {
        }

        return null; 
    }

    /**
     * @param request
     * @param length
     * @return String
     */
    protected Range getRange(HttpRequest request, long length)
    {
        String range = request.getHeader("Range");

        if(range != null)
        {
            long start = -1;
            long end = -1;

            if(range.startsWith("bytes="))
            {
                range = range.substring(6);
            }

            String[] a = range.split("-");

            if(a.length > 1)
            {
                try
                {
                    start = Integer.parseInt(a[0], 10);
                }
                catch(NumberFormatException e)
                {
                }

                try
                {
                    end = Integer.parseInt(a[1], 10);
                }
                catch(NumberFormatException e)
                {
                }

                if(start == -1)
                {
                    start = length - end;
                    end = length - 1;
                }

                if(end == -1)
                {
                    end = length - 1;
                }
            }

            if(start == -1 || end == -1 || start > end || end > length)
            {
                return null;
            }

            return new Range(start, end, "bytes " + start + "-" + end + "/" + length);
        }

        return null;
    };

    /**
     * @param lastModified
     * @param start
     * @param end
     * @return String
     */
    protected String getETag(long lastModified, long start, long end)
    {
        return ("W/\"f-" + lastModified + "\"");
    };
    
    /**
     * @param path
     * @return String
     */
    protected String getFileName(String path)
    {
        if(path != null && path.length() > 0)
        {
            int i = path.length() - 1;

            for(; i > -1; i--)
            {
                char c = path.charAt(i);

                if(c == '/' || c == '\\' || c == ':')
                {
                    break;
                }
            }

            return path.substring(i + 1);
        }

        return "";
    }

    /**
     * @param path
     * @return String
     */
    protected String getExtension(String path)
    {
        int i = path.lastIndexOf(".");

        if(i > -1)
        {
            return path.substring(i + 1);
        }

        return "";
    }
    
    /**
     * @return the expires
     */
    public String getExpires()
    {
        return expires;
    }

    /**
     * @param expires the expires to set
     */
    public void setExpires(String expires)
    {
        this.expires = expires;
    }

    /**
     * @return the maxAge
     */
    public int getMaxAge()
    {
        return maxAge;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(int maxAge)
    {
        this.maxAge = maxAge;
    }

    /**
     * @param inputStream
     */
    private void close(InputStream inputStream)
    {
        if(inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch(IOException e)
            {
            }
        }
    }
}
