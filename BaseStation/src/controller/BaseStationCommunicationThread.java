package controller;
/*
 * Extended from Oracle's Network Client Applet Example to ensure network operation
 * Written by: Brian Grosskleg
 * Date: Jan. 4 2014
 */ 

/**
 * Java(TM) SE 6 version.
 */

//Must omit package in order to compile nicely on RPI command line

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import model.CanvasObject;

public class BaseStationCommunicationThread extends Thread 
{   	
	private static ServerSocket objectServerSocket;
	private static ServerSocket activeServerSocket;
	private static ServerSocket passiveServerSocket;
	
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
    private static int activePort = 65001;
    private static int passivePort = 65002;
    
    private static ObjectOutputStream objectStreamOut;
    private static ObjectInputStream objectStreamIn;

    private static PrintWriter activeStreamOut;
    private static BufferedReader activeStreamIn;
    
    private static PrintWriter passiveStreamOut;
    private static BufferedReader passiveStreamIn;
    
    private static boolean connected;
	
    public BaseStationCommunicationThread() 
    {
        super("ServerCommunicationThread");
        
        //Create server socket and wait for applet connection
        initializeConnection();
    }
    
	public static void initializeConnection()
	{
		try 
		{	
			connected = false;
			if(objectServerSocket == null)
			{objectServerSocket = new ServerSocket(objectPort);}
			if(activeServerSocket == null)
			{activeServerSocket = new ServerSocket(activePort);}		
			if(passiveServerSocket == null)
			{passiveServerSocket = new ServerSocket(passivePort);}
			
			System.out.println("Server object stream listening on port: " + objectPort);
			System.out.println("Server active stream listening on port: " + activePort);
			System.out.println("Server passive stream listening on port: " + passivePort);

			//Wait for applet connection
			System.out.println("Initializing socket...");
			System.out.println("Waiting for request from applet...");   	

			//.accept() sits and waits for connection request on serverSocket then returns 
			//the requesting socket to establish connection
			objectSocket = objectServerSocket.accept();
			activeSocket = activeServerSocket.accept();
			passiveSocket = passiveServerSocket.accept();

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
			System.out.println("Initializing sockets complete.");
			
		} 
		catch (Exception e) 
		{
			System.err.println("Could not initialize socket.");
			//e.printStackTrace();
		}   
	}
	 
	
    public void run() 
    {
    	//Ensure sockets are initialized
        if (objectSocket == null || activeSocket == null || passiveSocket == null || !connected)
        {
        	System.err.println("One or more sockets not created...");
            return;
        }
        
        //Ensure streams are initialized
        if(objectStreamIn == null || objectStreamOut == null || 
        		activeStreamIn == null || activeStreamOut == null ||
        		passiveStreamIn == null || passiveStreamOut == null || !connected)
		{
			System.err.println("One or more streams not initialized...");
			return;
		}
        
        //Infinite loop, receive requests on passive stream
        while (true) 
        {
        	// Receive request on passive stream
        	String recieved = null;
			try 
			{		
				recieved = passiveStreamIn.readLine();
				
				if(recieved == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.err.println("Client closed socket, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + recieved + "|");
				
				//Process command
	        	switch(recieved)
		        {		        												
		        	case "SENDINGMODEL":						//recieveModel();
		        												//BaseStationController.saveModeltoFile();
		        												break;
      	
		        	case "ADDOBJECT":							BaseStationController.getCM().addCanvasObject(recieveObject());
		        												break;
		        	
		        	case "REMOVEOBJECT":						BaseStationController.getCM().removeCanvasObject(recieveObject());
		        												break;
		        												
		        	default:									System.out.println("DEFAULT BEHAVIOUR");
		        												break;
		
		        }
			} 
			catch (Exception e)
			{				
				//Socket error, close all streams and sockets
				closeStreams();
				
				//Wait to let server close as not to re-initializeConnection too soon
				try 
				{
					Thread.sleep(1000);
				} catch (Exception e1) {
					System.out.println("Failure in waiting to re-connect");
					e1.printStackTrace();
				}
				
				//Initialize the next connection
				initializeConnection();
			}
        }	
    }
    
    //MODEL TRANSMISSION*****************************************************************
    
    public static void sendModel()
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected )
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send model to applet
		try 
		{
			System.out.println("Sending model to client...");
			
			//Notify applet on command stream
			activeStreamOut.println("SENDINGMODEL");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(BaseStationController.getCM());

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Model sent to client successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Model did not send successfully.");
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
			System.err.println("Failure sending model to client");
			//e1.printStackTrace();
		}
	}
    
    public static CanvasObject recieveObject()
	{
		//Ensure streams have been initialized
		if(passiveStreamOut == null || passiveStreamIn == null || objectStreamIn == null || !connected)
		{
			System.err.println("Command or object stream not initialized, server may be off...");
			return null;
		}
		
		//Get model from applet
		try 
		{
			//Notify applet ready
			passiveStreamOut.println("OKAY");
			
			System.out.println("Recieving object...");
									
			//Read model off object stream
			CanvasObject received = (CanvasObject) objectStreamIn.readObject();
				
			//Notify applet result
			passiveStreamOut.println("OKAY");
				
			System.out.println("Object recieved.");
			
			return received;
			
		} 
		catch (Exception e1) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure requesting model from client!");
			//e1.printStackTrace();
			return null;
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
			{
				objectStreamIn.close();
				objectStreamIn = null;
			}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamIn (object input stream).");
			//e.printStackTrace();
		}
		
		try
		{
			if(objectStreamOut != null)
			{
				objectStreamOut.close();
				objectStreamOut = null;
			}
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
			{
				activeStreamOut.close();
				activeStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(activeStreamIn != null)
			{
				activeStreamIn.close();
				activeStreamIn = null;
			}
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
			{
				passiveStreamOut.close();
				passiveStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveStreamIn != null)
			{
				passiveStreamIn.close();
				passiveStreamIn = null;
			}
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
			{
				objectSocket.close();
				objectSocket = null;
			}	
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
			{
				activeSocket.close();
				activeSocket = null;
			}
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
			{
				passiveSocket.close();
				passiveSocket = null;
			}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing passiveSocket.");
			//e.printStackTrace();
		}
		
		System.out.println("Existing streams and sockets closed.");
    }
    
    
    //NETWORKING PARAMETERS*******************************************************
  	
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
      
  	
  	//SERVER SOCKETS************************************************
  	
  	//Object serverSocket
  	public static ServerSocket getObjectServerSocket() {
  	  	return objectServerSocket;
  	}
  	
  	//Active serverSocket
  	public static ServerSocket getActiveServerSocket() {
  		return activeServerSocket;
  	}
  	
  	//Passive serverSocket
  	public static ServerSocket getPassiveServerSocket() {
  		return passiveServerSocket;
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

		
