/*
 * $RCSfile: UploadServlet.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skin.taurus.http.HttpRequest;
import com.skin.taurus.http.HttpResponse;
import com.skin.taurus.servlet.adapter.HttpServletRequestAdapter;


/**
 * <p>Title: UploadServlet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class UploadServlet extends DefaultServlet
{
    private static final Logger logger = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Request.HTTP:\r\n" + request.toString());
        }

        this.upload(request, response);
    }

    public void upload(HttpRequest request, HttpResponse response) throws IOException
    {
        int maxFileSize = 1024 * 1024;
        String repository = System.getProperty("java.io.tmpdir");
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setRepository(new File(repository));
        factory.setSizeThreshold(maxFileSize * 2);
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

        servletFileUpload.setFileSizeMax(maxFileSize);
        servletFileUpload.setSizeMax(maxFileSize);

        try
        {
            HttpServletRequest httpRequest = new HttpServletRequestAdapter(request);
            List<?> list = servletFileUpload.parseRequest(httpRequest);

            for(Iterator<?> iterator = list.iterator(); iterator.hasNext();)
            {
                FileItem item = (FileItem)iterator.next();

                if(item.isFormField())
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("Form Field: " + item.getFieldName() + " " + item.toString());
                    }
                }
                else if(!item.isFormField())
                {
                    if(item.getFieldName() != null)
                    {
                        String fileName  = this.getFileName(item.getName());
                        String extension = this.getFileExtensionName(fileName);

                        if(this.isAllowed(extension))
                        {
                            try
                            {
                                this.save(item);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        item.delete();
                    }
                    else
                    {
                        item.delete();
                    }
                }
            }
        }
        catch(FileUploadException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param item
     * @throws Exception
     */
    private String save(FileItem item) throws Exception
    {
        String fileName  = this.getFileName(item.getName());
        String extension = this.getFileExtensionName(fileName);

        if(logger.isDebugEnabled())
        {
            logger.debug("Client File: " + item.getName());
            logger.debug("ContentType: " + item.getContentType());
            logger.debug("Field Name : " + item.getFieldName());
            logger.debug("File Name  : " + fileName);
            logger.debug("Extension  : " + extension);
        }

        int i = 1;
        String shortName = fileName.substring(0, fileName.length() - extension.length());
        File file = null;

        do
        {
            file = new File("./upload/" + shortName + "_" + (i) + extension);
            i++;
        }
        while(file.exists());

        if(logger.isDebugEnabled())
        {
            logger.debug("Save " + fileName + " To " + file.getAbsolutePath());
        }

        item.write(file);

        return file.getCanonicalPath();
    }

    /**
     * @param extension
     * @return
     */
    private boolean isAllowed(String extension)
    {
        String uploadFileType = "*";

        if(extension != null)
        {
            String s = extension.toLowerCase();

            if(s.startsWith("."))
            {
                s = s.substring(1);
            }

            String[] array = uploadFileType.toLowerCase().split("\\s*[|]\\s*");

            for(int i = 0; i < array.length; i++)
            {
                if(s.equals(array[i]) || array[i].equals("*"))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param file
     * @return
     */
    private String getFileName(String file)
    {
        String temp = file.replace('\\', '/');
        int k = temp.lastIndexOf('/');

        if(k > -1)
        {
            temp = temp.substring(k + 1);
        }

        return temp;
    }

    /**
     * @param file
     * @return
     */
    private String getFileExtensionName(String file)
    {
        String temp = file.replace('\\', '/');
        int k = temp.lastIndexOf('.');

        if(k > -1)
        {
            return temp.substring(k);
        }
        else
        {
            return "";
        }
    }
}
