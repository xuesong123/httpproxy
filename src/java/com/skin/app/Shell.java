/*
 * $RCSfile: Shell.java,v $$
 * $Revision: 1.1  $
 * $Date: 2010-6-17  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Shell</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Shell extends CommandLine
{
    private int level = 0;
    private Shell parent;

    private List<Shell> shells;

    /**
     * @param name
     */
    private Shell(String name)
    {
        this(name, System.in);
    }

    /**
     * @param name
     */
    private Shell(String name, InputStream inpuStream)
    {
        super(name, inpuStream);

        this.level = 0;
        this.shells = new ArrayList<Shell>();
    }

    /**
     * @param name
     */
    private Shell(String name, Shell parent)
    {
        super(name, parent);

        this.level = 0;
        this.shells = new ArrayList<Shell>();
    }

    /**
     * @return Shell
     */
    public static Shell getInstance()
    {
        return getInstance(null);
    }

    /**
     * @return Shell
     */
    public static Shell getInstance(InputStream inpuStream)
    {
        return new Shell("root", inpuStream);
    }

    /**
     * @param file
     */
    public static void start(File file) throws IOException
    {
    	InputStream inputStream = null;
    	
        if(file != null)
        {
            if(file.exists() == false)
            {
                System.out.println(file.getAbsolutePath() + " not exists !");

                return;
            }

            if(file.isFile() == false)
            {
                System.out.println(file.getAbsolutePath() + " can't read !");

                return;
            }

            if(file.canRead() == false)
            {
                System.out.println(file.getAbsolutePath() + " can't read !");

                return;
            }
        }
        
        try
        {
        	inputStream = new FileInputStream(file);
        	
        	Shell.start(inputStream);
        }
        finally
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
    
    /**
     * @param inputStream
     * @throws IOException 
     */
    public static void start(InputStream inputStream) throws IOException
    {
        if(inputStream != null)
        {
            Shell shell = null;

            try
            {
            	shell = Shell.getInstance(inputStream);
            	
            	shell.start();
            }
            finally
            {
                if(shell != null)
                {
                    shell.close(false);
                }
            }
        }
        else
        {
        	Shell.getInstance().start();
        }
    }

    /**
     * @param name
     * @param inputStream
     * @return Shell
     */
    public Shell create(String name, InputStream inputStream)
    {
        Shell shell = new Shell(name, inputStream);
        shell.level = this.level + 1;
        shell.parent = this;

        this.shells.add(shell);

        return shell;
    }

    /**
     * @param name
     * @param parent
     * @return Shell
     */
    public Shell create(String name, Shell parent)
    {
        Shell shell = new Shell(name, parent);
        shell.level = this.level + 1;
        shell.parent = this;

        this.shells.add(shell);

        return shell;
    }

    /**
     * @param i
     * @throws IOException
     */
    public Shell getParent()
    {
        return this.parent;
    }

    /**
     * @return Shell
     */
    public Shell getShell()
    {
        if(this.shells.size() > 0)
        {
            return this.shells.get(this.shells.size() - 1);
        }

        return null;
    }

    /**
     * @throws IOException
     */
    @Override
    public void start() throws IOException
    {
        this.start(this);
    }

    /**
     * @param context
     * @throws IOException
     */
    @Override
    public void start(Object context) throws IOException
    {
        Shell shell;

        while((shell = this.getShell()) != null)
        {
            shell.start();

            this.shells.remove(shell);
        }

        super.start(context);

        if(this.getShell() != null)
        {
            this.start(context);
        }
    }

    /**
     * @param args
     * @return int
     * @throws IOException 
     */
    public int shell(String[] args) throws IOException
    {
        String name = String.valueOf("user" + (this.level + 1));

        if(args != null && args.length > 0 && args[0] != null)
        {
            Shell.start(new File(args[0]));
        }
        else
        {
            this.create(name, System.in);
        }

        super.exit(args);

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    public int name(String[] args)
    {
        System.out.println("Shell: " + this.getName());

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    @Override
    public int hello(String[] args)
    {
        System.out.println("I am Shell " + this.getName() + " !");

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    @Override
    public int exit(String[] args)
    {
        this.shells.remove(this);

        return 0;
    }

    public static void main(String[] args)
    {
    	try
        {
    		if(args != null && args.length > 0 && args[0] != null)
            {
    			Shell.start(new File(args[0]));
            }
            else
            {
                Shell.start(System.in);
            }
		}
        catch(IOException e)
		{
			e.printStackTrace();
		}
    }
}
