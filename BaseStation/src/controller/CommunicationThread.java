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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.Timer;

import model.CurrentModel;

public class CommunicationThread extends Thread 
{   	
	
	private static ServerSocket commandServerSocket;
	private static ServerSocket objectServerSocket;
	
	private static Socket commandSocket;
	private static Socket objectSocket;
	
    private static int commandPort = 65000;
    private static int objectPort = 65001;

    private static PrintWriter outToClient;
    private static BufferedReader inFromClient;
    
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    
    private static String modelPath;
	
	public static void initialize()
	    {
		    try 
		    {	    	
		    	if(commandServerSocket == null)
		    	{commandServerSocket = new ServerSocket(commandPort);}
		    	if(objectServerSocket == null)
		    	{objectServerSocket = new ServerSocket(objectPort);}
		    	
		        System.out.println("Server command stream listening on port: " + commandPort);
		        System.out.println("Server object stream listening on port: " + objectPort);
		        
		        //Wait for applet connection
		    	System.out.println("Initializing socket...");
		    	System.out.println("Waiting for request from applet...");   	
		       
		    	//.accept() sits and waits for connection request on serverSocket then returns 
		    	//the requesting socket to establish connection
		    	commandSocket = commandServerSocket.accept();
		    	objectSocket = objectServerSocket.accept();
		        
		    	//If being run locally by eclipse, load the model from local path
		    	//If being run on webserver on Pi, load model from the /var/www path
		        if(commandSocket.getLocalAddress().toString().equals("/127.0.0.1"))
		        {
		        	System.out.println("modelPath = C:/Users/Brian Grosskleg/Desktop/model.ser");
		        	modelPath = "C:/Users/Brian Grosskleg/Desktop/model.ser";
		        }
		        else if(commandSocket.getLocalAddress().toString().equals("/172.16.1.85"))
		        {
		        	System.out.println("modelPath = var/www/model.ser");
		        	modelPath = "/var/www/model.ser";
		        }
		        else
		        {
		        	System.err.println("Could not set model path.");
		        	throw new Exception();
		        }
		        BaseStationController.setCM(loadModelfromFile());
		        
		        //Initialize the command stream writer and reader
		        outToClient = new PrintWriter(commandSocket.getOutputStream(), true);
		        inFromClient = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
		        
		        //Initialize the object stream
		        oos = new ObjectOutputStream(objectSocket.getOutputStream());
		        ois = new ObjectInputStream(objectSocket.getInputStream());
		        
		        System.out.println("Initializing socket complete.");
		    } 
		    catch (Exception e) 
		    {
		        System.err.println("Could not initialize socket.");
		    }   
	    }
	 
	
	
    public CommunicationThread() 
    {
        super("ServerCommunicationThread");
        
        //Create server socket and wait for applet connection
        initialize();
        
        
        //Creates a timer to save model to file every 60 seconds
        int delay = 60000; //milliseconds

        ActionListener taskPerformer = new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{
        		//Save CurrentModel to RPi card
        		saveModeltoDisk();
        	}
        };
        new Timer(delay, taskPerformer).start();     
    }
    
    

    public void run() 
    {
    	//Ensure sockets are initialized
        if (commandSocket == null || objectSocket == null)
        {
        	System.err.println("Sockets not created...");
            return;
        }
        
        //Ensure streams are initialized
        if(inFromClient == null)
		{
			System.out.println("Input stream not initialized...");
			return;
		}
                
        //Infinite loop
        while (true) 
        {
        	// Receive request on command stream
        	String received = null;
			try {
								
				received = inFromClient.readLine();
				
				if(received == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.out.println("Client closed socket, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + received + "|");
				
				//Process command
	        	switch(received)
		        	{
		        	case "REQUESTMODEL":					try {
																	System.out.println("Sending model to client...");
																	
																	//Respond to client
																	outToClient.println("SENDINGMODEL");
																	
																	//Reset object stream and write model to stream
																	oos.reset();
																	oos.writeObject(BaseStationController.getCM());
																	
																	//Analyze response
																	if(inFromClient.readLine().equals("OKAY"))
																	{
																		System.out.println("Model sent complete.");
																	}
																	else if(inFromClient.readLine().equals("FAIL"))
																	{
																		System.out.println("Model did not send correctly.");
																	}
																	else
																	{
																		System.err.println("Impossible response: " + inFromClient.readLine());
																	}
																	
																} catch (Exception e) {
																	System.out.println("Failure sending model to client!");
																	e.printStackTrace();
																}
		        	
		        												break;
		        												
		
		        	case "SENDINGMODEL":					try {
																	System.out.println("Receiving model...");
																	
																	//Send response to client
																	outToClient.println("OKAY");
																	
																	//Read object off object stream
																	BaseStationController.setCM((CurrentModel) ois.readObject());
																	
																	//Send response to client
																	outToClient.println("OKAY");
													    			System.out.println("Model recieved.");
													    			
			        												//Save CurrentModel to RPi card
			        								        		saveModeltoDisk();
			        								        		
																} catch (Exception e) {
																	outToClient.println("FAIL");
																	System.out.println("Failure getting model from client!");
																	e.printStackTrace();
																} 
		        												break;
		        	
		        	default:	
		        												break;
		
		        	}
			} 
			catch (Exception e2)
			{
				//Socket error, close all streams and sockets
				
				System.out.println("Socket Exception, closing existing socket...");

				try 
				{
					if(ois != null)
					{ois.close();}
				}
				
				catch(Exception e)
				{
					System.out.println("Failure closing OIS (object input stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(oos != null)
					{oos.close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OOS (object output stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(outToClient != null)
					{outToClient.close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OutToClient (print writer).");
					e.printStackTrace();
				}
				
				try
				{
					if(inFromClient != null)
					{inFromClient.close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing InFromClient (buffered reader).");
					e.printStackTrace();
				}
				
				try
				{
					if(commandSocket != null)
					{commandSocket.close();}
					System.out.println("Existing commandSocket closed.");
				}
					catch (Exception e) 
					{
						System.out.println("Failure closing commandSocket.");
						e.printStackTrace();
					}
					
				try
				{
					if(objectSocket != null)
					{objectSocket.close();}
					System.out.println("Existing objectSocket closed.");	
				} 
				catch (Exception e) 
				{
					System.out.println("Failure closing objectSocket.");
					e.printStackTrace();
				}
			
				//Initialize the next connection
				initialize();
			}
			
			
        }
    }

    public static void sendModel()
	{
		//Ensure streams have been initialized
		if(outToClient == null || inFromClient == null || oos == null )
		{
			System.out.println("Command or object stream not initialized, server may be off...");
			return;
		}
		
		//Submit model to Pi
		try 
		{
			System.out.println("Sending model to server...");
			
			//Notify Pi on command stream
			outToClient.println("SENDINGMODEL");
			
			//Analyze command stream response
			if(inFromClient.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				oos.reset();
				
				//Write object to object stream
				oos.writeObject(BaseStationController.getCM());

				//Analyze command stream response
				if(inFromClient.readLine().equals("OKAY"))
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
			outToClient.println("FAIL");
			System.out.println("Failure sending model to server");
			//e1.printStackTrace();
		}
	}
    
    
    public static void requestModel()
	{
		//Ensure streams have been initialized
		if(outToClient == null || inFromClient == null || ois == null )
		{
			System.out.println("Command or object stream not initialized, server may be off...");
			return;
		}
		
		//Get model from Pi
		try {
			System.out.println("Requesting model...");
			
			//Request model from Pi on command stream
			outToClient.println("REQUESTMODEL");
				
			//Analyze response on command stream
			if(inFromClient.readLine().equals("SENDINGMODEL"))
			{
				System.out.println("Model incoming...");
						
				//Read model off object stream
				BaseStationController.setCM((CurrentModel) ois.readObject());
				
				//Notify server result
				outToClient.println("OKAY");
				
				System.out.println("Model recieved.");
			}
			
		} catch (Exception e1) {
			outToClient.println("FAIL");
			System.out.println("Failure requesting model from server!");
			//e1.printStackTrace();
		}
	} 


    
  //MODEL HANDLING*********************************************
	
  	public static void saveModeltoDisk()
      {
  		FileOutputStream fos = null;
  		ObjectOutputStream oos = null;
  		
      	try 
      	{
      		fos = new FileOutputStream(modelPath);
      		oos = new ObjectOutputStream(fos);
      		oos.writeObject(BaseStationController.getCM());
      		oos.close();
      		fos.close();
      		System.out.println("Model saved to file!");
      	} 
      	catch (Exception e1) 
      	{
      		//Close any open streams
      		try
      		{
  	    		if(fos != null)
  	    		{
  	    			fos.close();
  	    		}
  	    		
  	    		if(oos != null)
  	    		{
  	    			oos.close();
  	    		}
  	    		
  	    		System.out.println("Existing file stream closed.");
  			} 
      		catch (Exception e) 
      		{
  				System.out.println("Failure closeing file stream");
  				e.printStackTrace();
      		}
      		
      		System.out.println("Model not saved to file!");
      		e1.printStackTrace();
      	}	
      }
  	
      private static CurrentModel loadModelfromFile() 
      {
          //De-serialize the model
      	
      	FileInputStream fis = null;
  		ObjectInputStream ois = null;
  		
          try 
          {
          	fis = new FileInputStream(modelPath);
              ois = new ObjectInputStream (fis);
  			CurrentModel result = (CurrentModel)ois.readObject();
  			ois.close();
  			fis.close();
  			System.out.println("Model loaded from file!");
  			return result;
  		} 
          catch (Exception e1) 
          {
          	//Close any open streams
          	try
          	{
          		if(ois != null)
          			{
          		ois.close();
          			}
      		
          		if(fis != null)
          		{
          				fis.close();
          		}
          		System.out.println("Existing file stream closed.");
  			} 
      		catch (Exception e) 
      		{
  				System.out.println("Failure closeing file stream");
  				e.printStackTrace();
      		}
      		
          	System.out.println("Model not loaded from file!");
  			System.out.println("New model created.");
  			return new CurrentModel();
  		}
  	}
	
      
    //NETWORKING PARAMETERS*******************************************************
  	
  	//Command port number
  	public static int getCommandPort() {
  		return commandPort;
  	}
  	
  	//Command port number
  		public static int getObjectPort() {
  			return objectPort;
  		}
  		
  	//ServerSocket
  	public static ServerSocket getCommandServerSocket() {
  		return commandServerSocket;
  	}
  	
  	public static ServerSocket getObjectServerSocket() {
  		return objectServerSocket;
  	}
  	
  	//Sockets
  	public static Socket getCommandSocket() {
  		return commandSocket;
  	}
  	
  	public static Socket getObjectSocket() {
  		return objectSocket;
  	}  	
  	
  	//Input/output TCP Streams
  	//Command stream to client
  	public static PrintWriter getOutToClient() {
  		return outToClient;
  	}

  	//Command stream from client
  	public static BufferedReader getInFromClient() {
  		return inFromClient;
  	}
  	
  	//Object stream to client
  	public static ObjectOutputStream getOOS() {
  		return oos;
  	}

  	//Object stream from client
  	public static ObjectInputStream getOIS() {
  		return ois;
  	}
  	
  	//Model path
  	public static String getModelPath() {
  		return modelPath;
  	}
	
}

		
