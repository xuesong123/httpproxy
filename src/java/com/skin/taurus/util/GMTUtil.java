/*
 * $RCSfile: GMTUtil.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-29  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Title: GMTUtil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class GMTUtil
{
    private static String EMPTY = "";

    private static String DATE_FORMAT_GMT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static Locale local = Locale.ENGLISH;
    private static TimeZone timeZone = TimeZone.getTimeZone("GMT");

    /**
     * @param source
     * @return Date
     */
    public static Date parse(String source)
    {
        Date date = null;

        try
        {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_GMT, local);
            dateFormat.setTimeZone(timeZone);

            date = dateFormat.parse(source);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * @param timeMillis
     * @return String
     */
    public static String format(long timeMillis)
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_GMT, local);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(new Date(timeMillis));
    }

    /**
     * @param date
     * @return String
     */
    public static String format(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_GMT, local);
        dateFormat.setTimeZone(timeZone);
        
        return (date != null ? dateFormat.format(date) : EMPTY);
    }

    /**
     * @param date
     * @return String
     */
    public static String toString(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_GMT, local);
        dateFormat.setTimeZone(timeZone);
        
        return (date != null ? dateFormat.format(date) : EMPTY);
    }
    
    public static void main(String[] args)
    {
        Date date = GMTUtil.parse("Wed, 09 Jun 2010 09:56:46 GMT");

        System.out.println(date);
    }
}
