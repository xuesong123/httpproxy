/*
 * $RCSfile: ConfigProxyManager.java,v $$
 * $Revision: 1.1 $
 * $Date: 2013-1-6 $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.taurus.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.skin.taurus.http.HttpRequest;

/**
 * <p>Title: ConfigProxyManager</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class ConfigProxyManager
{
    private static final Map<String, ProxyHost> hosts;
    
    static
    {
        hosts = getDefaultHosts(true);
    }

    /**
     * @param request
     * @return ProxyHost
     */
    public ProxyHost getProxyHost(HttpRequest request)
    {
        return getProxyHost(request.getHeader("Host"));
    }
    
    /**
     * @param host
     * @return ProxyHost
     */
    public ProxyHost getProxyHost(String host)
    {
        if(host != null)
        {
            String key = host;
            
            if(key.startsWith("www."))
            {
                key = key.substring(4);
            }

            System.out.println("host: " + host + ": " + key);

            return hosts.get(key);
        }

        return null;
    }

    /**
     * @return Map<String, ProxyHost>
     */
    private static Map<String, ProxyHost> getDefaultHosts(boolean synch)
    {
        Map<String, ProxyHost> map = new HashMap<String, ProxyHost>();
        HostConfigFactory factory = new HostConfigFactory();
        Map<String, Config> context = factory.getConfig("proxy.properties");

        if(context != null && !context.isEmpty())
        {
            Config config = context.get("hosts");

            if(config != null)
            {
                Map<String, String> configs = config.getConfigs();
                
                if(configs != null && !configs.isEmpty())
                {
                    for(Map.Entry<String, String> entry : configs.entrySet())
                    {
                        String name = entry.getKey();
                        String value = entry.getValue();
                        
                        if(name.startsWith("www."))
                        {
                            name = name.substring(4);
                        }
                        
                        String host = value;
                        int port = 80;
                        
                        int k = value.indexOf(":");
                        
                        if(k > -1)
                        {
                            host = value.substring(0, k).trim();

                            try
                            {
                               port = Integer.parseInt(value.substring(k + 1));
                            }
                            catch(NumberFormatException e)
                            {
                                continue;
                            }
                        }

                        map.put(name, new ProxyHost(host, port));
                    }
                }
            }
        }
        
        if(synch)
        {
            Map<String, ProxyHost> tmp = new ConcurrentHashMap<String, ProxyHost>();

            tmp.putAll(map);
            map = tmp;
        }

        return map;
    }
    
    public static void main(String[] args)
    {
        if(hosts != null)
        {
            for(Map.Entry<String, ProxyHost> entry : hosts.entrySet())
            {
                ProxyHost proxyHost = entry.getValue();
                System.out.println(entry.getKey() + ": " + proxyHost.getHost() + ":" + proxyHost.getPort());
            }

            System.out.println();
            
            String[] names = {
                "zongheng.com",
                "www.zongheng.com",
                "static.com",
                "localhost:7272",
                "localhost"
            };

            ConfigProxyManager configProxyManager = new ConfigProxyManager();

            for(String name : names)
            {
                ProxyHost proxyHost = configProxyManager.getProxyHost(name);

                if(proxyHost != null)
                {
                    System.out.println(name + ": " + proxyHost.getHost() + ":" + proxyHost.getPort());
                }
                else
                {
                    System.out.println(name + ": null");
                }
            }
        }
    }
}
