/*
 * $RCSfile: CommandLine.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-18  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * <p>Title: CommandLine</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class CommandLine
{
    private int state;
    private String name;
    private String prompt;

    private InputStream inputStream = null;
    private InputStreamReader inputStreamReader = null;
    private BufferedReader bufferedReader = null;

    public CommandLine()
    {
        this(null);
    }

    public CommandLine(InputStream inputStream)
    {
        this(null, inputStream);
    }

    public CommandLine(String name, InputStream inputStream)
    {
        this.name = name;
        this.prompt = "->$";

        if(inputStream == null)
        {
            this.inputStream = System.in;
        }
        else
        {
            this.inputStream = inputStream;
        }

        try
        {
            this.inputStreamReader = new java.io.InputStreamReader(this.inputStream);
            this.bufferedReader = new BufferedReader(this.inputStreamReader);
        }
        finally
        {
        }
    }

    /**
     * @param name
     * @param commandLine
     */
    public CommandLine(String name, CommandLine commandLine)
    {
        if(commandLine != null)
        {
            this.name = name;
            this.prompt = commandLine.prompt;

            this.inputStream = commandLine.inputStream;
            this.inputStreamReader = commandLine.inputStreamReader;
            this.bufferedReader = commandLine.bufferedReader;
        }
        else
        {
            this.name = name;
            this.prompt = "->$";
            this.setInputStream(System.in);
        }
    }

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param prompt
     */
    public void setPrompt(String prompt)
    {
        this.prompt = prompt;
    }

    /**
     * @return String
     */
    public String getPrompt()
    {
        return this.prompt;
    }

    /**
     * @param inputStream
     */
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;

        try
        {
            this.inputStreamReader = new java.io.InputStreamReader(this.inputStream);
            this.bufferedReader = new BufferedReader(this.inputStreamReader);
        }
        finally
        {
        }
    }

    /**
     * @return InputStream
     */
    public InputStream getInputStream()
    {
        return this.inputStream;
    }

    /**
     * @param bufferedReader
     */
    protected void setBufferedReader(BufferedReader bufferedReader)
    {
        this.bufferedReader = bufferedReader;
    }

    /**
     * @return BufferedReader
     */
    public BufferedReader getBufferedReader()
    {
        return this.bufferedReader;
    }

    /**
     * @param context
     * @throws IOException
     */
    public void start() throws IOException
    {
        this.start(this);
    }

    /**
     * @param context
     * @throws IOException
     */
    public void start(Object context) throws IOException
    {
        this.state = 0;

        String input = null;
        String command = null;

        while(this.state != -1)
        {
            System.out.print((this.prompt != null ? this.prompt : "->$"));

            input = this.readLine();

            if(input == null)
            {
                break;
            }

            command = input.trim();

            if(command.equals("/") || command.startsWith("/ "))
            {
            	this.exit(command);
            	this.state = -1;

                break;
            }
            else if(command.equals("quit") || command.startsWith("quit ") || command.equals("exit") || command.startsWith("exit "))
            {
                this.state = -1;
            	this.exit(command);

                break;
            }
            else
            {
                try
                {
                    this.process(context, input);
                    System.out.println();
                }
                catch(CommandNotFoundException e)
                {
                    System.out.println(e.getMessage());
                }
                catch(Throwable e)
                {
					e.printStackTrace();
				}
            }
        }

        System.out.println((this.getName() != null ? this.getName() : "cmd") + " bye !");
    }

    /**
     * @param input
     */
    protected Object process(String input) throws Throwable
    {
        return process(this, input);
    }

    /**
     * @param input
     * @throws Throwable 
     */
    protected Object process(Object context, String input) throws Throwable
    {
        if(context != this)
        {
            Method method = null;

            try
            {
                method = context.getClass().getMethod("process", new Class<?>[]{Object.class, String.class});
            }
            catch(SecurityException e)
            {
            }
            catch(NoSuchMethodException e)
            {
            }

            if(method != null)
            {
                try
                {
                    method.setAccessible(true);

                    return method.invoke(context, new Object[]{context, input});
                }
                catch(IllegalArgumentException e)
                {
                }
                catch(IllegalAccessException e)
                {
                }
                catch(InvocationTargetException e)
                {
                }

                return null;
            }
        }

        String[] args = Arguments.parse(input);

        if(args != null && args.length > 0)
        {
            try
            {
                return CommandLine.invoke(context, args);
            }
            catch(SecurityException e)
            {
            }
            catch(NoSuchMethodException e)
            {
                throw new CommandNotFoundException("Command \"" + args[0] + "\" not found !");
            }
            catch(IllegalArgumentException e)
            {
            }
            catch(IllegalAccessException e)
            {
            }
            catch(InvocationTargetException e)
            {
                throw e.getCause();
            }
        }

        return null;
    }

    /**
     * @param args
     * @return int
     */
    public int prompt(String[] args)
    {
        if(args.length > 0)
        {
            this.prompt = args[0];

            if(this.prompt.length() < 1)
            {
                this.prompt = "->$";
            }
        }

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    public int hello(String[] args)
    {
        if(args.length > 0 && args[0] != null && args[0].length() > 0)
        {
            System.out.println("Hello, " + args[0]);
        }
        else
        {
            System.out.println("Hello !");
        }

        return 0;
    }
    
    /**
     * @param args
     * @return int
     */
    public int dump(String[] args)
    {
        System.out.println(this.toString("dump", args, true));

        return 0;
    }

    /**
     * @param args
     * @param quote
     * @return int
     */
    public int dump(String[] args, boolean quote)
    {
        System.out.println(this.toString("dump", args, quote));

        return 0;
    }

    /**
     * @param cmd
     * @param args
     * @return int
     */
    public int dump(String cmd, String[] args)
    {
        System.out.println(this.toString(cmd, args, true));

        return 0;
    }
    
    /**
     * @param cmd
     * @param args
     * @param quote
     * @return int
     */
    public int dump(String cmd, String[] args, boolean quote)
    {
        System.out.println(this.toString(cmd, args, quote));

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    public int print(String[] args)
    {
        return this.print("", args, true);
    }
    
    /**
     * @param args
     * @param quote
     * @return int
     */
    public int print(String[] args, boolean quote)
    {
        return this.print("", args, quote);
    }

    /**
     * @param cmd
     * @param args
     * @param quote
     * @return int
     */
    public int print(String cmd, String[] args, boolean quote)
    {
        if(args != null && args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
            	if(quote)
            	{
            		System.out.print("\"");
            		System.out.print(args[i]);
            		System.out.print("\"");
            		System.out.println();
            	}
            	else
            	{
            		System.out.println(args[i]);
            	}
            }
        }

        return 0;
    }

    /**
     * @param args
     * @return int
     */
    public int sleep(String[] args)
    {
        if(args != null && args.length > 0)
        {
            long millis = getLong(args[0], 0L);

            if(millis > 0L)
            {
                try
                {
                    Thread.sleep(millis);
                }
                catch(InterruptedException e)
                {
                }
            }
        }
        
        System.out.println();

        return 0;
    }
    
    /**
     * @return int
     */
    public final int exit()
    {
        return this.exit(new String[0]);
    }
    
    /**
     * @param args
     * @return int
     */
    public final int exit(String input)
    {
    	String[] temp = null;
    	String[] args = Arguments.parse(input);

        if(args != null && args.length > 1)
        {
        	temp = new String[args.length - 1];

            if(temp.length > 0)
            {
                System.arraycopy(args, 1, temp, 0, temp.length);
            }
        }
        else
        {
        	temp = new String[0];
        }

        this.state = -1;

        return this.exit(temp);
    }

    /**
     * @param args
     * @return int
     */
    public int exit(String[] args)
    {
        this.state = -1;
        
        return 0;
    }

    /**
     * @param message
     * @return boolean
     */
    public boolean confirm(String message)
    {
        System.out.println(message);

        try
        {
            String input = this.readLine();

            if(input != null && (input = input.trim()).length() > 0)
            {
                return input.equalsIgnoreCase("y");
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param object
     * @param arguments
     * @return Object
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invoke(Object object, String arguments) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        String[] args = Arguments.parse(arguments);

        return CommandLine.invoke(object, args);
    }

    /**
     * @param object
     * @param args
     * @return Object
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invoke(Object object, String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if(args != null && args.length > 0)
        {
            String name = args[0];

            if(name != null && (name = name.trim()).length() > 0)
            {
                Class<?> type = object.getClass();

                Method method = type.getMethod(name, new Class<?>[]{String[].class});

                String[] tmp = new String[args.length - 1];

                if(tmp.length > 0)
                {
                    System.arraycopy(args, 1, tmp, 0, tmp.length);
                }

                method.setAccessible(true);

                Object value = method.invoke(object, new Object[]{tmp});

                return value;
            }
        }

        return null;
    }

    /**
     * @param s
     * @return Character
     */
    public static Character getCharacter(String s)
    {
        return getCharacter(s, null);
    }

    /**
     * @param s
     * @return Character
     */
    public static Character getCharacter(String s, Character defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                char c = s.charAt(0);

                return new Character(c);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Boolean
     */
    public static Boolean getBoolean(String s)
    {
        return getBoolean(s, null);
    }

    /**
     * @param s
     * @return Boolean
     */
    public static Boolean getBoolean(String s, Boolean defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                boolean b = (s != null) && (s.equals("true") || s.equals("on") || s.equals("1"));

                return new Boolean(b);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }


    /**
     * @param s
     * @return Byte
     */
    public static Byte getByte(String s)
    {
        return getByte(s, null);
    }

    /**
     * @param s
     * @return Byte
     */
    public static Byte getByte(String s, Byte defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                byte b = Byte.parseByte(s);

                return new Byte(b);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Short
     */
    public static Short getShort(String s)
    {
        return getShort(s, null);
    }

    /**
     * @param s
     * @return Short
     */
    public static Short getShort(String s, Short defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                short b = Short.parseShort(s);

                return new Short(b);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Integer
     */
    public static Integer getInteger(String s)
    {
        return getInteger(s, null);
    }

    /**
     * @param s
     * @return Integer
     */
    public static Integer getInteger(String s, Integer defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                int i = Integer.parseInt(s);

                return new Integer(i);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Float
     */
    public static Float getFloat(String s)
    {
        return getFloat(s, null);
    }

    /**
     * @param s
     * @return Float
     */
    public static Float getFloat(String s, Float defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                float i = Float.parseFloat(s);

                return new Float(i);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Double
     */
    public static Double getDouble(String s)
    {
        return getDouble(s, null);
    }

    /**
     * @param s
     * @return Double
     */
    public static Double getDouble(String s, Double defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                double i = Double.parseDouble(s);

                return new Double(i);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @param s
     * @return Long
     */
    public static Long getLong(String s)
    {
        return getLong(s, null);
    }

    /**
     * @param s
     * @return Long
     */
    public static Long getLong(String s, Long defaultValue)
    {
        if(s != null && s.length() > 0)
        {
            try
            {
                long i = Long.parseLong(s);

                return new Long(i);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return defaultValue;
    }

    /**
     * @return String
     * @throws IOException
     */
    public String readLine() throws IOException
    {
        return CommandLine.readLine(this.bufferedReader, "\r\n");
    }

    /**
     * @return String
     * @throws IOException
     */
    public static String readLine(InputStream inputStream) throws IOException
    {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try
        {
            inputStreamReader = new java.io.InputStreamReader(System.in);
            reader = new BufferedReader(inputStreamReader);

            return CommandLine.readLine(reader, "\r\n");
        }
        finally
        {
        }
    }

    /**
     * @param end
     * @return String
     * @throws IOException
     */
    public static String readLine(BufferedReader reader, String end) throws IOException
    {
        String str = null;
        StringBuilder buf = null;

        String line = System.getProperty("line.seperator");

        while((str = reader.readLine()) != null)
        {
            if(buf == null)
            {
                buf = new StringBuilder();
            }

            if(end.equals("\n") || end.equals("\r\n"))
            {
                buf.append(str);

                break;
            }
            else if(str.endsWith(end))
            {
                buf.append(str.substring(0, str.length() - end.length()));

                break;
            }
            else
            {
                buf.append(str).append(line);
            }
        }

        return (buf != null ? buf.toString() : null);
    }

    /**
     */
    public void close()
    {
        this.close(this.inputStream != System.in);
    }

    /**
     * @param enabled
     */
    public void close(boolean enabled)
    {
        if(enabled)
        {
            if(this.bufferedReader != null)
            {
                try
                {
                    this.bufferedReader.close();
                    this.bufferedReader = null;
                }
                catch(IOException e)
                {
                }
            }

            if(this.inputStreamReader != null)
            {
                try
                {
                    this.inputStreamReader.close();
                    this.inputStreamReader = null;
                }
                catch(IOException e)
                {
                }
            }
        }
    }
    
    /**
     * @param args
     * @return
     */
    public String toString(String[] args)
    {
    	return this.toString("", args, true);
    }
    
    /**
     * @param args
     * @param quote
     * @return String
     */
    public String toString(String[] args, boolean quote)
    {
    	return this.toString("", args, quote);
    }

    /**
     * @param cmd
     * @param args
     * @param quote
     * @return String
     */
    public String toString(String cmd, String[] args, boolean quote)
    {
        StringBuilder buf = new StringBuilder();
        
        buf.append(cmd);

        if(args != null && args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
            	if(quote)
            	{
            		buf.append("\"");
            	}
            	
                for(int j = 0; j < args[i].length(); j++)
                {
                    char c = args[i].charAt(j);

                    switch(c)
                    {
                        case '\n':
                        {
                            buf.append("\\n");
                            break;
                        }
                        case '\t':
                        {
                            buf.append("\\t");
                            break;
                        }
                        case '\b':
                        {
                            buf.append("\\b");
                            break;
                        }
                        case '\r':
                        {
                            buf.append("\\r");
                            break;
                        }
                        case '\f':
                        {
                            buf.append("\\f");
                            break;
                        }
                        case '\'':
                        {
                            buf.append("\'");
                            break;
                        }
                        case '\"':
                        {
                            buf.append("\\\"");
                            break;
                        }
                        case '\\':
                        {
                            buf.append("\\\\");
                            break;
                        }
                        default:
                        {
                            buf.append(c);
                        }
                    }
                }

                if(quote)
                {
                	buf.append("\" ");
                }
                else
                {
                	buf.append(" ");
                }
            }

         	buf.deleteCharAt(buf.length() - 1);
        }

        return buf.toString();
    }

    /**
     */
    static class CommandNotFoundException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public CommandNotFoundException(String message)
        {
            super(message);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }
}
