/*
 * $RCSfile: Telnet.java,v $
 * $Revision: 1.1  $
 * $Date: 2010-7-23  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * <p>Title: Telnet</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public class Telnet extends CommandLine
{
	private String host;
	private int port;
	
	private int state = 0;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private char[] buffer = new char[8192];
	
	/**
	 * @author xuesong.net
	 */
	public Telnet()
	{
	}
	
	/**
	 * @param host
	 * @param port
	 * @author xuesong.net
	 */
	public Telnet(String host, int port)
	{
		this.host = host;
		this.port = port;
	}
	
	public String getHost()
	{
		return this.host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public Socket getSocket()
	{
		return socket;
	}

	/**
	 * @param socket
	 */
	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}

	/**
	 * @return
	 */
	public InputStream getInputStream()
	{
		return inputStream;
	}

	/**
	 * @param inputStream
	 */
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	/**
	 * 
	 * @return
	 */
	public OutputStream getOutputStream()
	{
		return outputStream;
	}

	/**
	 * @param outputStream
	 */
	public void setOutputStream(OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}

	/**
	 * @param args
	 * @return int
	 */
	public static Telnet telnet(String[] args)
	{
		if(args != null && args.length > 0)
		{
			String host = args[0];
			int port = 23;
			
			if(args.length > 1)
			{
				try
				{
					port = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
				}
			}

			Telnet telnet = new Telnet(host, port);
			
			telnet.connect();
			
			return telnet;
		}

		return null;
	}

	/**
	 * @param host
	 * @param port
	 * @return int
	 */
	public int connect()
	{
		try
		{
			this.close();
			this.socket = new Socket(this.host, this.port);
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();

			this.socket.setKeepAlive(true);

			this.state = 1;
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}

		return state;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @author xuesong.net
	 */
	public void send(String[] args) throws IOException
	{
		try
		{
		    if(this.state == 0 || this.socket == null || this.socket.isClosed())
	        {
	            throw new SocketException("connect is closed !");
	        }

	        String data = this.toString(args, false);
	        
    		this.outputStream.write(data.getBytes("UTF-8"));
    		this.outputStream.write("\r\n\r\n".getBytes("UTF-8"));
    		this.outputStream.flush();
    		
    		this.read();
		}
		catch(IOException e)
		{
		    System.out.println(e.getMessage());

		    this.exit();
		}
	}
	
	/**
	 * @param cmd
	 * @param args
	 * @throws IOException 
	 */
	public void send(String cmd, String[] args) throws IOException
	{
		if(this.state == 0 || this.socket == null || this.socket.isClosed())
		{
			throw new SocketException("connect is closed !");
		}

		String data = this.toString(cmd, args, false);

		this.outputStream.write(data.getBytes("UTF-8"));
		this.outputStream.flush();

		this.read();
	}

	/**
	 * @throws IOException
	 */
	public void read() throws IOException
	{
		if(this.state == 0 || this.socket == null || this.socket.isClosed())
		{
			throw new SocketException("connect is closed !");
		}

		int length = 0;
		this.socket.setKeepAlive(true);
		this.socket.setSoTimeout(1000);
		InputStreamReader reader = new InputStreamReader(this.inputStream);

		try
		{
			while((length = reader.read(buffer, 0, 8192)) > -1)
			{
				System.out.println(new String(buffer, 0, length));
			}

			length = 0;
		}
		catch(SocketTimeoutException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		if(length > 0)
		{
			System.out.println(new String(buffer, 0, length));
		}

		System.out.println("IsClosed: " + this.socket.isClosed());

		if(this.socket.isClosed())
		{
		    System.out.println("connect is closed !");
		    
		    this.exit();
		}
	}
	
	@Override
	public Object process(Object context, String command) throws Throwable
    {
		if(command == null || command.trim().length() < 1)
		{
			return null;
		}

        String[] args = Arguments.parse(command);

        try
        {
			this.send(args);
		}
        catch(IOException e)
        {
			e.printStackTrace();
		}

        return null;
    }
	
	/**
	 * @author xuesong.net
	 */
	@Override
	public int exit(String[] args)
	{
		this.close();
        super.exit(args);

		return 0;
	}

	/**
	 * @author xuesong.net
	 */
	public void close()
	{
	    this.state = 0;
	    
		if(this.inputStream != null)
		{
			try
			{
				this.inputStream.close();
			}
			catch(IOException e)
			{
			}
		}

		if(this.outputStream != null)
		{
			try
			{
				this.outputStream.close();
			}
			catch(IOException e)
			{
			}
		}
		
		if(this.socket != null)
		{
			if(this.socket.isClosed() == false)
			{
				try
				{
					this.outputStream.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}

	/**
	 * @author xuesong.nets
	 */
	protected static void useage()
	{
		System.out.println("Usage:");
		System.out.println("    telnet HOST [PORT]");
		System.out.println();
	}

	public static void main(String[] args)
	{
		CommandLine cmd = new CommandLine()
		{
			private Telnet telnet;
			
			/**
			 * @param args
			 * @return int
			 */
			@SuppressWarnings("unused")
			public int test(String[] args)
			{
				return this.telnet(new String[]{"localhost", "7272"});
			}

			/**
			 * @param args
			 * @return int
			 */
			public int telnet(String[] args)
			{
				if(args != null && args.length > 1)
				{
					this.telnet = Telnet.telnet(args);
				}
				
				if(this.telnet != null)
				{
					try
					{
						this.telnet.setPrompt("telnet$");
						this.telnet.start();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					Telnet.useage();
				}

				return 0;
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
