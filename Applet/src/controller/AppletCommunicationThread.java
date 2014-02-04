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

import model.CanvasObject;
import model.CurrentModel;

public class AppletCommunicationThread extends Thread
{
	private static JApplet application;
	
	private static URL codebase;
    private static String host;
    private static InetAddress address;
    	
	private static Socket objectSocket;
	private static Socket activeSocket;
	private static Socket passiveSocket;
	
	//Server
	//objectPort = 65000
	//activePort = 65001
	//passivePort = 65002

	//Applet
	//objectPort = 65000
	//activePort = 65002
	//passivePort = 65001
	
	//Active stream of one must be paired to the passive stream of the other to avoid readLine() collisions
	
    private static int objectPort = 65000;
    private static int activePort = 65002;
    private static int passivePort = 65001;
    
    private static ObjectOutputStream objectStreamOut;
    private static ObjectInputStream objectStreamIn;

    private static PrintWriter activeStreamOut;
    private static BufferedReader activeStreamIn;
    
    private static PrintWriter passiveStreamOut;
    private static BufferedReader passiveStreamIn;	
    
    private static boolean connected;
	
	public AppletCommunicationThread(JApplet application) 
    {
		super("AppletCommunicationThread");
		
		//Create connection to server
		initializeConnection(application);
    }
	
	public void initializeConnection(JApplet app)
	{ 
		try 
		{
			connected = false;
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
			objectSocket = new Socket(InetAddress.getByName(host), objectPort);
			activeSocket = new Socket(InetAddress.getByName(host), activePort);
			passiveSocket = new Socket(InetAddress.getByName(host), passivePort);
			
			//Initialize the object streams
			objectStreamOut = new ObjectOutputStream(objectSocket.getOutputStream());
			objectStreamIn = new ObjectInputStream(objectSocket.getInputStream());

			//Initialize the active stream writer and reader
			activeStreamOut = new PrintWriter(activeSocket.getOutputStream(), true);
			activeStreamIn = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));

			//Initialize the passive stream writer and reader
			passiveStreamOut = new PrintWriter(passiveSocket.getOutputStream(), true);
			passiveStreamIn = new BufferedReader(new InputStreamReader(passiveSocket.getInputStream()));

			connected = true;
			System.out.println("Network connection initialized!");

		} 
		catch (Exception e) 
		{
			System.err.println("Failure initializing TCP socket and streams!");
			//e.printStackTrace();
		}
	}
	
	public void run() 
	{
		//Ensure sockets are initialized
        if (objectSocket == null || activeSocket == null || passiveSocket == null || !connected)
        {
        	System.err.println("One or more sockets not created, server may be down or off.");
            return;
        }
        
        //Ensure streams are initialized
        if(objectStreamIn == null || objectStreamOut == null || 
        		activeStreamIn == null || activeStreamOut == null ||
        		passiveStreamIn == null || passiveStreamOut == null || !connected)
		{
			System.err.println("One or more streams not initialized, server may be down or off.");
			return;
		}
                
        //Infinite loop
        while (true) 
        {
        	// Receive request on passive stream
        	String received = null;
			try 
			{
				received = passiveStreamIn.readLine();
				
				if(received == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.err.println("Server closed socket, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + received + "|");
				
				//Process command
				switch(received)
		        {		        												
		        	case "SENDINGMODEL":						recieveModel();
		        												break;
      	
		        	default:									System.out.println("DEFAULT BEHAVIOUR");
		        												break;
		        }
			}
			catch (Exception e2)
			{
				//Socket error, close all streams and sockets
				closeStreams();
				
				//Wait to let applet close as not to re-initializeConnection too soon
				try 
				{
					Thread.sleep(1000);
				} catch (Exception e) {
					System.out.println("Failure in waiting to re-connect");
					e.printStackTrace();
				}
					
				//Initialize the next connection
				initializeConnection(application);
			}
        }
	}	
	
	public static void addObject(CanvasObject object)
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected)
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send model to server
		try 
		{
			System.out.println("Sending model to server...");
			
			//Notify server on command stream
			activeStreamOut.println("ADDOBJECT");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(object);

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Object sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Object did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending object to baseStation");
			//e1.printStackTrace();
		}
	}
	
	public static void removeObject(CanvasObject object)
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected)
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send model to server
		try 
		{
			System.out.println("Sending model to server...");
			
			//Notify server on command stream
			activeStreamOut.println("REMOVEOBJECT");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(object);

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Object sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Object did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending object to baseStation");
			//e1.printStackTrace();
		}
	}
	
	public static void recieveModel()
	{
		//Ensure streams have been initialized
		if(passiveStreamOut == null || passiveStreamIn == null || objectStreamIn == null || !connected)
		{
			System.err.println("Command or object stream not initialized, server may be off...");
			return;
		}
		
		//Get model from server
		try 
		{
			//Notify applet ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving model...");
									
			//Read model off object stream
			AppletController.setCM((CurrentModel) objectStreamIn.readObject());
				
			//Notify applet result
			passiveStreamOut.println("OKAY");
				
			System.out.println("Model recieved.");
		} 
		catch (Exception e1) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure requesting model from client!");
			//e1.printStackTrace();
		}
	} 
	
	public static void closeStreams()
    {
    	//Socket error, close all streams and sockets
		
		System.out.println("Closing existing streams and sockets...");

		//CLOSE STREAMS********************************************
		
		//Close object streams
		try 
		{
			if(objectStreamIn != null)
			{objectStreamIn.close();}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamIn (object input stream).");
			//e.printStackTrace();
		}
		
		try
		{
			if(objectStreamOut != null)
			{objectStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamOut (object output stream).");
			//e.printStackTrace();
		}
		
		
		//Close active streams
		try
		{
			if(activeStreamOut != null)
			{activeStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(activeStreamIn != null)
			{activeStreamIn.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		
		//Close passive streams
		try
		{
			if(passiveStreamOut != null)
			{passiveStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveStreamIn != null)
			{passiveStreamIn.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		
		//CLOSE SOCKETS****************************************
		//Close object socket
		try
		{
			if(objectSocket != null)
			{objectSocket.close();}
		} 
		catch (Exception e) 
		{
			System.err.println("Failure closing objectSocket.");
			//e.printStackTrace();
		}
		
		//Close active socket
		try
		{
			if(activeSocket != null)
			{activeSocket.close();}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing activeSocket.");
			//e.printStackTrace();
		}
		
		//Close passive socket
		try
		{
			if(passiveSocket != null)
			{passiveSocket.close();}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing passiveSocket.");
			//e.printStackTrace();
		}
		
		System.out.println("Existing streams and sockets closed.");
    }
	
	

//NETWORKING PARAMETERS*******************************************************
  	
	//Reference to application
	public static JApplet getApplication() {
		return application;
	}
	
	//Get codebase
	public static URL getCodebase() {
		return codebase;
	}
	
	//Connected status
    public static boolean isConnected() {
    	return connected;
    }
	
	
  	//PORT NUMBERS**************************************************
    
    //Object port number
	public static int getObjectPort() {
		return objectPort;
	}
	
	//Active port number
	public static int getActivePort() {
		return activePort;
	}
		
	//Passive port number
	public static int getPassivePort() {
		return passivePort;
	}
   
  	
  	//SOCKETS*****************************************************
  	
  	//Object socket
  	public static Socket getObjectSocket() {
  		return objectSocket;
  	}  	
  	
  	//Active socket
  	public static Socket getActiveSocket() {
  		return activeSocket;
  	}
  	
  	//Passive socket
  	public static Socket getPassiveSocket() {
  		return passiveSocket;
  	}
  	
  	
  	//TCP STREAMS*************************************************
  	
  	//Object stream to client
  	public static ObjectOutputStream getObjectStreamOut() {
  		return objectStreamOut;
  	}

  	//Object stream from client
  	public static ObjectInputStream getObjectStreamIn() {
  		return objectStreamIn;
  	}
  	
  	//Active stream out
  	public static PrintWriter getActiveStreamOut() {
  		return activeStreamOut;
  	}

  	//Active stream in
  	public static BufferedReader getActiveStreamIn() {
  		return activeStreamIn;
  	}
  	
  	//Passive stream out
  	public static PrintWriter getPassiveStreamOut() {
  		return passiveStreamOut;
  	}

  	//Passive steam in
  	public static BufferedReader getPassiveStreamIn() {
  		return passiveStreamIn;
  	}
}