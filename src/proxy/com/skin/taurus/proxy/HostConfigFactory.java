/*
 * $RCSfile: HostConfigFactory.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-8-17  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

/**
 * <p>Title: HostConfigFactory</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class HostConfigFactory extends ConfigFactory
{
    @Override
    public void process(Config config, String name, String line)
    {
        if(line != null)
        {
            int k = line.indexOf(" ");

            if(k > -1)
            {
                String key = line.substring(0, k);
                String value = line.substring(k + 1);

                if((key = key.trim()).length() > 0)
                {
                    config.setValue(key, value);
                }
            }
        }
    }
}
