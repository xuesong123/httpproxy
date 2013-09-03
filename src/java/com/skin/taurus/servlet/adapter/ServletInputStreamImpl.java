/*
 * $RCSfile: ServletInputStreamImpl.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.servlet.adapter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * <p>Title: ServletInputStreamImpl</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ServletInputStreamImpl extends ServletInputStream
{
    private InputStream inputStream;

    public ServletInputStreamImpl(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException
    {
        return this.inputStream.read();
    }
}
