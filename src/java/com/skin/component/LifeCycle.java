/*
 * $RCSfile: LifeCycle.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-5-13  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.component;

/**
 * <p>Title: LifeCycle</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public interface LifeCycle
{
    public void start() throws Exception;

    public void stop() throws Exception;

    public boolean isRunning();

    public boolean isStarted();

    public boolean isStarting();

    public boolean isStopped();

    public boolean isStopping();
}
