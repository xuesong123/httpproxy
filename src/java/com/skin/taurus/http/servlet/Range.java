/*
 * $RCSfile: Range.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-6 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http.servlet;

/**
 * <p>Title: Range</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Range
{
    public long start;
    public long end;
    public String range;

    public Range(long start, long end, String range)
    {
        this.start = start;
        this.end = end;
        this.range = range;
    }
}
