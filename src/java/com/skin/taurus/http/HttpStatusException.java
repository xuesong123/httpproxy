/*
 * $RCSfile: HttpStatusException.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-15  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.http;

/**
 * <p>Title: HttpStatusException</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HttpStatusException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HttpStatusException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public HttpStatusException(String message)
    {
        super(message);
    }
}
