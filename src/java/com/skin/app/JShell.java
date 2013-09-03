/*
 * $RCSfile: JShell.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-19  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 * <p>Title: JShell</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class JShell
{
    /**
     * @param args
     */
    public static void main(String[] args){

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
        final StringBuilder buf = new StringBuilder();

        CommandLine cmd = new CommandLine()
        {
            @Override
            public Object process(Object context, String input)
            {
                String script = input;

                if(script != null)
                {
                    script = script.trim();

                    if(script.startsWith("@"))
                    {
                        try
                        {
                            scriptEngine.eval(new FileReader(script.substring(1)));
                        }
                        catch(FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        catch(ScriptException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if(script.equals("/g") || script.startsWith("/g "))
                        {
                            try
                            {
                                System.out.println();
                                System.out.println("// Script:");
                                System.out.println(buf.toString());

                                System.out.println("// Result:");
                                scriptEngine.eval(buf.toString());
                            }
                            catch(ScriptException e)
                            {
                                e.printStackTrace();
                            }

                            buf.setLength(0);
                            this.setPrompt(null);
                        }
                        else
                        {
                            buf.append(input).append("\r\n");
                            this.setPrompt("    ->");
                        }
                    }
                }

                return null;
            }
        };

        try
        {
            cmd.start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
