package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import javax.swing.JApplet;
import model.CurrentModel;

public class CommunicationThread extends Thread
{
	private static JApplet application;
	
	private static URL codebase;
	private static Socket commandSocket;
	private static Socket objectSocket;
    private static String host;
    private static InetAddress address;
    
    private static int commandPort = 65000;
    private static int objectPort = 65001;
    
    private static PrintWriter outToServer;
    private static BufferedReader inFromServer;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;	
	
	public CommunicationThread(JApplet application) 
    {
		super("AppletCommunicationThread");
		initializeNetworkConnection(application);
		requestModel();
    }
	
	public void run() 
	{
//		//Start automatic update
//  		int delay = 100; //milliseconds
//  		ActionListener taskPerformer = new ActionListener() 
//  		{
//  			@Override
//  			public void actionPerformed(ActionEvent e) 
//  			{
//  				//Request model and notify canvas to repaint
//  				requestModel();
//  				AppletController.getCanvas().currentModelChanged();
//  			}
//  		};
//
//  		new Timer(delay, taskPerformer).start();
	}
		
	public void initializeNetworkConnection(JApplet app)
	{ 
		try 
		{
			System.out.println("Initializing network connection...");

			//Getting this reference allows calls to the .getCodebase() and .getHost() outside this function
			application = app;

			//Grab codebase and host.
			//This finds the codebase and host name that the application was loaded from (ie. the server)
			codebase = application.getCodeBase();
			host = application.getCodeBase().getHost();
			System.out.println("Codebase: " + codebase);
			System.out.println("Host: " + host);

			//Get IP address of host server
			address = InetAddress.getByName(host);
			System.out.println("Address: " + address);


			//TCP Socket Setup
			//http://lycog.com/java/tcp-object-transmission-java/

			//socket = new Socket(host, serverPort);
			commandSocket = new Socket(InetAddress.getByName(host), commandPort);
			objectSocket = new Socket(InetAddress.getByName(host), objectPort);

			//Creates readers and writers on the socket's input and output streams
			//Command stream
			outToServer = new PrintWriter(commandSocket.getOutputStream(), true);
			inFromServer = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));

			//Object stream
			oos = new ObjectOutputStream(objectSocket.getOutputStream());
			ois = new ObjectInputStream(objectSocket.getInputStream());

			System.out.println("Network connection initialized!");

		} catch (Exception e1) {
			System.out.println("Failure initializing TCP socket and streams!");
			//e1.printStackTrace();
		}
	}
 
	
	public static void sendModel()
	{
		//Ensure streams have been initialized
		if(outToServer == null || inFromServer == null || oos == null )
		{
			System.out.println("Command or object stream not initialized, server may be off...");
			return;
		}
		
		//Submit model to Pi
		try 
		{
			System.out.println("Sending model to server...");
			
			//Notify Pi on command stream
			outToServer.println("SENDINGMODEL");
			
			//Analyze command stream response
			if(inFromServer.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				oos.reset();
				
				//Write object to object stream
				oos.writeObject(AppletController.getCM());

				//Analyze command stream response
				if(inFromServer.readLine().equals("OKAY"))
				{
					System.out.println("Model save complete.");
				}
				else
				{
					System.out.println("Model did not save correctly.");
				}
			}
		} 
		catch (Exception e1) 
		{
			outToServer.println("FAIL");
			System.out.println("Failure sending model to server");
			//e1.printStackTrace();
		}
	}
	
	
	public static void requestModel()
	{
		//Ensure streams have been initialized
		if(outToServer == null || inFromServer == null || ois == null )
		{
			System.out.println("Command or object stream not initialized, server may be off...");
			return;
		}
		
		//Get model from Pi
		try {
			System.out.println("Requesting model...");
			
			//Request model from Pi on command stream
			outToServer.println("REQUESTMODEL");
				
			//Analyze response on command stream
			if(inFromServer.readLine().equals("SENDINGMODEL"))
			{
				System.out.println("Model incoming...");
						
				//Read model off object stream
				AppletController.setCM((CurrentModel) ois.readObject());
				
				//Notify server result
				outToServer.println("OKAY");
				
				System.out.println("Model recieved.");
			}
			
		} catch (Exception e1) {
			outToServer.println("FAIL");
			System.out.println("Failure requesting model from server!");
			//e1.printStackTrace();
		}
	} 
	
	public static void closeStreams()
	{
		/*
    	http://docs.oracle.com/javase/7/docs/api/java/applet/Applet.html#destroy()
    	public void destroy()
    	Called by the browser or applet viewer to inform this applet that it is being reclaimed and that it should destroy any resources that it has allocated. The stop method will always be called before destroy.
    	A subclass of Applet should override this method if it has any operation that it wants to perform before it is destroyed. For example, an applet with threads would use the init method to create the threads and the destroy method to kill them.

    	The implementation of this method provided by the Applet class does nothing.

    	See Also:
    	init(), start(), stop()
    	*/
    	
    	//Close streams and sockets opened by applet, MUST BE DONE, ie default() does nothing if not defined!
		
		System.out.println("Closing applet streams and socket...");
    	
    	try 
    	{
 			if(ois != null)
			{ois.close();}
    	} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing OIS (Object Input Stream)");
			e.printStackTrace();
		}   
				
    	try
    	{
    		if(oos != null)
			{oos.close();}
    	}
    	catch (Exception e) 
    	{
			System.out.println("Failure closing OOS (Object Output Stream)");
			e.printStackTrace();
		}
				
    	try
    	{
			if(outToServer != null)
			{outToServer.close();}
    	}
    	catch (Exception e) 
	    	{
				System.out.println("Failure closing OutToServer (Print Writer)");
				e.printStackTrace();
			}
				
    	try
    	{
			if(inFromServer != null)
			{inFromServer.close();}
    	}
    	catch (Exception e) 
    	{
			System.out.println("Failure closing InFromServer (Buffered Reader)");
			e.printStackTrace();
		}
    	
    	try
    	{
			if(commandSocket != null)
			{commandSocket.close();}
			System.out.println("Command socket closed.");
		} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing command socket!");
			e.printStackTrace();
		}    	
    	
    	try
    	{
			if(objectSocket != null)
			{objectSocket.close();}
			System.out.println("Object socket closed.");
		} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing object socket!");
			e.printStackTrace();
		}    	
	}
	
	

	//NETWORKING PARAMETERS*******************************************************
	
	//Reference to application
	public static JApplet getApplication() {
		return application;
	}
	
	//Codebase
	public static URL getCodebase() {
		return codebase;
	}

	//Command port number
	public static int getCommandPort() {
		return commandPort;
	}
	
	//Object port number
	public static int getObjectPort() {
		return objectPort;
	}

	//Command socket
	public static Socket getCommandSocket() {
		return commandSocket;
	}
	
	//Object socket
	public static Socket getObjectSocket() {
		return objectSocket;
	}

	//Host name
	public static String getHost() {
		return host;
	}

	//Internet address
	public static InetAddress getAddress() {
		return address;
	}
		
	//Input/output TCP Streams
	//Command stream out to server
	public static PrintWriter getOutToServer() {
		return outToServer;
	}

	//Command stream in from server
	public static BufferedReader getInFromServer() {
		return inFromServer;
	}
	
	//Object stream out to server
	public static ObjectOutputStream getOOS() {
		return oos;
	}

	//Object stream in from server
	public static ObjectInputStream getOIS() {
		return ois;
	}

}
